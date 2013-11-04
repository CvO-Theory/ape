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

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.ape.editor.commands.AddPlaceCommand;
import org.ape.editor.commands.MoveElementCommand;
import org.ape.editor.commands.MoveElementsCommand;
import org.ape.petrinet.model.Element;
import org.ape.petrinet.model.Node;
import org.ape.util.CollectionTools;

public class PlaceFeature extends Feature {

	private Element draggedElement;
	private Point deltaPosition;
	private int prevDragX;
	private int prevDragY;

	public PlaceFeature(Root root) {
		super(root);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void mousePressed(MouseEvent event) {
		super.mousePressed(event);
		int mouseButton = event.getButton();
		boolean doubleclick = event.getClickCount() == 2;

		if (!doubleclick) {
			if (mouseButton == MouseEvent.BUTTON1) {
				if (APE.getRoot().getClickedElement() instanceof Node) {
					if (!APE.getRoot().getSelection()
							.contains(APE.getRoot().getClickedElement())) {
						APE.getRoot().getSelection().clear();
					}

					draggedElement = APE.getRoot().getDocument().petriNet
							.getCurrentSubnet().getElementByXY(getPressedX(),
									getPressedY());
					deltaPosition = new Point();
					prevDragX = getPressedX();
					prevDragY = getPressedY();
				} else if (APE.getRoot().getClickedElement() == null) {
					APE.getRoot().getSelection().clear();
					APE.getRoot()
							.getUndoManager()
							.executeCommand(
									new AddPlaceCommand(APE.getRoot()
											.getDocument().petriNet
											.getCurrentSubnet(), getPressedX(),
											getPressedY(), APE.getRoot()
													.getDocument().petriNet));
					APE.getRoot().setClickedElement(
							CollectionTools.getLastElement(APE.getRoot()
									.getDocument().petriNet.getCurrentSubnet()
									.getElements()));
				}
			} else if (event.getButton() == MouseEvent.BUTTON3) {
				if (!APE.getRoot().getSelection()
						.contains(APE.getRoot().getClickedElement())) {
					APE.getRoot().getSelection().clear();
				}
				showPopup();
			}

		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		super.mouseDragged(event);
		if (draggedElement != null) {
			doTheMoving(getPressedX(), getPressedY());
			getRoot().canvas.repaint(); // redraw canvas to show shape in new
										// position
			deltaPosition.translate(getDraggedX() - prevDragX, getDraggedY()
					- prevDragY);
			prevDragX = getDraggedX();
			prevDragY = getDraggedY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		super.mouseReleased(event);
		if (draggedElement != null) {
			doTheMoving(getReleasedX(), getReleasedY());
			deltaPosition.translate(getReleasedX() - prevDragX, getReleasedY()
					- prevDragY);
			saveTheMoving();
			getRoot().canvas.repaint();
			draggedElement = null; // Dragging is finished.
		}
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		super.mouseMoved(event);

	}

	@Override
	public void setHoverEffects(int x, int y) {
		super.setHoverEffects(x, y);

	}

	@Override
	public void setCursor(int x, int y) {
		super.setCursor(x, y);
		Element element = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getElementByXY(x, y);

		if (element != null && element instanceof Node) {
			getRoot().canvas.setAlternativeCursor(Cursor
					.getPredefinedCursor(Cursor.MOVE_CURSOR));

		}
	}

	private void doTheMoving(int mouseX, int mouseY) {
		if (!APE.getRoot().getSelection().isEmpty()) {
			for (Element selectedElement : APE.getRoot().getSelection()) {
				selectedElement.moveBy(mouseX - prevDragX, mouseY - prevDragY);
			}
		} else {
			draggedElement.moveBy(mouseX - prevDragX, mouseY - prevDragY);
		}
	}

	private void saveTheMoving() {
		if (!deltaPosition.equals(new Point(0, 0))) {
			if (!APE.getRoot().getSelection().isEmpty()) {
				for (Element selectedElement : APE.getRoot().getSelection()) {
					selectedElement.moveBy(-deltaPosition.x, -deltaPosition.y); // move
																				// back
																				// to
																				// original
																				// positions
				}
				APE.getRoot()
						.getUndoManager()
						.executeCommand(
								new MoveElementsCommand(APE.getRoot()
										.getSelection().getElements(),
										deltaPosition));
			} else {
				draggedElement.moveBy(-deltaPosition.x, -deltaPosition.y); // move
																			// back
																			// to
																			// original
																			// position
				APE.getRoot()
						.getUndoManager()
						.executeCommand(
								new MoveElementCommand(draggedElement,
										deltaPosition));
			}
		}
	}

	@Override
	public void drawBackground(Graphics g) {

	}

}
