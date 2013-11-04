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
import java.awt.Point;

import org.ape.petrinet.model.Arc;
import org.ape.petrinet.model.DrawingOptions;
import org.ape.petrinet.model.ReferenceArc;
import org.ape.petrinet.model.ReferencePlace;
import org.ape.util.GraphicsTools;

public class GraphicalReferenceArc extends GraphicalArc {

	public GraphicalReferenceArc(ReferenceArc referenceArc) {
		super(referenceArc);
	}

	@Override
	public void draw(Graphics g, DrawingOptions drawingOptions) {
		ReferenceArc refArc = (ReferenceArc) getArc();
		ReferencePlace referencePlace = refArc.getReferencePlace();
		if (referencePlace.getConnectedTransitionNodes().size() == 1) {
			g.setColor(refArc.color);
			GraphicsTools.setDashedStroke(g);
			drawSegmentedLine(g);

			for (Arc arc : referencePlace.getConnectedArcs()) { // TODO: also
																// referenceArcs
				refArc.setPlaceToTransition(arc.isPlaceToTransition());
				Point arrowTip = computeArrowTipPoint();
				drawArrow(g, arrowTip);

				if (referencePlace.getConnectedArcEdges().size() > 1
						|| arc.getMultiplicity() > 1) {
					drawMultiplicityLabel(g, arrowTip, arc.getMultiplicity());
				}
			}
			GraphicsTools.setDefaultStroke(g);
		} else if (referencePlace.getConnectedTransitionNodes().isEmpty()) {
			GraphicsTools.setDottedStroke(g);
			drawSegmentedLine(g);
			GraphicsTools.setDefaultStroke(g);
		} else {
			GraphicsTools.setDashedStroke(g);
			drawSegmentedLine(g);
			GraphicsTools.setDefaultStroke(g);
		}
	}

}
