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

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.ape.editor.commands.AddTokenCommand;
import org.ape.editor.commands.FireTransitionCommand;
import org.ape.editor.commands.RemoveTokenCommand;
import org.ape.petrinet.model.Element;
import org.ape.petrinet.model.Marking;
import org.ape.petrinet.model.PlaceNode;
import org.ape.petrinet.model.Transition;
import org.ape.util.GraphicsTools;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class TokenFeature extends Feature {

	private Cursor tokenCursor;
	private Cursor fireCursor;

	public TokenFeature(Root root) {
		super(root);
		tokenCursor = GraphicsTools.getCursor("ape/canvas/token.gif",
				new Point(16, 0));
		fireCursor = GraphicsTools.getCursor("ape/canvas/fire.gif", new Point(
				16, 0));
	}

	public void mousePressed(MouseEvent event) {
		super.mousePressed(event);
		int mouseButton = event.getButton();
		Marking initialMarking = APE.getRoot().getCurrentMarking();
		Element targetElement = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet()
				.getElementByXY(getPressedX(), getPressedY());

		if (targetElement instanceof PlaceNode) {
			PlaceNode placeNode = (PlaceNode) targetElement;
			if (mouseButton == MouseEvent.BUTTON1) {
				APE.getRoot()
						.getUndoManager()
						.executeCommand(
								new AddTokenCommand(placeNode, initialMarking));
			} else if (mouseButton == MouseEvent.BUTTON3) {
				if (initialMarking.getTokens(placeNode) > 0) {
					APE.getRoot()
							.getUndoManager()
							.executeCommand(
									new RemoveTokenCommand(placeNode,
											initialMarking));
				}
			}
		} else if (targetElement instanceof Transition) {
			Transition transition = (Transition) targetElement;
			if (mouseButton == MouseEvent.BUTTON1) {
				if (initialMarking.isEnabled(transition)) {
					APE.getRoot()
							.getUndoManager()
							.executeCommand(
									new FireTransitionCommand(transition,
											initialMarking));
				}
			}
		}

	}

	public void setHoverEffects(int x, int y) {
		super.setHoverEffects(x, y);
		Element targetElement = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getElementByXY(x, y);
		Marking initialMarking = APE.getRoot().getCurrentMarking();

		if (targetElement instanceof PlaceNode) {
			getRoot().canvas.getHighlightedElements().add(targetElement);
			targetElement.highlightColor = Colors.pointingColor;
			getRoot().canvas.repaint();
		} else if (targetElement instanceof Transition) {
			if (initialMarking.isEnabled((Transition) targetElement)) {
				getRoot().canvas.getHighlightedElements().add(targetElement);
				targetElement.highlightColor = Colors.permittedColor;
				getRoot().canvas.repaint();
			} else {
				getRoot().canvas.getHighlightedElements().add(targetElement);
				targetElement.highlightColor = Colors.disallowedColor;
				getRoot().canvas.repaint();
			}
		}
	}

	public void drawForeground(Graphics g) {
		super.drawForeground(g);
		Marking initialMarking = APE.getRoot().getCurrentMarking();

		for (Element element : APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getElements()) {
			if (element instanceof Transition) {
				Transition transition = (Transition) element;
				if (initialMarking.isEnabled(transition)) {
					g.setColor(Colors.permittedColor);
				} else {
					g.setColor(Colors.disallowedColor);
				}
				((Graphics2D) g).setStroke(new BasicStroke(2f));
				g.drawRect(transition.getStart().x + 1,
						transition.getStart().y + 1, transition.getWidth() - 3,
						transition.getHeight() - 3);
				((Graphics2D) g).setStroke(new BasicStroke(1f));
			}
		}
	}

	public void setCursor(int x, int y) {
		super.setCursor(x, y);
		Element targetElement = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getElementByXY(x, y);

		if (targetElement instanceof PlaceNode) {

			getRoot().canvas.setAlternativeCursor(tokenCursor);
		} else if (targetElement instanceof Transition) {
			getRoot().canvas.setAlternativeCursor(fireCursor);
		}

	}

	@Override
	public void drawBackground(Graphics g) {

	}

}
