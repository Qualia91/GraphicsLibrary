package com.nick.wood.graphics_library.objects.scene_graph_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class WaterSceneObject implements SceneGraphNode {

	private final SceneGraphNodeData waterSceneGraph;
	private final MeshObject water;


	public WaterSceneObject(SceneGraph parent, String waterTexture, int size, int waterHeight, int cellSize) {
		this.waterSceneGraph = new SceneGraphNodeData(parent, RenderObjectType.WATER, this);

		water = new MeshBuilder()
				.setMeshType(MeshType.WATER)
				.setWaterSquareWidth(size)
				.setWaterHeight(waterHeight)
				.setTexture(waterTexture)
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
