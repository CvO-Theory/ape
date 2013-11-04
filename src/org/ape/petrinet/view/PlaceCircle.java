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
import org.ape.petrinet.model.Marking;
import org.ape.petrinet.model.PlaceNode;
import org.ape.util.GraphicsTools;
import org.ape.util.GraphicsTools.HorizontalAlignment;
import org.ape.util.GraphicsTools.VerticalAlignment;

public class PlaceCircle extends GraphicalNode {

	private PlaceNode place;

	public PlaceCircle(PlaceNode place) {
		super(place);
		this.place = place;
	}

	@Override
	public void draw(Graphics g, DrawingOptions drawingOptions) {
		if (place.isStatic()) {
			drawStaticShadow(g);
		}
		drawPlaceBackground(g);
		drawPlaceBorder(g);
		drawLabel(g);
		drawTokens(g, drawingOptions.getMarking());
	}

	protected void drawStaticShadow(Graphics g) {
		g.setColor(place.getColor());
		final int phase = 4;
		g.fillOval(place.getStart().x + phase, place.getStart().y + phase,
				place.getWidth() - 1, place.getHeight() - 1);
	}

	protected void drawPlaceBackground(Graphics g) {
		g.setColor(Color.white);
		g.fillOval(place.getStart().x, place.getStart().y, place.getWidth(),
				place.getHeight());
	}

	protected void drawPlaceBorder(Graphics g) {
		g.setColor(place.getColor());
		g.drawOval(place.getStart().x, place.getStart().y,
				place.getWidth() - 1, place.getHeight() - 1);
	}

	protected void drawTokens(Graphics g, Marking marking) {
		g.setColor(place.getColor());
		int x = place.getCenter().x;
		int y = place.getCenter().y;
		int tokenSpacing = place.getWidth() / 5;
		if (marking.getTokens(place) == 1) {
			drawTokenAsDot(g, x, y);
		} else if (marking.getTokens(place) == 2) {
			drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
		} else if (marking.getTokens(place) == 3) {
			drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
			drawTokenAsDot(g, x, y);
			drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
		} else if (marking.getTokens(place) == 4) {
			drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
		} else if (marking.getTokens(place) == 5) {
			drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
			drawTokenAsDot(g, x, y);
			drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
		} else if (marking.getTokens(place) == 6) {
			drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x - tokenSpacing, y);
			drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y);
			drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
		} else if (marking.getTokens(place) == 7) {
			drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x - tokenSpacing, y);
			drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
			drawTokenAsDot(g, x, y);
			drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y);
			drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
		} else if (marking.getTokens(place) == 8) {
			drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x - tokenSpacing, y);
			drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
			drawTokenAsDot(g, x, y - tokenSpacing);
			drawTokenAsDot(g, x, y + tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y);
			drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
		} else if (marking.getTokens(place) == 9) {
			drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x - tokenSpacing, y);
			drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
			drawTokenAsDot(g, x, y - tokenSpacing);
			drawTokenAsDot(g, x, y);
			drawTokenAsDot(g, x, y + tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
			drawTokenAsDot(g, x + tokenSpacing, y);
			drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
		} else if (marking.getTokens(place) > 9) {
			GraphicsTools.drawString(g,
					Integer.toString(marking.getTokens(place)), x, y,
					HorizontalAlignment.center, VerticalAlignment.center);
		}
	}

	private void drawTokenAsDot(Graphics g, int x, int y) {
		final int tokenSize = place.getWidth() / 6;
		g.fillOval(x - tokenSize / 2, y - tokenSize / 2, tokenSize, tokenSize);
	}

}
