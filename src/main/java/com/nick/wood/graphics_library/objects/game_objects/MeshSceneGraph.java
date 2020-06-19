package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;

import java.util.UUID;

public class MeshSceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;
	private MeshObject meshObject;

	public MeshSceneGraph(SceneGraphNode parent, MeshObject meshObject) {
		this.sceneGraphNodeData = new SceneGraphNodeData(parent, RenderObjectType.MESH, this);
		this.meshObject = meshObject;
	}

	public MeshSceneGraph(UUID uuid, SceneGraphNode parent, MeshObject meshObject) {
		this.sceneGraphNodeData = new SceneGraphNodeData(uuid, parent, RenderObjectType.MESH, this);
		this.meshObject = meshObject;
	}

	public MeshObject getMeshObject() {
		return meshObject;
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
	}

	public void removeMeshObject() {
		meshObject = null;
	}

	public void setMeshObject(MeshObject meshObject) {
		this.meshObject = meshObject;
	}
}
