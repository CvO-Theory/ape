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

package org.ape.editor.controller;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ape.editor.actions.AddSelectedTransitionsToSelectedRolesAction;
import org.ape.editor.actions.CloseSubnetAction;
import org.ape.editor.actions.ConvertTransitionToSubnetAction;
import org.ape.editor.actions.CopyAction;
import org.ape.editor.actions.CutAction;
import org.ape.editor.actions.DeleteAction;
import org.ape.editor.actions.OpenSubnetAction;
import org.ape.editor.actions.PasteAction;
import org.ape.editor.actions.RedoAction;
import org.ape.editor.actions.RemoveSelectedTransitionsFromSelectedRolesAction;
import org.ape.editor.actions.ReplaceSubnetAction;
import org.ape.editor.actions.SaveSubnetAsAction;
import org.ape.editor.actions.SelectAllAction;
import org.ape.editor.actions.SetArcMultiplicityAction;
import org.ape.editor.actions.SetColorAction;
import org.ape.editor.actions.SetLabelAction;
import org.ape.editor.actions.SetPlaceStaticAction;
import org.ape.editor.actions.SetTokensAction;
import org.ape.editor.actions.UndoAction;
import org.ape.editor.filechooser.FileType;
import org.ape.editor.filechooser.FileTypeException;
import org.ape.editor.model.ActiveFeature;
import org.ape.editor.view.MainFrame;
import org.ape.editor.view.canvas.Canvas;
import org.ape.petrinet.model.Arc;
import org.ape.petrinet.model.Document;
import org.ape.petrinet.model.Element;
import org.ape.petrinet.model.Marking;
import org.ape.petrinet.model.PlaceNode;
import org.ape.petrinet.model.ReferencePlace;
import org.ape.petrinet.model.Role;
import org.ape.petrinet.model.Subnet;
import org.ape.petrinet.model.Transition;
import org.ape.petrinet.model.TransitionNode;
import org.ape.util.CollectionTools;
import org.ape.util.FeatureType;
import org.ape.util.GraphicsTools;
import org.ape.util.ListEditor;

