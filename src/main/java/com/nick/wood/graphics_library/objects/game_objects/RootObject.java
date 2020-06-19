package com.nick.wood.graphics_library.objects.game_objects;

import java.util.UUID;

public class RootObject implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;

	public RootObject() {
		this.sceneGraphNodeData = new SceneGraphNodeData(null, RenderObjectType.ROOT, this);
	}

	public RootObject(UUID uuid) {
		this.sceneGraphNodeData = new SceneGraphNodeData(uuid, null, RenderObjectType.ROOT, this);;
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
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
