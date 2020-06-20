package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;

public class WaterObject implements GameObject {

	private final GameObjectData waterSceneGraph;
	private final MeshObject water;

	public WaterObject(GameObject parent, String waterTexture, String normalMap, int size, int waterHeight, int cellSize) {
		this.waterSceneGraph = new GameObjectData(parent, ObjectType.WATER, this);

		water = new MeshBuilder()
				.setMeshType(MeshType.WATER)
				.setWaterSquareWidth(size)
				.setWaterHeight(waterHeight)
				.setTexture(waterTexture)
				.setNormalTexture(normalMap)
				.setCellSpace(cellSize)
				.build();

	}

	public MeshObject getWater() {
		return water;
	}

	@Override
	public GameObjectData getGameObjectData() {
		return waterSceneGraph;
	}
}
