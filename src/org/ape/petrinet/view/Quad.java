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

import java.awt.Color;
import java.awt.Graphics;

import org.ape.petrinet.model.DrawingOptions;
import org.ape.petrinet.model.Transition;
import org.ape.util.GraphicsTools;
import org.ape.util.GraphicsTools.HorizontalAlignment;
import org.ape.util.GraphicsTools.VerticalAlignment;

public class Quad extends GraphicalNode {

	private Transition transition;

	public Quad(Transition transition) {
		super(transition);
		this.transition = transition;
	}

	@Override
	public void draw(Graphics g, DrawingOptions drawingOptions) {
		g.setColor(Color.white);
		g.fillRect(transition.getStart().x, transition.getStart().y,
				transition.getWidth(), transition.getHeight());
		g.setColor(transition.color);
		g.drawRect(transition.getStart().x, transition.getStart().y,
				transition.getWidth() - 1, transition.getHeight() - 1);
		drawLabel(g);

	}

	@Override
	protected void drawLabel(Graphics g) {
		if (transition.getLabel() != null && !transition.getLabel().equals("")) {
			// GraphicsTools.drawString(g, getLabel(), getCenter().x,
			// getCenter().y, HorizontalAlignment.center,
			// VerticalAlignment.center, new Font("Times", Font.BOLD, 24));
			GraphicsTools.drawString(g, transition.getLabel(),
					transition.getCenter().x, transition.getCenter().y,
					HorizontalAlignment.center, VerticalAlignment.center);
		}
		// GraphicsTools.drawString(g, getId(), getCenter().x, getStart().y,
		// HorizontalAlignment.center, VerticalAlignment.bottom);
	}

}
