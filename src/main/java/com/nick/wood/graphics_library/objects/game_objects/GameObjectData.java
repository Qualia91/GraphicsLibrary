package com.nick.wood.graphics_library.objects.game_objects;

import java.util.ArrayList;
import java.util.UUID;

public class GameObjectData {

	private final UUID uuid;
	private final GameObject gameObject;
	private GameObject parent;
	private final ArrayList<GameObject> children;
	private final ObjectType objectType;
	private boolean delete = false;


	public GameObjectData(GameObject parent, ObjectType objectType, GameObject gameObject) {
		this(UUID.randomUUID(), parent, objectType, gameObject);
	}

	public GameObjectData(UUID uuid, GameObject parent, ObjectType objectType, GameObject gameObject) {
		this.uuid = uuid;
		this.parent = parent;
		if (parent != null) {
			parent.getGameObjectData().getChildren().add(gameObject);
		}
		this.gameObject = gameObject;
		this.children = new ArrayList<>();
		this.objectType = objectType;
	}

	public GameObject getParent() {
		return parent;
	}

	public ArrayList<GameObject> getChildren() {
		return  children;
	}

	public ObjectType getType() {
		return objectType;
	}

	public void addGameObjectNode(GameObject gameObject) {
		children.add(gameObject);
		gameObject.getGameObjectData().setParent(gameObject);
	}

	public void removeGameObjectNode(GameObject gameObject) {
		gameObject.getGameObjectData().setParent(null);
		children.remove(gameObject);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setParent(GameObject parent) {
		this.parent = parent;
		this.parent.getGameObjectData().getChildren().add(gameObject);
	}

	public void delete() {
		this.delete = true;
		for (GameObject child : children) {
			child.getGameObjectData().delete();
		}
	}

	public boolean isDelete() {
		return delete;
	}

	public void undelete() {
		this.delete = false;
		for (GameObject child : children) {
			child.getGameObjectData().undelete();
		}
	}

	public void remove() {
		this.delete = true;
	}
}
