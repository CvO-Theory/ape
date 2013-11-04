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

package org.ape.editor.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;

import org.ape.editor.commands.SetColorCommand;
import org.ape.editor.controller.Root;

public class SetColorAction extends AbstractAction {

	private Root root;

	public SetColorAction(Root root) {
		this.root = root;
		String name = "Set color";
		putValue(NAME, name);
		putValue(SHORT_DESCRIPTION, name);
		putValue(MNEMONIC_KEY, KeyEvent.VK_C);
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (root.getClickedElement() != null) {
			Color color = JColorChooser.showDialog(null, "Choose Color", root
					.getClickedElement().getColor());
			if (color != null) {
				root.getUndoManager().executeCommand(
						new SetColorCommand(root.getClickedElement(), color));
			}
		}

	}

}
