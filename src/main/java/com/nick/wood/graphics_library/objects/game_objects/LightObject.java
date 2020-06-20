package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.lighting.Light;

import java.util.UUID;

public class LightObject implements GameObject {

	private final GameObjectData gameObjectData;
	private final Light light;

	public LightObject(GameObject parent, Light light) {
		this.gameObjectData = new GameObjectData(parent, ObjectType.LIGHT, this);
		this.light = light;
	}

	public LightObject(UUID uuid, GameObject parent, Light light) {
		this.gameObjectData = new GameObjectData(uuid, parent, ObjectType.LIGHT, this);
		this.light = light;
	}

	public Light getLight() {
		return light;
	}

	@Override
	public GameObjectData getGameObjectData() {
		return gameObjectData;
	}
}
