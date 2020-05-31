package com.nick.wood.graphics_library.objects.scene_graph_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;

public class SkyBox implements SceneGraphNode {

	private final SceneGraphNodeData skyboxSceneGraph;
	private final MeshObject skybox;

	public SkyBox(SceneGraph parent, MeshObject skybox) {
		this.skyboxSceneGraph = new SceneGraphNodeData(parent, RenderObjectType.SKYBOX, this);
		this.skybox = skybox;
	}

	public SceneGraphNodeData getSkyboxSceneGraph() {
		return skyboxSceneGraph;
	}

	public MeshObject getSkybox() {
		return skybox;
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return skyboxSceneGraph;
	}
}
