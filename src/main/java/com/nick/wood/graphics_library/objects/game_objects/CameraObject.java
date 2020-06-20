package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.Camera;

import java.util.UUID;

public class CameraObject implements GameObject {

	private final GameObjectData gameObjectData;
	private Camera camera;
	private CameraType cameraType;

	public CameraObject(GameObject parent, Camera camera) {
		this.gameObjectData = new GameObjectData(parent, ObjectType.CAMERA, this);
		this.camera = camera;
		this.cameraType = camera.getCameraType();
	}

	public CameraObject(UUID uuid, GameObject parent, Camera camera) {
		this.gameObjectData = new GameObjectData(uuid, parent, ObjectType.CAMERA, this);
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
	public GameObjectData getGameObjectData() {
		return gameObjectData;
	}

}
