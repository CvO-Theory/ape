package org.ape.editor.view.canvas;

import java.awt.Graphics;

import org.ape.petrinet.model.DrawingOptions;
import org.ape.petrinet.model.VisualHandle;
import org.ape.petrinet.view.GraphicalElement;

public class VisualHandleRectangle extends GraphicalElement {

	private VisualHandle visualHandle;

	public VisualHandleRectangle(VisualHandle visualHandle) {
		this.visualHandle = visualHandle;
	}

	@Override
	public void draw(Graphics g, DrawingOptions drawingOptions) {
		g.setColor(visualHandle.color);
		g.drawRect(Math.min(visualHandle.getStart().x, visualHandle.getEnd().x), Math.min(visualHandle.getStart().y, visualHandle.getEnd().y), visualHandle.getWidth(), visualHandle.getHeight());

	}

}
