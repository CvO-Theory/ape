package org.ape.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.ape.editor.controller.APE;
import org.ape.editor.controller.Selection;
import org.ape.petrinet.model.Element;
import org.ape.petrinet.model.PetriNet;

/**
 *
 * @author matmas
 */
public class SelectAllAction extends AbstractAction {

	public SelectAllAction() {
		String name = "Select All";
		putValue(NAME, name);
		putValue(SHORT_DESCRIPTION, name);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
		setEnabled(false);
	}

    @Override
	public void actionPerformed(ActionEvent e) {
		PetriNet petriNet = APE.getRoot().getDocument().getPetriNet();
        
        Selection selection = APE.getRoot().getSelection();
        selection.clear();
        selection.addAll(petriNet.getCurrentSubnet().getElements());
		
        APE.getRoot().refreshAll();
	}

//	@Override
//	public boolean shouldBeEnabled() {
//		PetriNet petriNet = APE.getRoot().getDocument().getPetriNet();
//		return !petriNet.isEmpty();
//	}

}
