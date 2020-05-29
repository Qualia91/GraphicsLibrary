package com.nick.wood.graphics_library.objects.scene_graph_objects;

import com.nick.wood.graphics_library.objects.Camera;

public class CameraSceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;
	private Camera camera;
	private CameraType cameraType;

	public CameraSceneGraph(SceneGraphNode parent, Camera camera, CameraType cameraType) {
		this.sceneGraphNodeData = new SceneGraphNodeData(parent, RenderObjectType.CAMERA, this);
		this.camera = camera;
		this.cameraType = cameraType;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public CameraType getCameraType() {
		return cameraType;
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
	}

}
