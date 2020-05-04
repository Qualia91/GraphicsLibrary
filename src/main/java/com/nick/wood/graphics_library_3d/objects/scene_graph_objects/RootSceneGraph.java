package com.nick.wood.graphics_library_3d.objects.scene_graph_objects;

import java.util.ArrayList;

public class RootSceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;
	private final ArrayList<RenderObject> changes = new ArrayList<>();

	public RootSceneGraph() {

		this.sceneGraphNodeData = new SceneGraphNodeData(null, RenderObjectType.ROOT, this);
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
	}

	public ArrayList<RenderObject> getChanges() {
		return changes;
	}
}
