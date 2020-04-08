package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.lighting.Light;

public class LightGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;
	private final Light light;

	public LightGameObject(GameObjectNode parent, Light light) {
		this.gameObjectNodeData = new GameObjectNodeData(parent, GameObjectType.LIGHT, this);
		this.light = light;
	}

	public Light getLight() {
		return light;
	}

	@Override
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}
}
