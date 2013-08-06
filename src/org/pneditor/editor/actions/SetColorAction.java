package org.pneditor.editor.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import org.pneditor.editor.Root;
import org.pneditor.editor.RootPflow;
import org.pneditor.editor.commands.DeleteElementCommand;
import org.pneditor.editor.commands.SetArcMultiplicityCommand;
import org.pneditor.petrinet.Arc;
import org.pneditor.util.GraphicsTools;

public class SetColorAction  extends AbstractAction {
	
	private Root root;
	
	public SetColorAction(Root root) {
		this.root = root;
		String name = "Set color";
		putValue(NAME, name);
		//TODO neues Icon einfuegen
		putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/multiplicity.gif"));
		putValue(SHORT_DESCRIPTION, name);
		putValue(MNEMONIC_KEY, KeyEvent.VK_C);
//		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("C"));
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (root.getClickedElement() != null) {
			Color color = JColorChooser.showDialog(null, "Choose Color", root.getClickedElement().getColor());
			if (color != null){
				root.getClickedElement().setColor(color);
			}
		}
			
	}

}
