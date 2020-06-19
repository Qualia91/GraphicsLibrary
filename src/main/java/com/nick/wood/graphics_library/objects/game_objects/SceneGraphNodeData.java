package com.nick.wood.graphics_library.objects.game_objects;

import java.util.ArrayList;
import java.util.UUID;

public class SceneGraphNodeData {

	private final UUID uuid;
	private final SceneGraphNode sceneGraphNode;
	private SceneGraphNode parent;
	private final ArrayList<SceneGraphNode> children;
	private final RenderObjectType renderObjectType;
	private boolean delete = false;


	public SceneGraphNodeData(SceneGraphNode parent, RenderObjectType renderObjectType, SceneGraphNode sceneGraphNode) {
		this(UUID.randomUUID(), parent, renderObjectType, sceneGraphNode);
	}

	public SceneGraphNodeData(UUID uuid, SceneGraphNode parent, RenderObjectType renderObjectType, SceneGraphNode sceneGraphNode) {
		this.uuid = uuid;
		this.parent = parent;
		if (parent != null) {
			parent.getSceneGraphNodeData().getChildren().add(sceneGraphNode);
		}
		this.sceneGraphNode = sceneGraphNode;
		this.children = new ArrayList<>();
		this.renderObjectType = renderObjectType;
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
	}

	public void removeGameObjectNode(SceneGraphNode sceneGraphNode) {
		sceneGraphNode.getSceneGraphNodeData().setParent(null);
		children.remove(sceneGraphNode);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setParent(SceneGraphNode parent) {
		this.parent = parent;
	}

	public void delete() {
		this.delete = true;
		for (SceneGraphNode child : children) {
			child.getSceneGraphNodeData().delete();
		}
	}

	public boolean isDelete() {
		return delete;
	}

	public void undelete() {
		this.delete = false;
		for (SceneGraphNode child : children) {
			child.getSceneGraphNodeData().undelete();
		}
	}

	public void remove() {
		this.delete = true;
	}
}
