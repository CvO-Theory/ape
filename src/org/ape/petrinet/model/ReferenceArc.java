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

import org.ape.petrinet.view.GraphicalReferenceArc;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReferenceArc extends ArcEdge {

	public ReferenceArc(PlaceNode placeNode, Subnet subnet) {
		super(placeNode, subnet, true); // true or false - it is the same. TODO:
										// update it is not the same because of
										// breakpoints order from source to
										// destination node
		setGraphicalElement(new GraphicalReferenceArc(this));
	}

	public Subnet getSubnet() {
		return (Subnet) getTransitionNode();
	}

	public ReferencePlace getReferencePlace() {
		for (Element element : getSubnet().getElements()) {
			if (element instanceof ReferencePlace) {
				ReferencePlace referencePlace = (ReferencePlace) element;
				if (referencePlace.getConnectedPlaceNode() == getPlaceNode()) {
					return referencePlace;
				}
			}
		}
		throw new RuntimeException("ReferenceArc: missing ReferencePlace");
	}

}
