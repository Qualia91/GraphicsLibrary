package com.nick.wood.graphics_library.objects.game_objects;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RootGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;
	private final ArrayList<RenderObject> changes = new ArrayList<>();

	public RootGameObject() {

		this.gameObjectNodeData = new GameObjectNodeData(null, GameObjectType.ROOT, this);
	}

	@Override
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}

	public ArrayList<RenderObject> getChanges() {
		return changes;
	}
}