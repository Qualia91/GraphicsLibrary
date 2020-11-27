package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Renderer;
import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.mesh_objects.MeshType;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

		// while i would have liked to only delete meshes using the modelString id, because of the auto instancing using
		// mesh and material, if material is changed, this will not be correct
		// Therefore i have to iterate over all the mesh lists and remove it

		for (UUID uuid : uuids) {

			Iterator<ArrayList<InstanceObject>> iterator = window.getRenderGraphs().get(layerName).getMeshes().values().iterator();

			while (iterator.hasNext()) {

				ArrayList<InstanceObject> next = iterator.next();

				next.removeIf(ins -> ins.getUuid().equals(uuid));

				if (next.isEmpty()) {
					iterator.remove();
				}

			}

		}
		// now check if mesh needs to be converted to instanced model
		if (window.getMeshManager().getMesh(window.getModelManager().getModel(modelString).getMeshString()).getType().equals(MeshType.INSTANCED)) {
			// if instance array is over size limit, convert to an instanced mesh to improve performance
			// todo wut is happening here?
			if (window.getRenderGraphs().get(layerName).getMeshes().get(modelString) != null) {
				if (window.getRenderGraphs().get(layerName).getMeshes().get(modelString).size() < Renderer.INSTANCE_ARRAY_SIZE_LIMIT) {
					window.getMeshManager().convertToSingleMesh(window.getModelManager().getModel(modelString).getMeshString());
				}
			}
		}

	}
}
