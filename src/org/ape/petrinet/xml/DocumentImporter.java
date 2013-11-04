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

package org.ape.petrinet.xml;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;

import org.ape.petrinet.model.Arc;
import org.ape.petrinet.model.Document;
import org.ape.petrinet.model.Marking;
import org.ape.petrinet.model.Node;
import org.ape.petrinet.model.Place;
import org.ape.petrinet.model.PlaceNode;
import org.ape.petrinet.model.ReferenceArc;
import org.ape.petrinet.model.ReferencePlace;
import org.ape.petrinet.model.Role;
import org.ape.petrinet.model.Subnet;
import org.ape.petrinet.model.Transition;
import org.ape.util.Xslt;

/**
 * 
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DocumentImporter {

	private XmlDocument xmlDocument;
	private IdToXmlObject idToXmlObject;

	public Document readFromFile(File file) throws JAXBException,
			FileNotFoundException, IOException {
		JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
		Unmarshaller u = ctx.createUnmarshaller();
		FileInputStream fileInputStream = new FileInputStream(file);
		xmlDocument = (XmlDocument) u.unmarshal(fileInputStream);
		fileInputStream.close();
		idToXmlObject = new IdToXmlObject(xmlDocument);
		return getDocument();
	}

	public Document readFromFileWithXslt(File file, InputStream xslt)
			throws JAXBException, IOException, TransformerException {
		if (xslt == null) {
			return readFromFile(file);
		}
		JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
		Unmarshaller u = ctx.createUnmarshaller();
		File transformedXml = Xslt.transformXml(file, xslt,
				File.createTempFile("ape-import", null));
		xmlDocument = (XmlDocument) u.unmarshal(transformedXml);
		idToXmlObject = new IdToXmlObject(xmlDocument);
		transformedXml.delete(); // delete temp file
		return getDocument();
	}

	private Document getDocument() {
		Document document = new Document();
		Subnet rootSubnet = getNewSubnet(xmlDocument.rootSubnet);
		document.petriNet.setRootSubnet(rootSubnet);
		constructInitialMarkingRecursively(
				document.petriNet.getInitialMarking(), xmlDocument.rootSubnet);

		document.petriNet.getNodeSimpleIdGenerator().fixFutureUniqueIds();
		document.petriNet.getNodeSimpleIdGenerator().ensureNumberIds();
		document.petriNet.getNodeLabelGenerator().fixFutureUniqueLabels();

		for (XmlRole xmlRole : xmlDocument.roles) {
			Role role = new Role();
			role.id = xmlRole.id;
			role.name = xmlRole.name;
			role.createCase = xmlRole.createCase;
			role.destroyCase = xmlRole.destroyCase;
			for (String transitionId : xmlRole.transitionIds) {
				role.transitions
						.add((Transition) getObjectFromId(transitionId));
			}
			document.roles.add(role);
		}
		return document;
	}

	private Object getObjectFromId(String id) {
		return getObject(idToXmlObject.getXmlObject(id));
	}

	private Map<Object, Object> cachedObjects = new HashMap<Object, Object>();

	private Object getObject(Object xmlObject) {
		if (cachedObjects.containsKey(xmlObject)) {
			return cachedObjects.get(xmlObject);
		}
		Object object = null;
		if (xmlObject instanceof XmlArc) {
			object = getNewArc((XmlArc) xmlObject);
		}
		if (xmlObject instanceof XmlPlace) {
			object = getNewPlace((XmlPlace) xmlObject);
		}
		if (xmlObject instanceof XmlTransition) {
			object = getNewTransition((XmlTransition) xmlObject);
		}
		if (xmlObject instanceof XmlReferencePlace) {
			object = getNewReferencePlace((XmlReferencePlace) xmlObject);
		}
		if (xmlObject instanceof XmlSubnet) {
			object = getNewSubnet((XmlSubnet) xmlObject);
		}
		if (xmlObject instanceof XmlReferenceArc) {
			object = getNewReferenceArc((XmlReferenceArc) xmlObject);
		}

		if (object != null) {
			cachedObjects.put(xmlObject, object);
		}
		return object;
	}

	private Subnet getNewSubnet(XmlSubnet xmlSubnet) {
		Subnet subnet = new Subnet();
		subnet.setId(xmlSubnet.id);
		subnet.setLabel(xmlSubnet.label);
		subnet.setCenter(xmlSubnet.x, xmlSubnet.y);
		subnet.setColor(new Color(xmlSubnet.color));
		for (XmlArc xmlArc : xmlSubnet.arcs) {
			subnet.addElement((Arc) getObject(xmlArc));
		}
		for (XmlPlace xmlPlace : xmlSubnet.places) {
			subnet.addElement((Place) getObject(xmlPlace));
		}
		for (XmlTransition xmlTransition : xmlSubnet.transitions) {
			subnet.addElement((Transition) getObject(xmlTransition));
		}
		for (XmlReferencePlace xmlReferencePlace : xmlSubnet.referencePlaces) {
			subnet.addElement((ReferencePlace) getObject(xmlReferencePlace));
		}
		for (XmlReferenceArc xmlReferenceArc : xmlSubnet.referenceArcs) {
			subnet.addElement((ReferenceArc) getObject(xmlReferenceArc));
		}
		for (XmlSubnet xmlSubSubnet : xmlSubnet.subnets) {
			subnet.addElement((Subnet) getObject(xmlSubSubnet));
		}
		return subnet;
	}

	private void constructInitialMarkingRecursively(Marking marking,
			XmlSubnet xmlSubnet) {
		for (XmlPlace xmlPlace : xmlSubnet.places) {
			marking.setTokens((PlaceNode) getObject(xmlPlace), xmlPlace.tokens);
		}
		for (XmlSubnet xmlSubSubnet : xmlSubnet.subnets) {
			constructInitialMarkingRecursively(marking, xmlSubSubnet);
		}
	}

	private Arc getNewArc(XmlArc xmlArc) {
		Node source = (Node) getObjectFromId(xmlArc.sourceId);
		Node destination = (Node) getObjectFromId(xmlArc.destinationId);
		Arc arc = new Arc(source, destination);
		arc.setMultiplicity(xmlArc.multiplicity);
		List<Point> breakPoints = new LinkedList<Point>();
		for (XmlPoint xmlPoint : xmlArc.breakPoints) {
			breakPoints.add(new Point(xmlPoint.x, xmlPoint.y));
		}
		arc.setBreakPoints(breakPoints);
		arc.setColor(new Color(xmlArc.color));
		return arc;
	}

	private Place getNewPlace(XmlPlace xmlPlace) {
		Place place = new Place();
		place.setId(xmlPlace.id);
		place.setLabel(xmlPlace.label);
		place.setStatic(xmlPlace.isStatic);
		place.setCenter(xmlPlace.x, xmlPlace.y);
		place.setColor(new Color(xmlPlace.color));
		return place;
	}

	private Transition getNewTransition(XmlTransition xmlTransition) {
		Transition transition = new Transition();
		transition.setId(xmlTransition.id);
		transition.setLabel(xmlTransition.label);
		transition.setCenter(xmlTransition.x, xmlTransition.y);
		transition.setColor(new Color(xmlTransition.color));
		return transition;
	}

	private ReferencePlace getNewReferencePlace(XmlReferencePlace xmlReference) {
		PlaceNode connectedPlaceNode = (PlaceNode) getObjectFromId(xmlReference.connectedPlaceId);
		ReferencePlace referencePlace = new ReferencePlace(connectedPlaceNode);
		referencePlace.setId(xmlReference.id);
		referencePlace.setCenter(xmlReference.x, xmlReference.y);
		referencePlace.setColor(new Color(xmlReference.color));
		return referencePlace;
	}

	private ReferenceArc getNewReferenceArc(XmlReferenceArc xmlReferenceArc) {
		PlaceNode placeNode = (PlaceNode) getObjectFromId(xmlReferenceArc.placeId);
		Subnet subnet = (Subnet) getObjectFromId(xmlReferenceArc.subnetId);
		ReferenceArc referenceArc = new ReferenceArc(placeNode, subnet);

		List<Point> breakPoints = new LinkedList<Point>();
		for (XmlPoint xmlPoint : xmlReferenceArc.breakPoints) {
			breakPoints.add(new Point(xmlPoint.x, xmlPoint.y));
		}
		referenceArc.setBreakPoints(breakPoints);
		referenceArc.setColor(new Color(xmlReferenceArc.color));
		return referenceArc;
	}

}
