package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;

public class WaterSceneObject implements SceneGraphNode {

	private final SceneGraphNodeData waterSceneGraph;
	private final MeshObject water;

	public WaterSceneObject(SceneGraphNode parent, String waterTexture, String normalMap, int size, int waterHeight, int cellSize) {
		this.waterSceneGraph = new SceneGraphNodeData(parent, RenderObjectType.WATER, this);

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
	public SceneGraphNodeData getSceneGraphNodeData() {
		return waterSceneGraph;
	}
}
