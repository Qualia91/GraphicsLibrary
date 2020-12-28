package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.objects.render_scene.RenderGraph;

import java.util.HashSet;

public class PickingUpdateEvent implements RenderUpdateEvent<String> {


	private final String meshStringId;
	private final HashSet<InstanceObject> instanceObjects;
	private final String layerName;

	public PickingUpdateEvent(String meshStringId, HashSet<InstanceObject> instanceObjects, String layerName) {
		this.meshStringId = meshStringId;
		this.instanceObjects = instanceObjects;
		this.layerName = layerName;
	}

	@Override
	public String getData() {
		return meshStringId;
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
		if (window.getRenderGraphs().get(layerName).getPickingMeshes().containsKey(meshStringId)) {

			for (InstanceObject instanceObject : instanceObjects) {
				if (window.getRenderGraphs().get(layerName).getPickingMeshes().get(meshStringId).contains(instanceObject)) {
					window.getRenderGraphs().get(layerName).getPickingMeshes().get(meshStringId).remove(instanceObject);
					window.getRenderGraphs().get(layerName).getPickingMeshes().get(meshStringId).add(instanceObject);
				}
			}
		}
		// if it does not exist, print something because it should exist already
		else {
			System.out.println("No model with id name " + meshStringId + " found. Look into this in PickingUpdateEvent");
		}
	}
}
