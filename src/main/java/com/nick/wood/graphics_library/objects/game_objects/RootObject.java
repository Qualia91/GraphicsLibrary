package com.nick.wood.graphics_library.objects.game_objects;

import java.util.UUID;

public class RootObject implements GameObject {

	private final GameObjectData gameObjectData;

	public RootObject() {
		this.gameObjectData = new GameObjectData(null, ObjectType.ROOT, this);
	}

	public RootObject(UUID uuid) {
		this.gameObjectData = new GameObjectData(uuid, null, ObjectType.ROOT, this);;
	}

	@Override
	public GameObjectData getGameObjectData() {
		return gameObjectData;
	}

	public void dispose() {
		dispose(gameObjectData);
	}

	private void dispose(GameObjectData gameObjectData) {
		for (GameObject child : gameObjectData.getChildren()) {
			if (child instanceof MeshGameObject) {
				MeshGameObject meshGameObject = (MeshGameObject) child;
				if (meshGameObject.getMeshObject().getMesh().isCreated()) {
					meshGameObject.getMeshObject().getMesh().destroy();
				}
			}
			dispose(child.getGameObjectData());
		}
	}
}
