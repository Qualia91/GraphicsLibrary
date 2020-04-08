package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.Camera;

public class CameraGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;
	private Camera camera;
	private CameraType cameraType;

	public CameraGameObject(GameObjectNode parent, Camera camera, CameraType cameraType) {
		this.gameObjectNodeData = new GameObjectNodeData(parent, GameObjectType.CAMERA, this);
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
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}
}
