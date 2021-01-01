package com.boc_dev.graphics_library.objects.managers;

import com.boc_dev.graphics_library.objects.materials.BasicMaterial;
import com.boc_dev.graphics_library.objects.materials.Material;
import com.boc_dev.graphics_library.objects.materials.WaterMaterial;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.HashMap;
import java.util.UUID;

public class MaterialManager {

	public static final UUID WATER_MATERIAL = UUID.randomUUID();
	public static final UUID TEXT_MATERIAL = UUID.randomUUID();
	private final HashMap<UUID, Material> idToMaterialMap = new HashMap<>();
	private Material defaultMaterial;
	private WaterMaterial waterMaterial = new WaterMaterial(
			WATER_MATERIAL,
			"/textures/waterDuDvMap.jpg",
			"/normalMaps/waterNormalMap.jpg");

	public Material getMaterial(UUID materialID) {
//		System.out.println("Materials: " + idToMaterialMap.size());
//		idToMaterialMap.forEach((uuid, material) -> System.out.println(uuid + " " + material));
		return idToMaterialMap.getOrDefault(materialID, defaultMaterial);
	}

	public void create(String defaultTexturePath, UUID defaultMaterialId) {
		this.defaultMaterial = new BasicMaterial(defaultMaterialId, defaultTexturePath);

		addMaterial(WATER_MATERIAL, waterMaterial);
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
