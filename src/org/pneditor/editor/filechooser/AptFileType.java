package org.pneditor.editor.filechooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Icon;

import org.pneditor.petrinet.Arc;
import org.pneditor.petrinet.Document;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.Subnet;
import org.pneditor.util.GraphicsTools;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Token;
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
		org.pneditor.petrinet.PetriNet petriNetEd = document.petriNet;
		Subnet root = petriNetEd.getRootSubnet();
		Set<org.pneditor.petrinet.Place> places = root.getPlacesRecursively();
		HashMap<org.pneditor.petrinet.Place, Place> placesMap = new HashMap<org.pneditor.petrinet.Place, Place>();
		for (org.pneditor.petrinet.Place place : places){
			Place placeApt = petriNetApt.createPlace();
			placeApt.setInitialToken(petriNetEd.getInitialMarking().getTokens(place));
			placesMap.put(place, placeApt);
		}
		Set<org.pneditor.petrinet.Transition> transitions = root.getTransitionsRecursively();
		HashMap<org.pneditor.petrinet.Transition, Transition> transitionsMap = new HashMap<org.pneditor.petrinet.Transition, Transition>();
		for (org.pneditor.petrinet.Transition transition : transitions){
			Transition transitionApt = petriNetApt.createTransition();
			System.out.print(transition.getLabel());
			transitionsMap.put(transition, transitionApt);
			transitionApt.setLabel(transition.getLabel());
			for (Arc arc : transition.getConnectedArcs()) {
				org.pneditor.petrinet.Place place = arc.getPlaceNode().getPlace();
				Place placeApt = placesMap.get(place);
				if (arc.isPlaceToTransition()) {
					petriNetApt.createFlow(placeApt, transitionApt, arc.getMultiplicity());
					System.out.println(" <- (" + arc.getMultiplicity() + ") " + place.getLabel());
					
				} else {
					petriNetApt.createFlow(transitionApt, placeApt, arc.getMultiplicity());
					System.out.println(" -> (" + arc.getMultiplicity() + ") " + place.getLabel());
					
				}
			}
		}
		APTRenderer renderer = new APTRenderer();
		try {
			String pnString = renderer.render(petriNetApt);
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(pnString);
			out.close();
		} catch (ModuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub

	}

	@Override
	public Document load(File file) throws FileTypeException {
		throw new UnsupportedOperationException("Loading not supported.");
	}

	@Override
	public Icon getIcon() {
		// TODO change Icon
		final Icon icon = GraphicsTools.getIcon("pneditor/filechooser/eps.gif");
		return icon;
	}
	
	/**
	 * Render the given Petri net into the APT file format.\\
	 * This method was originally created by the APT-group but changed to fit into this project. (hillit)
	 * @param pn the Petri net that should be represented as a string.
	 * @return the string representation of the net.
	 * @throws ModuleException when the Petri net cannot be expressed in the LoLA file format, for example when
	 * invalid identifiers are used or when the net has no places or no transitions.
	 */
	public String render(PetriNet pn) throws ModuleException {
		// there is no possibility for the invalid "omega" in this editor, so the verifying is not needed (hillit)
		//verifyNet(pn);

		STGroup group = new STGroupFile("org/pneditor/filechooser/APTPN.stg");
		ST pnTemplate = group.getInstanceOf("pn");
		
		// At the moment petrinets don't have names in this editor (hillit)
		//pnTemplate.add("name", pn.getName());
		pnTemplate.add("name", "");

		// Handle places
		pnTemplate.add("places", pn.getPlaces());

		// Handle the initial marking
		for (Place p : pn.getPlaces()) {
			Token val = pn.getInitialMarkingCopy().getToken(p);
			if (val.getValue() != 0) {
				pnTemplate.addAggr("marking.{place, weight}", p, val.getValue());
			}
		}

		// Handle transitions (and arcs)
		pnTemplate.add("transitions", pn.getTransitions());

		return pnTemplate.render();
	}

}
