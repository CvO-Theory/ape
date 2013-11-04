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

package org.ape.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.ape.editor.controller.Root;
import org.ape.util.GraphicsTools;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AboutAction extends AbstractAction {

	private Root root;

	public AboutAction(Root root) {
		this.root = root;
		String name = "About...";
		putValue(NAME, name);
		putValue(SMALL_ICON, GraphicsTools.getIcon("ape/About16.gif"));
		putValue(SHORT_DESCRIPTION, name);
	}

	public void actionPerformed(ActionEvent e) {
		JOptionPane
				.showOptionDialog(
						root.getParentFrame(),
						root.getAppLongName()
								+ "\n"
								+ "Author: Hillit Saathoff\n"
								+ "\n"
								+ "This program was originally developed under the name PNEditor but \n"
								+ "has been enhanced to APE (APT-Editor). The original authors were\n"
								+ "\n"
								+ "Author: Martin Riesz\n"
								+ "Contributors: Milka Knapereková (boudedness algorithm)\n"
								+ "\n"
								+ "This program is free software: you can redistribute it and/or modify\n"
								+ "it under the terms of the GNU General Public License as published by\n"
								+ "the Free Software Foundation, either version 3 of the License, or\n"
								+ "(at your option) any later version.\n"
								+ "\n"
								+ "This program is distributed in the hope that it will be useful,\n"
								+ "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
								+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
								+ "GNU General Public License for more details.\n"
								+ "You should have received a copy of the GNU General Public License\n"
								+ "along with this program.  If not, see <http://www.gnu.org/licenses/>.",
						"About", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null,
						new String[] { "OK" }, "OK");
	}

}
