package com.nick.wood.graphics_library_3d.objects.game_objects;

import com.nick.wood.graphics_library_3d.objects.mesh_objects.MeshObject;

public class MeshGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;
	private final MeshObject meshObject;

	public MeshGameObject(GameObjectNode parent, MeshObject meshObject) {
		this.gameObjectNodeData = new GameObjectNodeData(parent, GameObjectType.MESH, this);
		this.meshObject = meshObject;
	}

	public MeshObject getMeshObject() {
		return meshObject;
	}

	@Override
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}
}
