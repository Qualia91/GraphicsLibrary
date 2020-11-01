package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.mesh_objects.InstanceMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.Model;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

import java.util.ArrayList;

public class GeometryRemoveEvent implements RenderUpdateEvent<Model> {

	private final InstanceObject instanceObject;
	private final Model model;
	private final String layerName;


	public GeometryRemoveEvent(InstanceObject instanceObject, Model model, String layerName) {
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
		// if the layer does not exist, just ignore this as its clearly not being rendered (or something has gone wrong)
		if (window.getRenderGraphs().get(layerName) == null) {
			System.err.println("Geometry " + getData().getMeshString() + " cannot ben removed as layer " + layerName + " does not exist");
			return;
		}

		// remove model instance
		// pretty sure as ive overriden the instance equals and hash method to just look at uuids,
		// remove will remove instance with same uuid
		window.getRenderGraphs().get(layerName).getMeshes().get(model.getStringID()).remove(instanceObject);

		InstanceMesh instanceMesh = (InstanceMesh) window.getMeshManager().getMesh(model.getMeshString());
		instanceMesh.createTransformArray(window.getRenderGraphs().get(layerName).getMeshes().get(model.getStringID()));
	}
}
