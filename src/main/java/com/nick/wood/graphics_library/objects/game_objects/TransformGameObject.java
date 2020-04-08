package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.Transform;

public class TransformGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;
	private final Transform transform;

	public TransformGameObject(GameObjectNode parent, Transform transform) {
		this.gameObjectNodeData = new GameObjectNodeData(parent, GameObjectType.TRANSFORM, this);
		this.transform = transform;
	}

	public Transform getTransform() {
		return transform;
	}

	@Override
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}
}
