package com.nick.wood.graphics_library.objects.game_objects;

public class RootGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;

	public RootGameObject() {

		this.gameObjectNodeData = new GameObjectNodeData(null, GameObjectType.ROOT, this);
	}

	@Override
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}
}
