package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Renderer;
import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.mesh_objects.MeshType;
import com.boc_dev.graphics_library.objects.mesh_objects.Model;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.objects.render_scene.RenderGraph;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PickingCreateEvent implements RenderUpdateEvent<String> {

	private final ArrayList<InstanceObject> instanceObjects;
	private final String meshString;
	private final String layerName;

	public PickingCreateEvent(ArrayList<InstanceObject> instanceObjects, String meshString, String layerName) {
		this.instanceObjects = instanceObjects;
		this.meshString = meshString;
		this.layerName = layerName;
	}

	@Override
	public String getData() {
		return meshString;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		if (window.getRenderGraphs().get(layerName) == null) {
			window.getRenderGraphs().put(layerName, new RenderGraph());
		}
		// if model already exists in render graph, it is already created and so just add instance
		if (window.getRenderGraphs().get(layerName).getPickingMeshes().containsKey(meshString)) {
			window.getRenderGraphs().get(layerName).getPickingMeshes().get(meshString).addAll(instanceObjects);
		}
		// if it does not exist, we need to add model to model manager and add map entry
		else {
			try {
				window.getMeshManager().createMesh(meshString);
			} catch (IOException | URISyntaxException e) {
				System.err.println("Mesh " + meshString + " not found, using default for mesh");
			}
			window.getRenderGraphs().get(layerName).getPickingMeshes().put(meshString, instanceObjects);
		}

		// now check if mesh needs to be converted to instanced model
		if (window.getMeshManager().getMesh(meshString).getType().equals(MeshType.SINGLE)) {
			// if instance array is over size limit, convert to an instanced mesh to improve performance
			if (window.getRenderGraphs().get(layerName).getPickingMeshes().get(meshString).size() > window.getWindowInitialisationParameters().getInstanceArraySizeLimit()) {
				window.getMeshManager().convertToInstancedMesh(meshString);
			}
		}

	}
}
