package com.nick.wood.graphics_library_3d.objects.scene_graph_objects;

import java.util.ArrayList;
import java.util.UUID;

public class SceneGraphNodeData {

	private final UUID uuid = UUID.randomUUID();
	private final SceneGraphNode parent;
	private final ArrayList<SceneGraphNode> children;
	private final RenderObjectType renderObjectType;
	private int numberOfPlayers;
	private int numberOfMeshes;
	private int numberOfLights;
	private int numberOfCameras;


	public SceneGraphNodeData(SceneGraphNode parent, RenderObjectType renderObjectType, SceneGraphNode sceneGraphNode) {
		this.parent = parent;
		if (parent != null) {
			parent.getSceneGraphNodeData().getChildren().add(sceneGraphNode);
			parent.getSceneGraphNodeData().addType(renderObjectType);
		}
		this.children = new ArrayList<>();
		this.renderObjectType = renderObjectType;
		this.numberOfPlayers = 0;
		this.numberOfMeshes = 0;
		this.numberOfLights = 0;
		this.numberOfCameras = 0;
	}

	public SceneGraphNode getParent() {
		return parent;
	}

	public ArrayList<SceneGraphNode> getChildren() {
		return  children;
	}

	public RenderObjectType getType() {
		return renderObjectType;
	}

	public void addGameObjectNode(SceneGraphNode sceneGraphNode) {
		children.add(sceneGraphNode);
	}

	public void removeGameObjectNode(SceneGraphNode sceneGraphNode) {
		children.remove(sceneGraphNode);
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

	public void addType(RenderObjectType type) {
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
			parent.getSceneGraphNodeData().addType(type);
		}
	}

	public void removeType(RenderObjectType type) {
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
			parent.getSceneGraphNodeData().removeType(type);
		}
	}

	public UUID getUuid() {
		return uuid;
	}
}
