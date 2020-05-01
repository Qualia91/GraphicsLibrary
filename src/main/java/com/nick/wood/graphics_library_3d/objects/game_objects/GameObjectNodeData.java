package com.nick.wood.graphics_library_3d.objects.game_objects;

import java.util.ArrayList;
import java.util.UUID;

public class GameObjectNodeData {

	private final UUID uuid = UUID.randomUUID();
	private final GameObjectNode parent;
	private final ArrayList<GameObjectNode> children;
	private final GameObjectType gameObjectType;
	private int numberOfPlayers;
	private int numberOfMeshes;
	private int numberOfLights;
	private int numberOfCameras;


	public GameObjectNodeData(GameObjectNode parent, GameObjectType gameObjectType, GameObjectNode gameObjectNode) {
		this.parent = parent;
		if (parent != null) {
			parent.getGameObjectNodeData().getChildren().add(gameObjectNode);
			parent.getGameObjectNodeData().addType(gameObjectType);
		}
		this.children = new ArrayList<>();
		this.gameObjectType = gameObjectType;
		this.numberOfPlayers = 0;
		this.numberOfMeshes = 0;
		this.numberOfLights = 0;
		this.numberOfCameras = 0;
	}

	public GameObjectNode getParent() {
		return parent;
	}

	public ArrayList<GameObjectNode> getChildren() {
		return  children;
	}

	public GameObjectType getType() {
		return gameObjectType;
	}

	public void addGameObjectNode(GameObjectNode gameObjectNode) {
		children.add(gameObjectNode);
	}

	public void removeGameObjectNode(GameObjectNode gameObjectNode) {
		children.remove(gameObjectNode);
	}

	public boolean containsPlayer() {
		return numberOfPlayers > 0;
	}

	public boolean containsMeshes() {
		return numberOfMeshes > 0;
	}

	public boolean containsLights() {
		return numberOfLights > 0;
	}

	public boolean containsCameras() {
		return numberOfCameras > 0;
	}

	public void addType(GameObjectType type) {
		switch (type){
			case PLAYER:
				numberOfPlayers++;
				break;
			case LIGHT:
				numberOfLights++;
				break;
			case MESH:
				numberOfMeshes++;
				break;
			case CAMERA:
				numberOfCameras++;
				break;
			default:
				break;
		}
		if (parent != null) {
			parent.getGameObjectNodeData().addType(type);
		}
	}

	public void removeType(GameObjectType type) {
		switch (type){
			case PLAYER:
				numberOfPlayers--;
				break;
			case LIGHT:
				numberOfLights--;
				break;
			case MESH:
				numberOfMeshes--;
				break;
			case CAMERA:
				numberOfCameras--;
				break;
			default:
				break;
		}
		if (parent != null) {
			parent.getGameObjectNodeData().removeType(type);
		}
	}

	public UUID getUuid() {
		return uuid;
	}
}
