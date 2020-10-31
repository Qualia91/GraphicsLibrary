package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.mesh_objects.Model;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;
import com.nick.wood.maths.objects.matrix.Matrix4f;

import java.util.ArrayList;
import java.util.UUID;

public class GeometryUpdateEvent implements RenderUpdateEvent<Model> {

	private final UUID uuid;
	private final Model model;
	private final String layerName;
	private final Matrix4f matrix4f;


	public GeometryUpdateEvent(UUID uuid, Model model, String layerName, Matrix4f matrix4f) {
		this.uuid = uuid;
		this.model = model;
		this.layerName = layerName;
		this.matrix4f = matrix4f;
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
		// if model already exists in render graph, find its instance via uuid and update transforms
		if (window.getRenderGraphs().get(layerName).getMeshes().containsKey(model.getStringID())) {
			for (InstanceObject instanceObject : window.getRenderGraphs().get(layerName).getMeshes().get(model.getStringID())) {
				if (instanceObject.getUuid().equals(uuid)) {
					instanceObject.setTransformation(matrix4f);
					return;
				}
			}

			// if iot masde it here, there is not instance of this, so add it
			window.getRenderGraphs().get(layerName).getMeshes().get(model.getStringID()).add(new InstanceObject(uuid, matrix4f));
		}
		// if it does not exist, we need to add model to model manager and add map entry
		else {
			window.getModelManager().addModel(model);
			ArrayList<InstanceObject> instanceObjects = new ArrayList<>();
			instanceObjects.add(new InstanceObject(uuid, matrix4f));
			window.getRenderGraphs().get(layerName).getMeshes().put(model.getStringID(), instanceObjects);
		}
	}
}
