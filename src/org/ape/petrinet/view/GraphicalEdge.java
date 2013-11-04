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
import java.util.List;

import org.ape.petrinet.model.ArcEdge;
import org.ape.util.GraphicsTools;
import org.ape.util.GraphicsTools.HorizontalAlignment;
import org.ape.util.GraphicsTools.VerticalAlignment;

public abstract class GraphicalEdge extends GraphicalElement {

	public static final int NEAR_TOLERANCE = 10;

	private ArcEdge arc;

	public GraphicalEdge(ArcEdge arc2) {
		this.setArc(arc2);
	}

	protected Point getLabelPoint(Point arrowTip) {
		final Point labelPoint = new Point();
		List<Point> breakPoints = arc.getBreakPoints();
		if (getArc().getBreakPoints().isEmpty()) {
			labelPoint.x = getStart().x + (arrowTip.x - getStart().x) * 2 / 3;
			labelPoint.y = getStart().y + (arrowTip.y - getStart().y) * 2 / 3
					- 3;
		} else {
			final Point lastBreakPoint = breakPoints
					.get(breakPoints.size() - 1);
			labelPoint.x = lastBreakPoint.x + (arrowTip.x - lastBreakPoint.x)
					* 1 / 2;
			labelPoint.y = lastBreakPoint.y + (arrowTip.y - lastBreakPoint.y)
					* 1 / 2 - 3;
		}
		return labelPoint;
	}

	public Point getStart() {
		return getArc().getSource() != null ? getArc().getSource().getCenter()
				: arc.getStart();
	}

	public Point getEnd() {
		return getArc().getDestination() != null ? getArc().getDestination()
				.getCenter() : arc.getEnd();
	}

	protected void drawSegmentedLine(Graphics g) {
		g.setColor(arc.getColor());
		Point previous = getStart();
		for (Point breakPoint : arc.getBreakPoints()) {
			g.drawLine(previous.x, previous.y, breakPoint.x, breakPoint.y);
			previous = breakPoint;
		}
		g.drawLine(previous.x, previous.y, getEnd().x, getEnd().y);
	}

	protected void drawArrow(Graphics g, Point arrowTip) {
		Point lastBreakPoint = arc.getLastBreakPoint();
		GraphicsTools.drawArrow(g, lastBreakPoint.x, lastBreakPoint.y,
				arrowTip.x, arrowTip.y);
	}

	protected void drawMultiplicityLabel(Graphics g, Point arrowTip,
			int multiplicity) {
		Point labelPoint = getLabelPoint(arrowTip);
		GraphicsTools.drawString(g, Integer.toString(multiplicity),
				labelPoint.x, labelPoint.y, HorizontalAlignment.center,
				VerticalAlignment.bottom);
	}

	protected Point computeArrowTipPoint() {
		Point arrowTip = new Point(getEnd());
		if (getArc().getDestination() == null) {
			return arrowTip;
		} else { // Thanks to
					// http://www.cs.unc.edu/~mcmillan/comp136/Lecture6/Lines.html
			int x0 = arc.getLastBreakPoint().x;
			int y0 = arc.getLastBreakPoint().y;
			int x1 = getEnd().x;
			int y1 = getEnd().y;

			int dy = y1 - y0;
			int dx = x1 - x0;
			int stepx, stepy;

			if (dy < 0) {
				dy = -dy;
				stepy = -1;
			} else {
				stepy = 1;
			}
			if (dx < 0) {
				dx = -dx;
				stepx = -1;
			} else {
				stepx = 1;
			}
			dy <<= 1;
			dx <<= 1;

			if (dx > dy) {
				int fraction = dy - (dx >> 1);
				while (x0 != x1) {
					if (fraction >= 0) {
						y0 += stepy;
						fraction -= dx;
					}
					x0 += stepx;
					fraction += dy;
					if (getArc().getDestination().containsPoint(x0, y0)) {
						return arrowTip;
					}
					arrowTip = new Point(x0, y0);
				}
			} else {
				int fraction = dx - (dy >> 1);
				while (y0 != y1) {
					if (fraction >= 0) {
						x0 += stepx;
						fraction -= dy;
					}
					y0 += stepy;
					fraction += dx;
					if (getArc().getDestination().containsPoint(x0, y0)) {
						return arrowTip;
					}
					arrowTip = new Point(x0, y0);
				}
			}
		}
		return arrowTip;
	}

	public ArcEdge getArc() {
		return arc;
	}

	public void setArc(ArcEdge arc2) {
		this.arc = arc2;
	}

}
