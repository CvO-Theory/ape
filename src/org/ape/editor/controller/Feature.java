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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.ape.editor.commands.SetEdgeZigzagPointCommand;
import org.ape.petrinet.model.ArcEdge;
import org.ape.petrinet.model.DrawingOptions;
import org.ape.petrinet.model.Edge;
import org.ape.petrinet.model.Element;
import org.ape.petrinet.model.PlaceNode;
import org.ape.petrinet.model.Subnet;
import org.ape.petrinet.model.Transition;
import org.ape.petrinet.model.VisualHandle;
import org.ape.petrinet.model.VisualSelection;
import org.ape.util.FeatureType;
import org.ape.util.GraphicsTools;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class Feature {

	private int pressedX = 0;
	private int pressedY = 0;
	private int draggedX = 0;
	private int draggedY = 0;
	private int releasedX = 0;
	private int releasedY = 0;
	private int movedX = 0;
	private int movedY = 0;
	private Root root;

	private VisualHandle visualHandle = new VisualHandle();
	private Point activeBreakPoint;
	private boolean started;
	private List<Point> oldBreakPoints;
	private List<Element> foregroundVisualElements = new ArrayList<Element>();
	private VisualSelection visualSelection = new VisualSelection();

	public Feature(Root root) {
		this.root = root;
		visualHandle.color = Colors.pointingColor;
		visualHandle.setSize(ArcEdge.nearTolerance, ArcEdge.nearTolerance);
	}

	public Feature() {
		// TODO Auto-generated constructor stub
	}

	public void drawForeground(Graphics g) {
		for (Element element : foregroundVisualElements) {
			element.getGraphicalElement().draw(g, null);
		}
		getVisualSelection().getGraphicalElement().draw(g, null);

	}

	public void drawMainLayer(Graphics g) {
		for (Element element : root.getDocument().petriNet.getCurrentSubnet()
				.getElements()) {
			DrawingOptions drawingOptions = new DrawingOptions();
			drawingOptions.setMarking(APE.getRoot().getCurrentMarking());
			if (element.highlightColor != null) {
				Color previousColor = element.getColor();

				element.setColor(element.highlightColor);
				element.getGraphicalElement().draw(g, drawingOptions); // TODO

				element.setColor(previousColor);
			} else {
				element.getGraphicalElement().draw(g, drawingOptions); // TODO
			}
		}
	}

	public abstract void drawBackground(Graphics g);

	public void mousePressed(MouseEvent event) {
		Point translation = root.canvas.getViewTranslation();
		pressedX = event.getX() + translation.x;
		pressedY = event.getY() + translation.y;

		APE.getRoot().setClickedElement(
				APE.getRoot().getDocument().petriNet.getCurrentSubnet()
						.getElementByXY(pressedX, pressedY));

		if (event.getButton() == MouseEvent.BUTTON3) {
			if (APE.getRoot().getClickedElement() == null) { // The user did not
																// click on a
																// shape.
				APE.getRoot().getActiveFeatureType()
						.setActiveFeature(FeatureType.SELECT);
			}
		} else if (event.getButton() == MouseEvent.BUTTON1
				&& APE.getRoot().getClickedElement() instanceof ArcEdge) {
			if (!APE.getRoot().getSelection()
					.contains(APE.getRoot().getClickedElement())) {
				APE.getRoot().getSelection().clear();
			}
			Edge edge = (Edge) APE.getRoot().getClickedElement();

			oldBreakPoints = edge.getBreakPointsCopy();
			activeBreakPoint = edge.addOrGetBreakPoint(new Point(pressedX,
					pressedY));
			started = true;
		}
		if (event.getClickCount() == 2) {
			if (APE.getRoot().getClickedElement() instanceof Subnet) {
				APE.getRoot().openSubnet();
			} else if (APE.getRoot().getClickedElement() == null) {
				APE.getRoot().closeSubnet();
			}
		}

		setCursor(pressedX, pressedY);
		setHoverEffects(pressedX, pressedY);
	}

	public void showPopup() {
		int positionX = pressedX - 10;
		int positionY = pressedY - 2;
		if (APE.getRoot().getClickedElement() instanceof PlaceNode) {
			APE.getRoot().getPlacePopup()
					.show(root.canvas, positionX, positionY);

		} else if (APE.getRoot().getClickedElement() instanceof Subnet) {
			APE.getRoot().getSubnetPopup()
					.show(root.canvas, positionX, positionY);
		} else if (APE.getRoot().getClickedElement() instanceof Transition) {
			APE.getRoot().getTransitionPopup()
					.show(root.canvas, positionX, positionY);
		} else if (APE.getRoot().getClickedElement() instanceof ArcEdge) {
			APE.getRoot().getArcEdgePopup()
					.show(root.canvas, positionX, positionY);
		} else {
			root.getCanvasPopup().show(root.canvas, positionX, positionY);
		}

	}

	public void mouseDragged(MouseEvent event) {
		Point translation = root.canvas.getViewTranslation();
		draggedX = event.getX() + translation.x;
		draggedY = event.getY() + translation.y;

		if (started) {
			activeBreakPoint.move(draggedX, draggedY);
			root.canvas.repaint();
		}

		setHoverEffects(draggedX, draggedY);
	}

	public void mouseReleased(MouseEvent event) {
		Point translation = root.canvas.getViewTranslation();
		releasedX = event.getX() + translation.x;
		releasedY = event.getY() + translation.y;

		if (started) {
			Edge edge = (Edge) root.getClickedElement();
			edge.cleanupUnecessaryBreakPoints();

			boolean change = false;
			if (oldBreakPoints.size() != edge.getBreakPoints().size()) {
				change = true;
			} else {
				for (int i = 0; i < edge.getBreakPoints().size(); i++) {
					if (!edge.getBreakPoints().get(i)
							.equals(oldBreakPoints.get(i))) {
						change = true;
						break;
					}
				}
			}
			if (!change) {
				edge.setBreakPoints(oldBreakPoints);
				Point targetLocation = new Point(releasedX, releasedY);
				APE.getRoot()
						.getUndoManager()
						.executeCommand(
								new SetEdgeZigzagPointCommand(edge, new Point(
										pressedX, pressedX), targetLocation));
			}
			started = false;
		}

		setHoverEffects(releasedX, releasedY);
		setCursor(releasedX, releasedY);
	}

	public void mouseMoved(MouseEvent event) {
		Point translation = root.canvas.getViewTranslation();
		movedX = event.getX() + translation.x;
		movedY = event.getY() + translation.y;

		setHoverEffects(movedX, movedY);
		setCursor(movedX, movedY);
	}

	public void setHoverEffects(int x, int y) {
		if (!root.canvas.getHighlightedElements().isEmpty()) {
			for (Element element : root.canvas.getHighlightedElements()) {
				element.highlightColor = null;
			}
			root.canvas.getHighlightedElements().clear();
			root.canvas.repaint();
		}

		for (Element selectedElement : APE.getRoot().getSelection()) {
			root.canvas.getHighlightedElements().add(selectedElement);
			selectedElement.highlightColor = Colors.selectedColor;
		}

		Element element = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getElementByXY(x, y);
		boolean drawHandle = false;
		if (element instanceof ArcEdge) {
			ArcEdge anArc = (ArcEdge) element;
			final Point mousePos = new Point(x, y);
			for (Point breakPoint : anArc.getBreakPoints()) {
				if (GraphicsTools.isPointNearPoint(breakPoint, mousePos,
						ArcEdge.nearTolerance)) {
					if (!foregroundVisualElements.contains(visualHandle)) {
						foregroundVisualElements.add(visualHandle);
					}
					visualHandle.setCenter(breakPoint.x, breakPoint.y);
					drawHandle = true;

					break;
				}
			}
		}
		if (!drawHandle) {
			foregroundVisualElements.remove(visualHandle);
		}

		if (element != null) {
			root.canvas.getHighlightedElements().add(element);
			element.highlightColor = Colors.pointingColor;
			// TODO: hier einfügen, dass die Hervorhebung anders funktioniert.
			root.canvas.repaint();
		}
	}

	public void setCursor(int x, int y) {
		getRoot().canvas.setAlternativeCursor(null);

	}

	public Root getRoot() {
		return root;
	}

	public void setRoot(Root root) {
		this.root = root;
	}

	public int getPressedX() {
		return pressedX;
	}

	public void setPressedX(int pressedX) {
		this.pressedX = pressedX;
	}

	public int getPressedY() {
		return pressedY;
	}

	public void setPressedY(int pressedY) {
		this.pressedY = pressedY;
	}

	public int getDraggedX() {
		return draggedX;
	}

	public void setDraggedX(int draggedX) {
		this.draggedX = draggedX;
	}

	public int getDraggedY() {
		return draggedY;
	}

	public void setDraggedY(int draggedY) {
		this.draggedY = draggedY;
	}

	public int getReleasedX() {
		return releasedX;
	}

	public void setReleasedX(int releasedX) {
		this.releasedX = releasedX;
	}

	public int getReleasedY() {
		return releasedY;
	}

	public void setReleasedY(int releasedY) {
		this.releasedY = releasedY;
	}

	public int getMovedX() {
		return movedX;
	}

	public void setMovedX(int movedX) {
		this.movedX = movedX;
	}

	public int getMovedY() {
		return movedY;
	}

	public void setMovedY(int movedY) {
		this.movedY = movedY;
	}

	public VisualSelection getVisualSelection() {
		return visualSelection;
	}

	public void setVisualSelection(VisualSelection visualSelection) {
		this.visualSelection = visualSelection;
	}
}
