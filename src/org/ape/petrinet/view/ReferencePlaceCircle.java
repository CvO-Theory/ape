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

import org.ape.petrinet.model.PlaceNode;
import org.ape.util.GraphicsTools;

public class ReferencePlaceCircle extends PlaceCircle {

	public ReferencePlaceCircle(PlaceNode place) {
		super(place);
	}

	@Override
	protected void drawPlaceBorder(Graphics g) {
		GraphicsTools.setDashedStroke(g);
		super.drawPlaceBorder(g);
		GraphicsTools.setDefaultStroke(g);
	}

}
