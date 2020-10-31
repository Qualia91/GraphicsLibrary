package com.nick.wood.graphics_library.materials;

import java.util.HashMap;
import java.util.UUID;

public class MaterialManager {

	private final HashMap<UUID, Material> idToMaterialMap = new HashMap<>();
	private Material defaultMaterial;

	public Material getMaterial(UUID materialID) {
		return idToMaterialMap.getOrDefault(materialID, defaultMaterial);
	}

	public void create(String defaultTexturePath, UUID defaultMaterialId) {
		this.defaultMaterial = new BasicMaterial(defaultMaterialId, defaultTexturePath);
	}

	public void destroy() {
		// do nothing
	}

	public HashMap<UUID, Material> getIdToMaterialMap() {
		return idToMaterialMap;
	}

	public void addMaterial(UUID uuid, Material material) {
		idToMaterialMap.put(uuid, material);
	}
}
