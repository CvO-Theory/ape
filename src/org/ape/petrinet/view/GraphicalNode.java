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

package org.ape.petrinet.view;

import java.awt.Graphics;

import org.ape.petrinet.model.DrawingOptions;
import org.ape.petrinet.model.Node;
import org.ape.util.GraphicsTools;
import org.ape.util.GraphicsTools.HorizontalAlignment;
import org.ape.util.GraphicsTools.VerticalAlignment;

public class GraphicalNode extends GraphicalElement {

	private Node node;

	public GraphicalNode(Node node) {
		this.node = node;
	}

	@Override
	public void draw(Graphics g, DrawingOptions drawingOptions) {

	}

	protected void drawLabel(Graphics g) {
		if (node.getLabel() != null && !node.getLabel().equals("")) {
			GraphicsTools.drawString(g, node.getLabel(), node.getCenter().x,
					node.getEnd().y, HorizontalAlignment.center,
					VerticalAlignment.top);
		}
	}

}
