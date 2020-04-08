package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshGroup;

public class MeshGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;
	private final MeshGroup meshGroup;

	public MeshGameObject(GameObjectNode parent, MeshGroup meshGroup) {
		this.gameObjectNodeData = new GameObjectNodeData(parent, GameObjectType.MESH, this);
		this.meshGroup = meshGroup;
	}

	public MeshGroup getMeshGroup() {
		return meshGroup;
	}

	@Override
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}
}
