package com.nick.wood.graphics_library.objects.scene_graph_objects;

import java.util.ArrayList;
import java.util.UUID;

public class SceneGraphNodeData {

	private final UUID uuid = UUID.randomUUID();
	private final SceneGraphNode sceneGraphNode;
	private SceneGraphNode parent;
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
		this.sceneGraphNode = sceneGraphNode;
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
		sceneGraphNode.getSceneGraphNodeData().setParent(sceneGraphNode);
		addType(sceneGraphNode.getSceneGraphNodeData().getType());
	}

	public void removeGameObjectNode(SceneGraphNode sceneGraphNode) {
		sceneGraphNode.getSceneGraphNodeData().setParent(null);
		children.remove(sceneGraphNode);
		removeType(sceneGraphNode.getSceneGraphNodeData().getType());
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

	public void setParent(SceneGraphNode parent) {
		this.parent = parent;
	}
}
