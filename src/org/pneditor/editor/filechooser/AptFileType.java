package org.pneditor.editor.filechooser;

import java.io.File;

import javax.swing.Icon;

import org.pneditor.petrinet.Document;
import org.pneditor.util.GraphicsTools;

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

}
