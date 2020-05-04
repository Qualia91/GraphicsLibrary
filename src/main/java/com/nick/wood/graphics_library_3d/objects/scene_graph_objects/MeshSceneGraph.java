package com.nick.wood.graphics_library_3d.objects.scene_graph_objects;

import com.nick.wood.graphics_library_3d.objects.mesh_objects.MeshObject;

public class MeshSceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;
	private final MeshObject meshObject;

	public MeshSceneGraph(SceneGraphNode parent, MeshObject meshObject) {
		this.sceneGraphNodeData = new SceneGraphNodeData(parent, RenderObjectType.MESH, this);
		this.meshObject = meshObject;
	}

	public MeshObject getMeshObject() {
		return meshObject;
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
	}
}
