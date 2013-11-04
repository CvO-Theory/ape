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

package org.ape.editor.model;

import java.util.HashMap;
import java.util.Observable;

import org.ape.editor.controller.ArcFeature;
import org.ape.editor.controller.Feature;
import org.ape.editor.controller.PlaceFeature;
import org.ape.editor.controller.Root;
import org.ape.editor.controller.SelectFeature;
import org.ape.editor.controller.TokenFeature;
import org.ape.editor.controller.TransitionFeature;
import org.ape.util.FeatureType;

public class ActiveFeature extends Observable {

	private FeatureType activeFeature = FeatureType.SELECT;
	private HashMap<FeatureType, Feature> features = new HashMap<FeatureType, Feature>();

	public ActiveFeature(Root root) {
		setupFeatures(root);
	}

	public Feature getActiveFeature() {
		return features.get(activeFeature);
	}

	public void setActiveFeature(FeatureType activeFeature) {
		this.activeFeature = activeFeature;
		setChanged();
		notifyObservers();
	}

	public FeatureType getActiveFeatureType() {
		return activeFeature;
	}

	private void setupFeatures(Root root) {
		features.put(FeatureType.SELECT, new SelectFeature(root));
		features.put(FeatureType.PLACE, new PlaceFeature(root));
		features.put(FeatureType.TRANSITION, new TransitionFeature(root));
		features.put(FeatureType.ARC, new ArcFeature(root));
		features.put(FeatureType.TOKEN, new TokenFeature(root));
	}

}
