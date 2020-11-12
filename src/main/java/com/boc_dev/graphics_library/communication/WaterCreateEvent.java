package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Renderer;
import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.managers.MaterialManager;
import com.boc_dev.graphics_library.objects.mesh_objects.MeshType;
import com.boc_dev.graphics_library.objects.mesh_objects.Model;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.objects.render_scene.RenderGraph;
import com.boc_dev.maths.objects.matrix.Matrix4f;

import java.io.IOException;
import java.util.UUID;

public class WaterCreateEvent implements RenderUpdateEvent<UUID> {

	private final UUID uuid;
	private final String waterName;
	private final float[][] grid;
	private final String layerName;
	private final Matrix4f globalTransform;
	private final int cellSpace;

	public WaterCreateEvent(UUID uuid, String waterName, float[][] grid, int cellSpace, String layerName, Matrix4f globalTransform) {
		this.uuid = uuid;
		this.waterName = waterName;
		this.grid = grid;
		this.cellSpace = cellSpace;
		this.layerName = layerName;
		this.globalTransform = globalTransform;
	}


	@Override
	public UUID getData() {
		return uuid;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		if (window.getRenderGraphs().get(layerName) == null) {
			window.getRenderGraphs().put(layerName, new RenderGraph());
		}

		if (!window.getRenderGraphs().get(layerName).getWaterMeshes().containsKey(waterName + MaterialManager.WATER_MATERIAL)) {
			window.getMeshManager().createMesh(grid, cellSpace, waterName);
			window.getModelManager().addModel(new Model(waterName, MaterialManager.WATER_MATERIAL));
		}
		window.getRenderGraphs().get(layerName).getWaterMeshes().put(waterName + MaterialManager.WATER_MATERIAL, new InstanceObject(uuid, globalTransform));

	}
}
