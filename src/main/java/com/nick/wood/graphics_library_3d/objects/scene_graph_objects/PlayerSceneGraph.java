package com.nick.wood.graphics_library_3d.objects.scene_graph_objects;

public class PlayerSceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;

	public PlayerSceneGraph(SceneGraphNode parent) {

		this.sceneGraphNodeData = new SceneGraphNodeData(parent, RenderObjectType.PLAYER, this);
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
	}

}
