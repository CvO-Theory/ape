/*
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

package org.ape.editor.filechooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Icon;

import org.ape.petrinet.model.Arc;
import org.ape.petrinet.model.Document;
import org.ape.petrinet.model.Subnet;

import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.renderer.impl.APTRenderer;
import uniol.apt.module.exception.ModuleException;

public class AptFileType extends FileType {

	@Override
	public String getExtension() {
		return "apt";
	}

	@Override
	public String getName() {
		return "APT";
	}

	@Override
	public void save(Document document, File file) throws FileTypeException {
		PetriNet petriNetApt = new PetriNet();
		org.ape.petrinet.model.PetriNet petriNetEd = document.getPetriNet();
		Subnet root = petriNetEd.getRootSubnet();

		// Created places in APT format

		Set<org.ape.petrinet.model.Place> places = root.getPlacesRecursively();

		HashMap<org.ape.petrinet.model.Place, Place> placesMap = new HashMap<org.ape.petrinet.model.Place, Place>();

		for (org.ape.petrinet.model.Place place : places) {
			Place placeApt = petriNetApt.createPlace();
			placeApt.setInitialToken(petriNetEd.getInitialMarking().getTokens(
					place));
			placesMap.put(place, placeApt);
		}

		// Created transitions in APT format

		Set<org.ape.petrinet.model.Transition> transitions = root
				.getTransitionsRecursively();

		HashMap<org.ape.petrinet.model.Transition, Transition> transitionsMap = new HashMap<org.ape.petrinet.model.Transition, Transition>();

		for (org.ape.petrinet.model.Transition transition : transitions) {
			Transition transitionApt = petriNetApt.createTransition();
			transitionsMap.put(transition, transitionApt);
			if (transition.getLabel() != null) {
				transitionApt.setLabel(transition.getLabel());
			}
			for (Arc arc : transition.getConnectedArcs()) {
				org.ape.petrinet.model.Place place = arc.getPlaceNode()
						.getPlace();
				Place placeApt = placesMap.get(place);

				// Create arcs in APT format

				if (arc.isPlaceToTransition()) {
					petriNetApt.createFlow(placeApt, transitionApt,
							arc.getMultiplicity());

				} else {
					petriNetApt.createFlow(transitionApt, placeApt,
							arc.getMultiplicity());
				}
			}
		}

		// Save in APT format
		APTRenderer renderer = new APTRenderer();
		try {
			String pnString = renderer.render(petriNetApt);
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(pnString);
			out.close();
		} catch (ModuleException e) {
		} catch (IOException e) {
		}

	}

	@Override
	public Document load(File file) throws FileTypeException {
		throw new UnsupportedOperationException("Loading not supported.");
	}

	@Override
	public Icon getIcon() {
		// TODO change Icon
		return null;
	}

}
