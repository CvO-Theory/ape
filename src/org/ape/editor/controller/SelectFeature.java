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
import java.util.HashSet;
import java.util.Set;

import org.ape.editor.commands.MoveElementCommand;
import org.ape.editor.commands.MoveElementsCommand;
import org.ape.petrinet.model.Element;
import org.ape.petrinet.model.Node;

public class SelectFeature extends Feature {

	private Element draggedElement;
	private Point deltaPosition;
	private int prevDragX;
	private int prevDragY;
	private boolean selecting = false;
	private Set<Element> previousSelection = new HashSet<Element>();

	public SelectFeature(Root root) {
		super(root);
		
	}

	

	@Override
	public void mousePressed(MouseEvent event) {
		super.mousePressed(event);
		int mouseButton = event.getButton();
		boolean doubleclick = event.getClickCount() == 2;

		if (!doubleclick) {
			if (mouseButton == MouseEvent.BUTTON1
					&& APE.getRoot().getClickedElement() instanceof Node) {
				if (!APE.getRoot().getSelection()
						.contains(APE.getRoot().getClickedElement())) {
					APE.getRoot().getSelection().clear();
				}

				draggedElement = APE.getRoot().getDocument().petriNet
						.getCurrentSubnet().getElementByXY(getPressedX(), getPressedY());
				deltaPosition = new Point();
				prevDragX = getPressedX();
				prevDragY = getPressedY();
				
			} else if (mouseButton == MouseEvent.BUTTON1
					&& APE.getRoot().getClickedElement() == null) {
				selecting = true;
				getVisualSelection().setStart(getPressedX(), getPressedY());
				getVisualSelection().setEnd(getPressedX(), getPressedY());
				getRoot().canvas.repaint();
				if (event.isShiftDown()) {
					previousSelection.addAll(APE.getRoot().getSelection()
							.getElements());
				} else {
					APE.getRoot().getSelection().clear();
					previousSelection.clear();
				}
			} else if (event.getButton() == MouseEvent.BUTTON3) {
				if (!APE.getRoot().getSelection().contains(APE.getRoot().getClickedElement())) {
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
			doTheMoving(getDraggedX(), getDraggedY());
			getRoot().canvas.repaint(); // redraw canvas to show shape in new
										// position
			deltaPosition.translate(getDraggedX() - prevDragX, getDraggedY()
					- prevDragY);
			prevDragX = getDraggedX();
			prevDragY = getDraggedY();
		} else if (selecting) {
			getVisualSelection().setEnd(getDraggedX(), getDraggedY());
			getRoot().canvas.repaint();
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
		} else if (selecting) {
			selecting = false;
			getRoot().canvas.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		super.mouseMoved(event);

	}

	@Override
	public void setHoverEffects(int x, int y) {
		super.setHoverEffects(x, y);
		if (selecting) {
			APE.getRoot().getSelection().clear();
			APE.getRoot().getSelection().addAll(previousSelection);
			for (Element visualElement : APE.getRoot().getDocument().petriNet
					.getCurrentSubnet().getElements()) {
				if (getVisualSelection().containsPoint(visualElement.getCenter().x,
						visualElement.getCenter().y)) {
					addElementToSelection(visualElement);
				}
			}
			getRoot().canvas.repaint();
		}
	}

	private void addElementToSelection(Element element) {
		getRoot().canvas.getHighlightedElements().add(element);
		element.highlightColor = Colors.selectedColor;

		APE.getRoot().getSelection().add(element);
	}

	@Override
	public void setCursor(int x, int y) {
		super.setCursor(x, y);
		Element element = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getElementByXY(x, y);

		if (element instanceof Node) {
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
				for (Element selectedElement : APE.getRoot()
						.getSelection()) {
					// move back to original positions
					selectedElement.moveBy(-deltaPosition.x, -deltaPosition.y);
				}
				APE.getRoot()
						.getUndoManager()
						.executeCommand(
								new MoveElementsCommand(APE.getRoot()
										.getSelection().getElements(),
										deltaPosition));
			} else {
				// move back to original positions
				draggedElement.moveBy(-deltaPosition.x, -deltaPosition.y);
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
