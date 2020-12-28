package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.render_scene.RenderGraph;

import java.util.*;

public class GeometryUpdateEvent implements RenderUpdateEvent<String> {


	private final String geometryStringId;
	private final HashSet<InstanceObject> instanceObjects;
	private final String layerName;

	public GeometryUpdateEvent(String geometryStringId, HashSet<InstanceObject> instanceObjects, String layerName) {
		this.geometryStringId = geometryStringId;
		this.instanceObjects = instanceObjects;
		this.layerName = layerName;
	}

	@Override
	public String getData() {
		return geometryStringId;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.TRANSFORM_UPDATE;
	}

	public void applyToGraphicsEngine(Window window) {
		if (window.getRenderGraphs().get(layerName) == null) {
			window.getRenderGraphs().put(layerName, new RenderGraph());
		}
		// if model already exists in render graph, find its instance via uuid and update transforms
		if (window.getRenderGraphs().get(layerName).getMeshes().containsKey(geometryStringId)) {

			for (InstanceObject instanceObject : instanceObjects) {
				if (window.getRenderGraphs().get(layerName).getMeshes().get(geometryStringId).contains(instanceObject)) {
					window.getRenderGraphs().get(layerName).getMeshes().get(geometryStringId).remove(instanceObject);
					window.getRenderGraphs().get(layerName).getMeshes().get(geometryStringId).add(instanceObject);
				}
			}
		}
		// if it does not exist, print something because it should exist already
		else {
			System.out.println("No model with id name " + geometryStringId + " found in layer " + layerName + ". Look into this in GeometryUpdateEvent");
		}
	}
}
