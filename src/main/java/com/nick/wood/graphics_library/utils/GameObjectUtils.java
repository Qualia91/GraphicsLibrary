package com.nick.wood.graphics_library.utils;

import com.nick.wood.graphics_library.objects.game_objects.GameObject;

import java.util.ArrayList;
import java.util.UUID;

public class GameObjectUtils {
	public static GameObject FindGameObjectByID(ArrayList<GameObject> gameObjects, UUID uuid) {
		for (GameObject gameObject : gameObjects) {
			if (gameObject.getGameObjectData().getUuid().equals(uuid)) {
				return gameObject;
			} else {
				GameObject sceneGraphNode = FindGameObjectByID(gameObject.getGameObjectData().getChildren(), uuid);
				if (sceneGraphNode != null) {
					return sceneGraphNode;
				}
			}
		}
		return null;
	}
}
