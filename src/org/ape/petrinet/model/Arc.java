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

package org.ape.petrinet.model;

import org.ape.petrinet.view.GraphicalArc;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Arc extends ArcEdge implements Cloneable {

	private int multiplicity = 1;

	public Arc(Node sourceNode) {
		setSource(sourceNode);
		setStart(sourceNode.getCenter().x, sourceNode.getCenter().y);
		setEnd(sourceNode.getCenter().x, sourceNode.getCenter().y);
		setGraphicalElement(new GraphicalArc(this));
	}

	public Arc(Node source, Node destination) {
		setSource(source);
		setDestination(destination);
		setGraphicalElement(new GraphicalArc(this));
	}

	public Arc(PlaceNode placeNode, Transition transition,
			boolean placeToTransition) {
		super(placeNode, transition, placeToTransition);
		setGraphicalElement(new GraphicalArc(this));
	}

	public int getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(int multiplicity) {
		this.multiplicity = multiplicity;
	}

	public Transition getTransition() {
		return (Transition) getTransitionNode();
	}

	@Override
	public Arc getClone() {
		Arc arc = (Arc) super.getClone();
		arc.multiplicity = this.multiplicity;
		return arc;
	}
}
