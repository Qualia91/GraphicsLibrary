package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Renderer;
import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.mesh_objects.InstanceMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.graphics_library.objects.mesh_objects.Model;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class GeometryRemoveEvent implements RenderUpdateEvent<String> {

	private final HashSet<UUID> uuids;
	private final String modelString;
	private final String layerName;


	public GeometryRemoveEvent(HashSet<UUID> uuids, String modelString, String layerName) {
		this.uuids = uuids;
		this.modelString = modelString;
		this.layerName = layerName;
	}

	@Override
	public String getData() {
		return modelString;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.DESTROY;
	}

	public void applyToGraphicsEngine(Window window) {
		// if the layer does not exist, just ignore this as its clearly not being rendered (or something has gone wrong)
		if (window.getRenderGraphs().get(layerName) == null) {
			System.err.println("Geometry " + modelString + " cannot ben removed as layer " + layerName + " does not exist");
			return;
		}

		// remove model instance
		if (window.getRenderGraphs().get(layerName).getMeshes().get(modelString) != null) {
			for (UUID uuid : uuids) {
				window.getRenderGraphs().get(layerName).getMeshes().get(modelString).removeIf(ins -> ins.getUuid().equals(uuid));
			}
		}

		if (window.getRenderGraphs().get(layerName).getMeshes().get(modelString) != null) {
			// now check if mesh needs to be converted to instanced model
			if (window.getMeshManager().getMesh(window.getModelManager().getModel(modelString).getMeshString()).getType().equals(MeshType.INSTANCED)) {
				// if instance array is over size limit, convert to an instanced mesh to improve performance
				if (window.getRenderGraphs().get(layerName).getMeshes().get(modelString).size() < Renderer.INSTANCE_ARRAY_SIZE_LIMIT) {
					window.getMeshManager().convertToSingleMesh(window.getModelManager().getModel(modelString).getMeshString());
				}
			}
		}

	}
}
