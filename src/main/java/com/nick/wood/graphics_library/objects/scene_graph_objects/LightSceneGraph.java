package com.nick.wood.graphics_library.objects.scene_graph_objects;

import com.nick.wood.graphics_library.lighting.Light;

public class LightSceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;
	private final Light light;

	public LightSceneGraph(SceneGraphNode parent, Light light) {
		this.sceneGraphNodeData = new SceneGraphNodeData(parent, RenderObjectType.LIGHT, this);
		this.light = light;
	}

	public Light getLight() {
		return light;
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
	}
}
