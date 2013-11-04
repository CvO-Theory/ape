package org.ape.editor.view.canvas;

import java.awt.Graphics;

import org.ape.petrinet.model.DrawingOptions;
import org.ape.petrinet.model.VisualSelection;
import org.ape.petrinet.view.GraphicalElement;
import org.ape.util.GraphicsTools;

public class VisualSelectionRectangle extends GraphicalElement {
	
	private VisualSelection visualSelection;


	public VisualSelectionRectangle(VisualSelection visualSelection) {
		this.visualSelection = visualSelection;
	}


	@Override
	public void draw(Graphics g, DrawingOptions drawingOptions) {
		g.setColor(visualSelection.color);
		GraphicsTools.setDashedStroke(g);
		g.drawRect(Math.min(visualSelection.getStart().x, visualSelection.getEnd().x), Math.min(visualSelection.getStart().y, visualSelection.getEnd().y),visualSelection.getWidth(), visualSelection.getHeight());
		GraphicsTools.setDefaultStroke(g);
	}

}
