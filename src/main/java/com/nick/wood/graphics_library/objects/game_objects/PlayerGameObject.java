package com.nick.wood.graphics_library.objects.game_objects;

public class PlayerGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;

	public PlayerGameObject(GameObjectNode parent) {

		this.gameObjectNodeData = new GameObjectNodeData(parent, GameObjectType.MESH, this);
	}

	@Override
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}

}
