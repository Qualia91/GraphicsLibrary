package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.materials.Material;
import com.nick.wood.graphics_library.materials.NormalMaterial;
import com.nick.wood.graphics_library.objects.mesh_objects.Model;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

import java.io.IOException;
import java.util.ArrayList;
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
