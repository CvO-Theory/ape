/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * Copyright (C) 2013 Hillit Saathoff <mail at hillit.de>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ape.editor.controller;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.ape.editor.commands.AddArcCommand;
import org.ape.editor.commands.AddReferenceArcCommand;
import org.ape.editor.commands.SetArcMultiplicityCommand;
import org.ape.petrinet.model.Arc;
import org.ape.petrinet.model.ArcEdge;
import org.ape.petrinet.model.Element;
import org.ape.petrinet.model.Node;
import org.ape.petrinet.model.PlaceNode;
import org.ape.petrinet.model.ReferenceArc;
import org.ape.petrinet.model.Subnet;
import org.ape.petrinet.model.Transition;
import org.ape.petrinet.model.TransitionNode;
import org.ape.util.CollectionTools;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ArcFeature extends Feature {

	public ArcFeature(Root root) {
		super(root);
	}

	private Element sourceElement = null;
	private Arc connectingArc = null;
	private List<Element> backgroundElements = new ArrayList<Element>();
	private boolean started = false;
	private Subnet currentSubnet;

	public void mousePressed(MouseEvent event) {
		super.mousePressed(event);
		int mouseButton = event.getButton();

		if (mouseButton == MouseEvent.BUTTON1
				&& APE.getRoot().getClickedElement() instanceof Node
				&& !started) {
			sourceElement = APE.getRoot().getDocument().petriNet
					.getCurrentSubnet().getElementByXY(getPressedX(),
							getPressedY());
			connectingArc = new Arc((Node) sourceElement);
			backgroundElements.add(connectingArc);
			started = true;
			currentSubnet = APE.getRoot().getDocument().petriNet
					.getCurrentSubnet();
		} else if (event.getButton() == MouseEvent.BUTTON3) {
			if (!APE.getRoot().getSelection()
					.contains(APE.getRoot().getClickedElement())) {
				APE.getRoot().getSelection().clear();
			}
			showPopup();
		}
	}

	public void mouseDragged(MouseEvent event) {
		super.mouseDragged(event);
		if (APE.getRoot().getDocument().petriNet.getCurrentSubnet() != currentSubnet) {
			cancelDragging();
		}

		Element targetElement = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet()
				.getElementByXY(getDraggedX(), getDraggedY());

		if (started) {
			if (targetElement != null
					&& (sourceElement instanceof PlaceNode
							&& targetElement instanceof TransitionNode || sourceElement instanceof TransitionNode
							&& targetElement instanceof PlaceNode)) {
				connectingArc.setEnd(targetElement.getCenter().x,
						targetElement.getCenter().y);
				connectingArc.setDestination((Node) targetElement);
			} else {
				connectingArc.setEnd(getDraggedX(), getDraggedY());
				connectingArc.setSource(null);
				connectingArc.setDestination(null);
			}
			APE.getRoot().canvas.repaint();
		}
	}

	public void mouseMoved(MouseEvent event) {
		super.mouseMoved(event);
		mouseDragged(event);
	}

	public void mouseReleased(MouseEvent event) {
		super.mouseReleased(event);
		if (APE.getRoot().getDocument().petriNet.getCurrentSubnet() != currentSubnet) {
			cancelDragging();
		}
		Element targetElement = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getElementByXY(getReleasedX(),
						getReleasedY());

		if (started) {
			connectingArc.setEnd(getReleasedX(), getReleasedY());
			if (sourceElement != targetElement) {
				if (targetElement != null) {
					if (sourceElement instanceof PlaceNode
							&& targetElement instanceof TransitionNode
							|| sourceElement instanceof TransitionNode
							&& targetElement instanceof PlaceNode) {
						boolean placeToTransition = sourceElement instanceof PlaceNode
								&& targetElement instanceof TransitionNode;
						PlaceNode placeNode;
						TransitionNode transitionNode;
						if (placeToTransition) {
							placeNode = (PlaceNode) sourceElement;
							transitionNode = (TransitionNode) targetElement;
						} else {
							transitionNode = (TransitionNode) sourceElement;
							placeNode = (PlaceNode) targetElement;
						}

						ArcEdge arcEdge = APE.getRoot().getDocument().petriNet
								.getCurrentSubnet().getArcEdge(placeNode,
										transitionNode, placeToTransition);
						ArcEdge counterArcEdge = APE.getRoot().getDocument().petriNet
								.getCurrentSubnet().getArcEdge(placeNode,
										transitionNode, !placeToTransition);
						if (counterArcEdge instanceof ReferenceArc) {
							// never attempt make arc in opposite direction of
							// ReferenceArc
						} else if (arcEdge == null) {
							// is there is no arc go ahead
							if (transitionNode instanceof Transition) {
								APE.getRoot()
										.getUndoManager()
										.executeCommand(
												new AddArcCommand(
														placeNode,
														(Transition) transitionNode,
														placeToTransition));
							} else if (transitionNode instanceof Subnet) {
								APE.getRoot()
										.getUndoManager()
										.executeCommand(
												new AddReferenceArcCommand(
														placeNode,
														(Subnet) transitionNode,
														APE.getRoot()
																.getDocument().petriNet));
							} else {
								throw new RuntimeException(
										"transitionNode not instanceof Transition neither Subnet");
							}

							// newly created arcs are always first in subnet
							APE.getRoot().setClickedElement(
									CollectionTools.getFirstElement(APE
											.getRoot().getDocument().petriNet
											.getCurrentSubnet().getElements()));
						} else if (!(arcEdge instanceof ReferenceArc)) {
							Arc arc = (Arc) arcEdge;
							// increase multiplicity
							// but only if there is no ReferenceArc
							APE.getRoot()
									.getUndoManager()
									.executeCommand(
											new SetArcMultiplicityCommand(arc,
													arc.getMultiplicity() + 1));
							APE.getRoot().setClickedElement(arcEdge);
						}
					}
				}
				cancelDragging();
			}
		}
	}

	public void setHoverEffects(int x, int y) {
		super.setHoverEffects(x, y);
		Element targetElement = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getElementByXY(x, y);
		if (started) { // Connecting to something...
			if (targetElement == null) { // Connecting to air
				getRoot().canvas.getHighlightedElements().add(sourceElement);
				sourceElement.highlightColor = Colors.pointingColor;
				APE.getRoot().canvas.repaint();
			} else { // Connecting to solid element
				if (sourceElement instanceof PlaceNode
						&& targetElement instanceof TransitionNode
						|| sourceElement instanceof TransitionNode
						&& targetElement instanceof PlaceNode) {
					getRoot().canvas.getHighlightedElements()
							.add(sourceElement);
					getRoot().canvas.getHighlightedElements()
							.add(targetElement);
					sourceElement.highlightColor = Colors.connectingColor;
					targetElement.highlightColor = Colors.connectingColor;
					APE.getRoot().canvas.repaint();
				} else if (sourceElement == targetElement) {
					getRoot().canvas.getHighlightedElements()
							.add(sourceElement);
					sourceElement.highlightColor = Colors.pointingColor;
					APE.getRoot().canvas.repaint();
				} else if (targetElement instanceof Node) { // Wrong combination
					getRoot().canvas.getHighlightedElements()
							.add(sourceElement);
					getRoot().canvas.getHighlightedElements()
							.add(targetElement);
					sourceElement.highlightColor = Colors.disallowedColor;
					targetElement.highlightColor = Colors.disallowedColor;
					APE.getRoot().canvas.repaint();
				}
			}
		} else {
			if (targetElement != null) {
				getRoot().canvas.getHighlightedElements().add(targetElement);
				targetElement.highlightColor = Colors.pointingColor;
				APE.getRoot().canvas.repaint();
			}
		}
	}

	public void drawBackground(Graphics g) {
		for (Element element : backgroundElements) {
			element.getGraphicalElement().draw(g, null);
		}
	}

	private void cancelDragging() {
		sourceElement = null;
		backgroundElements.remove(connectingArc);
		started = false;
		APE.getRoot().canvas.repaint();
	}
}
