/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
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

package org.ape;

import org.ape.petrinet.model.PetriNet;
import org.ape.petrinet.model.Transition;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class LabelToTransition {
	
	private Map<String, Transition> map = new HashMap<String, Transition>();
	private PetriNet petriNet;

	public LabelToTransition(PetriNet petriNet) {
		this.petriNet = petriNet;
	}
	
	public Transition getTransition(String label) {
		if (label.equals(null)) {
			return null;
		}
		if (map.containsKey(label)) {
			return map.get(label);
		}
		Transition transition = new Transition();
		transition.setLabel(label);
		petriNet.getNodeSimpleIdGenerator().setUniqueId(transition);
		map.put(label, transition);
		return transition;
	}
}