/**
 * This class is the main point of the application.
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Root implements WindowListener, ListSelectionListener,
		SelectionChangedListener {

	private static final String APP_NAME = "APE - APT-Editor";
	private static final String APP_VERSION = "1.0";
	private ActiveFeature activeFeatureType;

	public Root(String[] args) {

		APE.setRoot(this);

		loadPreferences();

		activeFeatureType = new ActiveFeature(this);

		selection.setSelectionChangedListener(this);

		roleEditor = new ListEditor<Role>("Roles", document.roles,
				getParentFrame());
		roleEditor.addButton.setIcon(GraphicsTools.getIcon("ape/addrole.gif"));
		roleEditor.deleteButton.setIcon(GraphicsTools
				.getIcon("ape/deleterole.gif"));
		roleEditor.addButton.setToolTipText("Add role");
		roleEditor.editButton.setToolTipText("Edit role properties");
		roleEditor.deleteButton.setToolTipText("Delete role");
		roleEditor.addListSelectionListener(this);

		setupActions();

		mainFrame = new MainFrame(this, getNewWindowTitle());

		if (args.length == 1) {
			String filename = args[0];
			File file = new File(filename);
			FileType fileType = FileType.getAcceptingFileType(file,
					FileType.getAllFileTypes());
			try {
				Document document = fileType.load(file);
				this.setDocument(document);
				this.setCurrentFile(file); // TODO: make it DRY with
											// OpenFileAction
				this.setModified(false);
				this.setCurrentDirectory(file.getParentFile());
				canvas.repaint();
			} catch (FileTypeException ex) {
				Logger.getLogger(Root.class.getName())
						.log(Level.INFO, null, ex);
			}
		}

	}

	private static final String CURRENT_DIRECTORY = "current_directory";

	private void loadPreferences() {
		Preferences preferences = Preferences.userNodeForPackage(this
				.getClass());
		setCurrentDirectory(new File(preferences.get(CURRENT_DIRECTORY,
				System.getProperty("user.home"))));
	}

	private void savePreferences() {
		Preferences preferences = Preferences.userNodeForPackage(this
				.getClass());
		preferences.put(CURRENT_DIRECTORY, getCurrentDirectory().toString());
	}

	// Undo manager - per tab
	public UndoAction undo = new UndoAction(this);
	public RedoAction redo = new RedoAction(this);
	protected UndoManager undoManager = new UndoManager(this, undo, redo);

	public UndoManager getUndoManager() {
		return undoManager;
	}

	// Current directory - per application
	protected File currentDirectory;

	public File getCurrentDirectory() {
		return currentDirectory;
	}

	public void setCurrentDirectory(File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}

	// Main frame - per application
	private MainFrame mainFrame;

	public Frame getParentFrame() {
		return mainFrame;
	}

	// Document - per tab
	protected Document document = new Document();

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
		getDocument().petriNet.resetView();
		getRoleEditor().setModel(getDocument().roles);
		getUndoManager().eraseAll();
		refreshAll();
	}

	// Clicked element - per tab
	protected Element clickedElement = null;

	public Element getClickedElement() {
		return clickedElement;
	}

	public void setClickedElement(Element clickedElement) {
		this.clickedElement = clickedElement;
		enableOnlyPossibleActions();
	}

	// Selection - per tab
	protected Selection selection = new Selection();

	public Selection getSelection() {
		return selection;
	}

	@Override
	public void selectionChanged() {
		enableOnlyPossibleActions();
	}

	// Selection + clicked element

	public Set<Element> getSelectedElementsWithClickedElement() {
		Set<Element> selectedElements = new HashSet<Element>();
		selectedElements.addAll(getSelection().getElements());
		selectedElements.add(getClickedElement());
		return selectedElements;
	}

	// List editor - per tab
	protected ListEditor<Role> roleEditor; // TODO

	@Override
	public void valueChanged(ListSelectionEvent e) {
		enableOnlyPossibleActions();
		repaintCanvas();
	}

	// per tab
	public void selectTool_Select() {
		activeFeatureType.setActiveFeature(FeatureType.SELECT);
		canvas.activeCursor = Cursor.getDefaultCursor();
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}

	public void selectTool_Place() {
		activeFeatureType.setActiveFeature(FeatureType.PLACE);
		canvas.activeCursor = GraphicsTools.getCursor("ape/canvas/place.gif",
				new Point(16, 16));
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}

	public boolean isSelectedTool_Place() {
		return mainFrame.getPlace().isSelected();
	}

	public void selectTool_Transition() {
		activeFeatureType.setActiveFeature(FeatureType.TRANSITION);
		canvas.activeCursor = GraphicsTools.getCursor(
				"ape/canvas/transition.gif", new Point(16, 16));
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}

	public boolean isSelectedTool_Transition() {
		return mainFrame.getTransition().isSelected();
	}

	public void selectTool_Arc() {
		activeFeatureType.setActiveFeature(FeatureType.ARC);
		canvas.activeCursor = GraphicsTools.getCursor("ape/canvas/arc.gif",
				new Point(0, 0));
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}

	public boolean isSelectedTool_Arc() {
		return mainFrame.getArc().isSelected();
	}

	public void selectTool_Token() {
		activeFeatureType.setActiveFeature(FeatureType.TOKEN);
		canvas.activeCursor = GraphicsTools.getCursor(
				"ape/canvas/token_or_fire.gif", new Point(16, 0));
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}

	public boolean isSelectedTool_Token() {
		return mainFrame.getToken().isSelected();
	}

	public ListEditor<Role> getRoleEditor() {
		return roleEditor;
	}

	public JPopupMenu getPlacePopup() {
		return placePopup;
	}

	public JPopupMenu getTransitionPopup() {
		return transitionPopup;
	}

	public JPopupMenu getArcEdgePopup() {
		return arcEdgePopup;
	}

	public JPopupMenu getSubnetPopup() {
		return subnetPopup;
	}

	public JPopupMenu getCanvasPopup() {
		return canvasPopup;
	}

	// per tab
	public Canvas canvas = new Canvas(this);
	// protected DrawingBoard drawingBoard = new DrawingBoard(canvas);

	public JPopupMenu placePopup;
	public JPopupMenu transitionPopup;
	public JPopupMenu arcEdgePopup;
	public JPopupMenu subnetPopup;
	public JPopupMenu canvasPopup;

	public Action setLabel;
	public Action setTokens;
	public Action setArcMultiplicity;
	public Action delete;

	public Action getCutAction() {
		return cutAction;
	}

	public void setCutAction(Action cutAction) {
		this.cutAction = cutAction;
	}

	public Action getSetLabel() {
		return setLabel;
	}

	public Action getSetTokens() {
		return setTokens;
	}

	public Action getSetArcMultiplicity() {
		return setArcMultiplicity;
	}

	public Action getDelete() {
		return delete;
	}

	public Action getSetPlaceStatic() {
		return setPlaceStatic;
	}

	public Action getAddSelectedTransitionsToSelectedRoles() {
		return addSelectedTransitionsToSelectedRoles;
	}

	public Action getRemoveSelectedTransitionsFromSelectedRoles() {
		return removeSelectedTransitionsFromSelectedRoles;
	}

	public Action getConvertTransitionToSubnet() {
		return convertTransitionToSubnet;
	}

	public Action getReplaceSubnet() {
		return replaceSubnet;
	}

	public Action getSaveSubnetAs() {
		return saveSubnetAs;
	}

	public Action getCopyAction() {
		return copyAction;
	}

	public Action getPasteAction() {
		return pasteAction;
	}

	public Action getSelectAllAction() {
		return selectAllAction;
	}

	public Action getSetColor() {
		return setColor;
	}

	public Action setPlaceStatic;
	public Action addSelectedTransitionsToSelectedRoles;
	public Action removeSelectedTransitionsFromSelectedRoles;
	public Action convertTransitionToSubnet;
	public Action replaceSubnet;
	public Action saveSubnetAs;
	public Action cutAction;
	public Action copyAction;
	private Action pasteAction;
	public Action selectAllAction;
	public Action setColor;

	// per application
	public Action openSubnet;
	public Action closeSubnet;

	public void openSubnet() {
		openSubnet.actionPerformed(null);
	}

	public void closeSubnet() {
		closeSubnet.actionPerformed(null);
	}

	public void refreshAll() {
		canvas.repaint();
		enableOnlyPossibleActions();
		getRoleEditor().refreshSelected();
	}

	public void repaintCanvas() {
		canvas.repaint();
	}

	protected void enableOnlyPossibleActions() {
		boolean isDeletable = clickedElement != null
				&& !(clickedElement instanceof ReferencePlace)
				|| !selection.isEmpty()
				&& !CollectionTools.containsOnlyInstancesOf(
						selection.getElements(), ReferencePlace.class);
		boolean isCutable = isDeletable;
		boolean isCopyable = isCutable;
		boolean isPastable = !clipboard.isEmpty();
		boolean isPlaceNode = clickedElement instanceof PlaceNode;
		boolean isArc = clickedElement instanceof Arc;
		boolean isTransitionNode = clickedElement instanceof TransitionNode;
		boolean isTransition = clickedElement instanceof Transition;
		boolean isSubnet = clickedElement instanceof Subnet;
		boolean areSubnets = !selection.getSubnets().isEmpty();
		boolean areTransitionNodes = !selection.getTransitionNodes().isEmpty();
		boolean areTransitions = !selection.getTransitions().isEmpty();
		boolean roleSelected = !roleEditor.getSelectedElements().isEmpty();
		boolean isParent = !document.petriNet.isCurrentSubnetRoot();
		boolean isElement = clickedElement instanceof Element;

		cutAction.setEnabled(isCutable);
		copyAction.setEnabled(isCopyable);
		pasteAction.setEnabled(isPastable);
		selectAllAction.setEnabled(true);
		delete.setEnabled(isDeletable);
		setArcMultiplicity.setEnabled(isArc);
		setTokens.setEnabled(isPlaceNode);
		setLabel.setEnabled(isPlaceNode || isTransitionNode);
		addSelectedTransitionsToSelectedRoles
				.setEnabled((isTransitionNode || areTransitionNodes)
						&& roleSelected);
		removeSelectedTransitionsFromSelectedRoles
				.setEnabled((isTransitionNode || areTransitionNodes)
						&& roleSelected);
		convertTransitionToSubnet.setEnabled(isTransition || areTransitions
				|| isSubnet || areSubnets);
		replaceSubnet.setEnabled(isSubnet || areSubnets);
		saveSubnetAs.setEnabled(isSubnet);
		openSubnet.setEnabled(isSubnet);
		closeSubnet.setEnabled(isParent);
		undo.setEnabled(getUndoManager().isUndoable());
		redo.setEnabled(getUndoManager().isRedoable());
		setPlaceStatic.setEnabled(isPlaceNode);
		setColor.setEnabled(isElement);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		quitApplication();
	}

	/**
	 * Terminates the application
	 */
	public void quitApplication() {
		if (!this.isModified()) {
			quitNow();
		}
		mainFrame.setState(Frame.NORMAL);
		mainFrame.setVisible(true);
		int answer = JOptionPane.showOptionDialog(this.getParentFrame(),
				"Any unsaved changes will be lost. Really quit?", "Quit",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				new String[] { "Quit", "Cancel" }, "Cancel");
		if (answer == JOptionPane.YES_OPTION) {
			quitNow();
		}
	}

	private void quitNow() {
		savePreferences();
		System.exit(0);
	}

	private void setupActions() {
		setLabel = new SetLabelAction(this);
		setTokens = new SetTokensAction(this);
		setPlaceStatic = new SetPlaceStaticAction(this);
		setArcMultiplicity = new SetArcMultiplicityAction(this);
		addSelectedTransitionsToSelectedRoles = new AddSelectedTransitionsToSelectedRolesAction(
				this);
		removeSelectedTransitionsFromSelectedRoles = new RemoveSelectedTransitionsFromSelectedRolesAction(
				this);
		convertTransitionToSubnet = new ConvertTransitionToSubnetAction(this);
		openSubnet = new OpenSubnetAction(this);
		closeSubnet = new CloseSubnetAction(this);
		delete = new DeleteAction(this);
		setColor = new SetColorAction(this);

		cutAction = new CutAction(this);
		copyAction = new CopyAction(this);
		pasteAction = new PasteAction(this);
		selectAllAction = new SelectAllAction();

		saveSubnetAs = new SaveSubnetAsAction(this);
		replaceSubnet = new ReplaceSubnetAction(this);
	}

	public Marking getCurrentMarking() {
		return getDocument().petriNet.getInitialMarking();
	}

	public void setCurrentMarking(Marking currentMarking) {
	}

	protected LocalClipboard clipboard = new LocalClipboard();

	public LocalClipboard getClipboard() {
		return clipboard;
	}

	private boolean isModified = false;

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
		mainFrame.setTitle(getNewWindowTitle());
	}

	public String getNewWindowTitle() {
		String windowTitle = "";
		if (getCurrentFile() != null) {
			windowTitle += getCurrentFile().getName();
		} else {
			windowTitle += "Untitled";
		}
		if (isModified()) {
			windowTitle += " [modified]";
		}
		windowTitle += " - " + getAppShortName();
		return windowTitle;
	}

	private File currentFile = null;

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
		mainFrame.setTitle(getNewWindowTitle());
	}

	public String getAppShortName() {
		return APP_NAME;
	}

	public String getAppLongName() {
		return APP_NAME + ", version " + APP_VERSION;
	}

	public ActiveFeature getActiveFeatureType() {
		return activeFeatureType;
	}

	public void setActiveFeatureType(FeatureType activeFeatureType) {
		this.activeFeatureType.setActiveFeature(activeFeatureType);
	}

	public Feature getActiveFeature() {
		return activeFeatureType.getActiveFeature();
	}

}
