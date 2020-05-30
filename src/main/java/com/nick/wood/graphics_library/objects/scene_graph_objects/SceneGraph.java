package com.nick.wood.graphics_library.objects.scene_graph_objects;

import java.util.ArrayList;

public class SceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;
	private final ArrayList<RenderObject> changes = new ArrayList<>();

	public SceneGraph() {
		this.sceneGraphNodeData = new SceneGraphNodeData(null, RenderObjectType.ROOT, this);
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
	}

	public ArrayList<RenderObject> getChanges() {
		return changes;
	}

	public void dispose() {
		dispose(sceneGraphNodeData);
	}

	private void dispose(SceneGraphNodeData sceneGraphNodeData) {
		for (SceneGraphNode child : sceneGraphNodeData.getChildren()) {
			if (child instanceof MeshSceneGraph) {
				MeshSceneGraph meshSceneGraph = (MeshSceneGraph) child;
				if (meshSceneGraph.getMeshObject().getMesh().isCreated()) {
					meshSceneGraph.getMeshObject().getMesh().destroy();
				}
			}
			dispose(child.getSceneGraphNodeData());
		}
	}
}
