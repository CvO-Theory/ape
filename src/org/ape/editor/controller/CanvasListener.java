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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class CanvasListener implements MouseListener, MouseMotionListener,
		MouseWheelListener {

	private Root root;

	public CanvasListener(Root root) {
		this.root = root;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() == 1) {
			if (APE.getRoot().isSelectedTool_Place())
				APE.getRoot().selectTool_Transition();
			else if (APE.getRoot().isSelectedTool_Transition())
				APE.getRoot().selectTool_Arc();
			else if (APE.getRoot().isSelectedTool_Arc())
				APE.getRoot().selectTool_Token();
			else if (APE.getRoot().isSelectedTool_Token())
				APE.getRoot().selectTool_Place();
			else
				APE.getRoot().selectTool_Place();
		} else if (e.getWheelRotation() == -1) {
			if (APE.getRoot().isSelectedTool_Place())
				APE.getRoot().selectTool_Token();
			else if (APE.getRoot().isSelectedTool_Transition())
				APE.getRoot().selectTool_Place();
			else if (APE.getRoot().isSelectedTool_Arc())
				APE.getRoot().selectTool_Transition();
			else if (APE.getRoot().isSelectedTool_Token())
				APE.getRoot().selectTool_Arc();
			else
				APE.getRoot().selectTool_Token();
		}
		root.canvas.repaint();
		root.canvas.setHoverEffects(e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent event) {
		//

		root.getActiveFeature().mousePressed(event);

	}

	public void mouseDragged(MouseEvent event) {

		root.getActiveFeature().mouseDragged(event);

	}

	public void mouseReleased(MouseEvent event) {

		root.getActiveFeature().mouseReleased(event);

	}

	public void mouseMoved(MouseEvent evt) {

		root.getActiveFeature().mouseMoved(evt);
	}

	void setCursor(int x, int y) {

		root.getActiveFeature().setCursor(x, y);

	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	public void mouseClicked(MouseEvent evt) {
	}

}
