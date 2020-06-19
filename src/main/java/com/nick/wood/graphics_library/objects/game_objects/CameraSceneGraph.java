package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.Camera;

import java.util.UUID;

public class CameraSceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;
	private Camera camera;
	private CameraType cameraType;

	public CameraSceneGraph(SceneGraphNode parent, Camera camera) {
		this.sceneGraphNodeData = new SceneGraphNodeData(parent, RenderObjectType.CAMERA, this);
		this.camera = camera;
		this.cameraType = camera.getCameraType();
	}

	public CameraSceneGraph(UUID uuid, SceneGraphNode parent, Camera camera) {
		this.sceneGraphNodeData = new SceneGraphNodeData(uuid, parent, RenderObjectType.CAMERA, this);
		this.camera = camera;
		this.cameraType = camera.getCameraType();
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
