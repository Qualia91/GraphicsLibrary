package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Renderer;
import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.mesh_objects.MeshType;

import java.util.HashSet;
import java.util.UUID;

public class PickingRemoveEvent implements RenderUpdateEvent<String> {

	private final HashSet<UUID> uuids;
	private final String meshString;
	private final String layerName;


	public PickingRemoveEvent(HashSet<UUID> uuids, String meshString, String layerName) {
		this.uuids = uuids;
		this.meshString = meshString;
		this.layerName = layerName;
	}

	@Override
	public String getData() {
		return meshString;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.DESTROY;
	}

	public void applyToGraphicsEngine(Window window) {
		// if the layer does not exist, just ignore this as its clearly not being rendered (or something has gone wrong)
		if (window.getRenderGraphs().get(layerName) == null) {
			System.err.println("Mesh " + meshString + " cannot ben removed as layer " + layerName + " does not exist");
			return;
		}

		// remove model instance
		if (window.getRenderGraphs().get(layerName).getPickingMeshes().get(meshString) != null) {
			for (UUID uuid : uuids) {
				window.getRenderGraphs().get(layerName).getPickingMeshes().get(meshString).removeIf(ins -> ins.getUuid().equals(uuid));
			}
			// now check if mesh needs to be converted to instanced model
			if (window.getMeshManager().getMesh(meshString).getType().equals(MeshType.INSTANCED)) {
				// if instance array is over size limit, convert to an instanced mesh to improve performance
				if (window.getRenderGraphs().get(layerName).getPickingMeshes().get(meshString).size() < window.getWindowInitialisationParameters().getInstanceArraySizeLimit()) {
					window.getMeshManager().convertToSingleMesh(meshString);
				}
			}
		}

	}
}
