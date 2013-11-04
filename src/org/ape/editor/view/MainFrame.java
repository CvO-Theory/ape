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

package org.ape.editor.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.ape.editor.actions.AboutAction;
import org.ape.editor.actions.ArcSelectToolAction;
import org.ape.editor.actions.ExportAction;
import org.ape.editor.actions.ImportAction;
import org.ape.editor.actions.NewFileAction;
import org.ape.editor.actions.OpenFileAction;
import org.ape.editor.actions.PlaceSelectToolAction;
import org.ape.editor.actions.QuitAction;
import org.ape.editor.actions.SaveAction;
import org.ape.editor.actions.SaveFileAsAction;
import org.ape.editor.actions.SelectionSelectToolAction;
import org.ape.editor.actions.TokenSelectToolAction;
import org.ape.editor.actions.TransitionSelectToolAction;
import org.ape.editor.actions.algorithms.BoundednessAction;
import org.ape.editor.controller.Root;
import org.ape.editor.filechooser.AptFileType;
import org.ape.editor.filechooser.EpsFileType;
import org.ape.editor.filechooser.FileType;
import org.ape.editor.filechooser.PflowFileType;
import org.ape.editor.filechooser.PngFileType;
import org.ape.editor.filechooser.ViptoolPnmlFileType;
import org.ape.util.FeatureType;
import org.ape.util.GraphicsTools;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class MainFrame extends JFrame {

	private JToggleButton select;
	private JToggleButton place;
	private JToggleButton transition;
	private JToggleButton arc;
	// per application
	private JToggleButton token;
	private JToolBar toolBar = new JToolBar();
	private Root root;

	public MainFrame(Root root, String title) {
		super(title);
		UIManager.getDefaults().put("ToolTip.hideAccelerator", Boolean.TRUE);
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		if (lookAndFeel.equals("javax.swing.plaf.metal.MetalLookAndFeel")) {
			// lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
		}
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception ex) {
		}
		this.root = root;
		setupMainFrame();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setupFrameIcons();
	}

	public void setupFrameIcons() {
		List<Image> icons = new LinkedList<Image>();
		icons.add(GraphicsTools.getBufferedImage("icon16.png"));
		icons.add(GraphicsTools.getBufferedImage("icon32.png"));
		icons.add(GraphicsTools.getBufferedImage("icon48.png"));
		setIconImages(icons);
	}

	public JToggleButton getSelect() {
		return select;
	}

	public void setSelect(JToggleButton select) {
		this.select = select;
	}

	public JToggleButton getPlace() {
		return place;
	}

	public void setPlace(JToggleButton place) {
		this.place = place;
	}

	public JToggleButton getTransition() {
		return transition;
	}

	public void setTransition(JToggleButton transition) {
		this.transition = transition;
	}

	public JToggleButton getArc() {
		return arc;
	}

	public void setArc(JToggleButton arc) {
		this.arc = arc;
	}

	public JToggleButton getToken() {
		return token;
	}

	public void setToken(JToggleButton token) {
		this.token = token;
	}

	public void setupMainFrame() {
		List<FileType> openSaveFiletypes = new LinkedList<FileType>();
		openSaveFiletypes.add(new PflowFileType());
		List<FileType> importFiletypes = new LinkedList<FileType>();
		importFiletypes.add(new ViptoolPnmlFileType());
		List<FileType> exportFiletypes = new LinkedList<FileType>();
		exportFiletypes.add(new AptFileType());
		exportFiletypes.add(new ViptoolPnmlFileType());
		exportFiletypes.add(new EpsFileType());
		exportFiletypes.add(new PngFileType());

		Action newFile = new NewFileAction(root);
		Action openFile = new OpenFileAction(root, openSaveFiletypes);
		Action saveFile = new SaveAction(root, openSaveFiletypes);
		Action saveFileAs = new SaveFileAsAction(root, openSaveFiletypes);
		Action importFile = new ImportAction(root, importFiletypes);
		Action exportFile = new ExportAction(root, exportFiletypes);
		Action quit = new QuitAction(root);

		Action selectTool_SelectionAction = new SelectionSelectToolAction(root);
		Action selectTool_PlaceAction = new PlaceSelectToolAction(root);
		Action selectTool_TransitionAction = new TransitionSelectToolAction(
				root);
		Action selectTool_ArcAction = new ArcSelectToolAction(root);
		Action selectTool_TokenAction = new TokenSelectToolAction(root);

		select = new SelectButton(root, selectTool_SelectionAction);
		select.setSelected(true);
		place = new PlaceButton(root, selectTool_PlaceAction);
		transition = new TransitionButton(root, selectTool_TransitionAction);
		arc = new ArcButton(root, selectTool_ArcAction);
		token = new TokenButton(root, selectTool_TokenAction);

		select.setText("");
		place.setText("");
		transition.setText("");
		arc.setText("");
		token.setText("");

		ButtonGroup drawGroup = new ButtonGroup();
		drawGroup.add(select);
		drawGroup.add(place);
		drawGroup.add(transition);
		drawGroup.add(arc);
		drawGroup.add(token);

		toolBar.setFloatable(false);

		toolBar.add(newFile);
		toolBar.add(openFile);
		toolBar.add(saveFile);
		toolBar.add(importFile);
		toolBar.add(exportFile);
		toolBar.addSeparator();

		toolBar.add(root.cutAction);
		toolBar.add(root.copyAction);
		toolBar.add(root.getPasteAction());
		toolBar.addSeparator();

		toolBar.add(root.undo);
		toolBar.add(root.redo);
		toolBar.add(root.delete);
		toolBar.addSeparator();
		toolBar.add(select);
		toolBar.add(place);
		toolBar.add(transition);
		toolBar.add(arc);
		toolBar.add(token);
		toolBar.addSeparator();
		toolBar.add(root.addSelectedTransitionsToSelectedRoles);
		toolBar.add(root.removeSelectedTransitionsFromSelectedRoles);

		root.setActiveFeatureType(FeatureType.SELECT);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		menuBar.add(editMenu);

		JMenu drawMenu = new JMenu("Draw");
		drawMenu.setMnemonic('D');
		menuBar.add(drawMenu);

		JMenu elementMenu = new JMenu("Element");
		elementMenu.setMnemonic('l');
		menuBar.add(elementMenu);

		JMenu rolesMenu = new JMenu("Roles");
		rolesMenu.setMnemonic('R');
		menuBar.add(rolesMenu);

		JMenu subnetMenu = new JMenu("Subnet");
		subnetMenu.setMnemonic('S');
		menuBar.add(subnetMenu);

		// asus 2012 algorithms menu
		JMenu algorithmsMenu = new JMenu("Algorithms");
		algorithmsMenu.setMnemonic('A');
		menuBar.add(algorithmsMenu);

		// asus 2012 algorithms submenu items
		algorithmsMenu.add(new BoundednessAction(root));

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new AboutAction(root));
		menuBar.add(helpMenu);

		fileMenu.add(newFile);
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		fileMenu.add(saveFileAs);
		fileMenu.add(importFile);
		fileMenu.add(exportFile);
		fileMenu.addSeparator();
		fileMenu.add(quit);

		editMenu.add(root.undo);
		editMenu.add(root.redo);
		editMenu.addSeparator();
		editMenu.add(root.cutAction);
		editMenu.add(root.copyAction);
		editMenu.add(root.getPasteAction());
		editMenu.add(root.selectAllAction);
		editMenu.add(root.delete);

		elementMenu.add(root.setLabel);
		elementMenu.add(root.setColor);
		elementMenu.addSeparator();
		elementMenu.add(root.setTokens);
		elementMenu.add(root.setPlaceStatic);
		elementMenu.addSeparator();
		elementMenu.add(root.setArcMultiplicity);

		rolesMenu.add(root.addSelectedTransitionsToSelectedRoles);
		rolesMenu.add(root.removeSelectedTransitionsFromSelectedRoles);

		drawMenu.add(selectTool_SelectionAction);
		drawMenu.addSeparator();
		drawMenu.add(selectTool_PlaceAction);
		drawMenu.add(selectTool_TransitionAction);
		drawMenu.add(selectTool_ArcAction);
		drawMenu.add(selectTool_TokenAction);

		subnetMenu.add(root.openSubnet);
		subnetMenu.add(root.closeSubnet);
		subnetMenu.add(root.replaceSubnet);
		subnetMenu.add(root.saveSubnetAs);
		subnetMenu.add(root.convertTransitionToSubnet);

		root.placePopup = new JPopupMenu();
		root.placePopup.add(root.setLabel);
		root.placePopup.add(root.setTokens);
		root.placePopup.add(root.setPlaceStatic);
		root.placePopup.add(root.setColor);
		root.placePopup.addSeparator();
		root.placePopup.add(root.cutAction);
		root.placePopup.add(root.copyAction);
		root.placePopup.add(root.delete);

		root.transitionPopup = new JPopupMenu();
		root.transitionPopup.add(root.setLabel);
		root.transitionPopup.add(root.convertTransitionToSubnet);
		root.transitionPopup.add(root.addSelectedTransitionsToSelectedRoles);
		root.transitionPopup
				.add(root.removeSelectedTransitionsFromSelectedRoles);
		root.transitionPopup.add(root.setColor);
		root.transitionPopup.addSeparator();
		root.transitionPopup.add(root.cutAction);
		root.transitionPopup.add(root.copyAction);
		root.transitionPopup.add(root.delete);

		Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);

		root.canvasPopup = new JPopupMenu();
		root.canvasPopup.add(root.closeSubnet).setFont(boldFont);
		root.canvasPopup.add(root.getPasteAction());

		root.subnetPopup = new JPopupMenu();
		root.subnetPopup.add(root.openSubnet).setFont(boldFont);
		root.subnetPopup.add(root.setLabel);
		root.subnetPopup.add(root.replaceSubnet);
		root.subnetPopup.add(root.saveSubnetAs);
		root.subnetPopup.add(root.convertTransitionToSubnet);
		root.subnetPopup.add(root.addSelectedTransitionsToSelectedRoles);
		root.subnetPopup.add(root.removeSelectedTransitionsFromSelectedRoles);
		root.subnetPopup.add(root.setColor);
		root.subnetPopup.addSeparator();
		root.subnetPopup.add(root.cutAction);
		root.subnetPopup.add(root.copyAction);
		root.subnetPopup.add(root.delete);

		root.arcEdgePopup = new JPopupMenu();
		root.arcEdgePopup.add(root.setArcMultiplicity);
		root.arcEdgePopup.add(root.setColor);
		root.arcEdgePopup.add(root.delete);

		JScrollPane canvasScrollPane = new JScrollPane(root.canvas,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane.setDividerSize(6);
		splitPane.setOneTouchExpandable(true);
		splitPane.setLeftComponent(root.getRoleEditor());
		splitPane.setRightComponent(canvasScrollPane);
		splitPane.setDividerLocation(120);

		add(splitPane, BorderLayout.CENTER);
		add(toolBar, BorderLayout.NORTH);

		addWindowListener(root);
		setLocation(50, 50);
		setSize(700, 450);
		setVisible(true);
	}
}