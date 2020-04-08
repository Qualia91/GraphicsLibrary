package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.Transform;

public class TransformGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;
	private final Transform transform;

	public TransformGameObject(GameObjectNodeData gameObjectNodeData, Transform transform) {
		this.gameObjectNodeData = gameObjectNodeData;
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
