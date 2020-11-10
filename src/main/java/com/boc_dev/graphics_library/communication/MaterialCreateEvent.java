package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.materials.Material;

import java.util.UUID;

public class MaterialCreateEvent implements RenderUpdateEvent<Material> {

	private final UUID uuid;
	private final Material material;
	private final String layerName;


	public MaterialCreateEvent(UUID uuid, Material material, String layerName) {
		this.uuid = uuid;
		this.material = material;
		this.layerName = layerName;
	}

	@Override
	public Material getData() {
		return material;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		// add material to material manager
		window.getMaterialManager().addMaterial(uuid, material);
	}
}
