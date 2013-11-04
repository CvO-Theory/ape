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

package org.ape.editor.view.canvas;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import org.ape.editor.controller.APE;
import org.ape.editor.controller.CanvasListener;
import org.ape.editor.controller.Root;
import org.ape.petrinet.model.Element;
import org.ape.petrinet.model.Role;
import org.ape.petrinet.model.Subnet;
import org.ape.petrinet.model.Transition;
import org.ape.petrinet.model.TransitionNode;
import org.ape.util.CollectionTools;
import org.ape.util.GraphicsTools;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Canvas extends JPanel {

	private List<Element> highlightedElements = new ArrayList<Element>();
	private Cursor alternativeCursor;
	public Cursor activeCursor;
	private Root root;

	private BufferedImage fullRoleImage, partialRoleImage, mixedRoleImage;

	public Canvas(Root root) {
		this.root = root;
		setBackground(Color.white);

		fullRoleImage = GraphicsTools
				.getBufferedImage("ape/canvas/fullrole.gif");
		partialRoleImage = GraphicsTools
				.getBufferedImage("ape/canvas/partialrole.gif");
		mixedRoleImage = GraphicsTools
				.getBufferedImage("ape/canvas/mixedrole.gif");

		CanvasListener listener = new CanvasListener(root);
		addMouseListener(listener);
		addMouseMotionListener(listener);
		addMouseWheelListener(listener);

	}

	public Root getRoot() {
		return root;
	}

	public Point getViewTranslation() {
		return new Point(APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getViewTranslation());
	}

	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);
		Point translation = getViewTranslation();
		g.translate(Math.abs(translation.x), Math.abs(translation.y));

		root.getActiveFeature().drawBackground(g);

		root.getActiveFeature().drawMainLayer(g);

		root.getActiveFeature().drawForeground(g);

		drawForeground(g);
	}

	private void drawForeground(Graphics g) {
		Set<TransitionNode> partiallyIncluded = new HashSet<TransitionNode>();
		Set<TransitionNode> fullyIncluded = null;
		Set<Subnet> mixedIncluded = new HashSet<Subnet>();

		for (Role role : APE.getRoot().getRoleEditor().getSelectedElements()) {
			Set<TransitionNode> included = new HashSet<TransitionNode>();

			for (Transition transition : APE.getRoot().getDocument().petriNet
					.getCurrentSubnet().getTransitions()) {
				if (role.transitions.contains(transition)) {
					included.add(transition);
				}
			}
			for (Subnet subnet : APE.getRoot().getDocument().petriNet
					.getCurrentSubnet().getSubnets()) {
				Set<Transition> transitions = subnet
						.getTransitionsRecursively();
				if (role.transitions.containsAll(transitions)) {
					included.add(subnet);
				} else if (CollectionTools.containsAtLeastOne(role.transitions,
						transitions)) {
					mixedIncluded.add(subnet);
				}
			}

			if (fullyIncluded == null) {
				fullyIncluded = new HashSet<TransitionNode>();
				fullyIncluded.addAll(included);
			}
			partiallyIncluded.addAll(included);
			fullyIncluded.retainAll(included);
		}

		if (fullyIncluded != null) {
			partiallyIncluded.removeAll(fullyIncluded);
			for (TransitionNode transition : partiallyIncluded) {
				GraphicsTools.drawImageCentered(g, partialRoleImage,
						transition.getStart().x + transition.getWidth() / 2,
						transition.getStart().y + transition.getHeight() / 2);
			}
			for (TransitionNode transition : fullyIncluded) {
				GraphicsTools.drawImageCentered(g, fullRoleImage,
						transition.getStart().x + transition.getWidth() / 2,
						transition.getStart().y + transition.getHeight() / 2);
			}
		}
		for (Subnet subnet : mixedIncluded) {
			GraphicsTools.drawImageCentered(g, mixedRoleImage,
					subnet.getStart().x + subnet.getWidth() / 2,
					subnet.getStart().y + subnet.getHeight() / 2);
		}

		// draw subnet label
		if (!APE.getRoot().getDocument().petriNet.isCurrentSubnetRoot()) {
			StringBuilder subnetPath = new StringBuilder("Subnet: ");
			for (Subnet subnet : APE.getRoot().getDocument().petriNet
					.getOpenedSubnets()) {
				if (subnet != APE.getRoot().getDocument().petriNet
						.getRootSubnet()) {
					subnetPath.append(subnet.getLabel());
					if (subnet != APE.getRoot().getDocument().petriNet
							.getCurrentSubnet()) {
						subnetPath.append(" > ");
					}
				}
			}
			g.setColor(Color.darkGray);
			g.drawString(subnetPath.toString(), 2, 2 + g.getFontMetrics()
					.getAscent());
		}

	}

	public void setHoverEffects(int x, int y) {

		root.getActiveFeature().setHoverEffects(x, y);
	}

	@Override
	public Dimension getPreferredSize() {
		Rectangle petriNetBounds = APE.getRoot().getDocument().petriNet
				.getCurrentSubnet().getBounds();
		return petriNetBounds.getSize();

	}

	@Override
	public void repaint() {
		revalidate();
		super.repaint();
	}

	public List<Element> getHighlightedElements() {
		return highlightedElements;
	}

	public void setHighlightedElements(List<Element> highlightedElements) {
		this.highlightedElements = highlightedElements;
	}

	public Cursor getAlternativeCursor() {
		return alternativeCursor;
	}

	public void setAlternativeCursor(Cursor alternativeCursor) {
		this.alternativeCursor = alternativeCursor;
	}
}
