package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Renderer;
import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.mesh_objects.InstanceMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.graphics_library.objects.mesh_objects.Model;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

import java.io.IOException;
import java.util.ArrayList;

public class GeometryCreateEvent implements RenderUpdateEvent<Model> {

	private final ArrayList<InstanceObject> instanceObjects;
	private final Model model;
	private final String layerName;

	public GeometryCreateEvent(ArrayList<InstanceObject> instanceObjects, Model model, String layerName) {
		this.instanceObjects = instanceObjects;
		this.model = model;
		this.layerName = layerName;
	}

	@Override
	public Model getData() {
		return model;
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
		if (window.getRenderGraphs().get(layerName).getMeshes().containsKey(model.getStringID())) {
			window.getRenderGraphs().get(layerName).getMeshes().get(model.getStringID()).addAll(instanceObjects);
		}
		// if it does not exist, we need to add model to model manager and add map entry
		else {
			try {
				window.getMeshManager().createMesh(model.getMeshString());
			} catch (IOException e) {
				System.err.println("Mesh " + model.getMeshString() + " not found, using default for model " + model.getStringID());
			}
			window.getModelManager().addModel(model);
			window.getRenderGraphs().get(layerName).getMeshes().put(model.getStringID(), instanceObjects);
		}

		// now check if mesh needs to be converted to instanced model
		if (window.getMeshManager().getMesh(model.getMeshString()).getType().equals(MeshType.SINGLE)) {
			// if instance array is over size limit, convert to an instanced mesh to improve performance
			if (window.getRenderGraphs().get(layerName).getMeshes().get(model.getStringID()).size() > Renderer.INSTANCE_ARRAY_SIZE_LIMIT) {
				window.getMeshManager().convertToInstancedMesh(model.getMeshString());
			}
		}

	}
}
