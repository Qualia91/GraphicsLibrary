package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.mesh_objects.InstanceMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.Model;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

import java.io.IOException;
import java.util.ArrayList;

public class GeometryCreateEvent implements RenderUpdateEvent<Model> {

	private final InstanceObject instanceObject;
	private final Model model;
	private final String layerName;


	public GeometryCreateEvent(InstanceObject instanceObject, Model model, String layerName) {
		this.instanceObject = instanceObject;
		this.model = model;
		this.layerName = layerName;
	}

	public InstanceObject getInstanceObject() {
		return instanceObject;
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
			window.getRenderGraphs().get(layerName).getMeshes().get(model.getStringID()).add(instanceObject);
			InstanceMesh instanceMesh = (InstanceMesh) window.getMeshManager().getMesh(model.getMeshString());
			instanceMesh.createTransformArray(window.getRenderGraphs().get(layerName).getMeshes().get(model.getStringID()));
		}
		// if it does not exist, we need to add model to model manager and add map entry
		else {
			try {
				window.getMeshManager().createMesh(model.getMeshString());
			} catch (IOException e) {
				System.err.println("Mesh " + model.getMeshString() + " not found, using default for model " + model.getStringID());
			}
			window.getModelManager().addModel(model);
			ArrayList<InstanceObject> instanceObjects = new ArrayList<>();
			instanceObjects.add(instanceObject);
			window.getRenderGraphs().get(layerName).getMeshes().put(model.getStringID(), instanceObjects);
			InstanceMesh instanceMesh = (InstanceMesh) window.getMeshManager().getMesh(model.getMeshString());
			instanceMesh.createTransformArray(window.getRenderGraphs().get(layerName).getMeshes().get(model.getStringID()));
		}
	}
}
