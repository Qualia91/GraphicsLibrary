package com.nick.wood.graphics_library.objects;

import com.nick.wood.graphics_library.frame_buffers.SceneFrameBuffer;
import com.nick.wood.graphics_library.objects.game_objects.CameraType;
import com.nick.wood.maths.objects.matrix.Matrix4f;

public class Camera {
	private final float near;
	private final float far;
	private CameraType cameraType;
	private int width;
	private int height;
	private float fov;
	private Matrix4f projectionMatrix = null;
	private int fboTextureIndex;
	private final SceneFrameBuffer sceneFrameBuffer;

	public Camera(CameraType cameraType, int width, int height, float fov, float near, float far, int fboTextureIndex, SceneFrameBuffer sceneFrameBuffer) {
		this.cameraType = cameraType;
		this.width = width;
		this.height = height;
		this.fov = fov;
		this.near = near;
		this.far = far;
		projectionMatrix = Matrix4f.Projection((float) width/ (float) height, fov, near, far);
		this.fboTextureIndex = fboTextureIndex;
		this.sceneFrameBuffer = sceneFrameBuffer;
	}

	public Camera(float fov, float near, float far) {
		this.fov = fov;
		this.near = near;
		this.far = far;
		this.cameraType = CameraType.PRIMARY;
		this.fboTextureIndex = 0;
		this.sceneFrameBuffer = null;
	}

	public SceneFrameBuffer getSceneFrameBuffer() {
		return sceneFrameBuffer;
	}

	public float getNear() {
		return near;
	}

	public float getFar() {
		return far;
	}

	public float getFov() {
		return fov;
	}

	public CameraType getCameraType() {
		return cameraType;
	}

	public void setCameraType(CameraType cameraType) {
		this.cameraType = cameraType;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}

	public void updateProjectionMatrix(int screenWidth, int screenHeight) {
		this.width = screenWidth;
		this.height = screenHeight;
		if (projectionMatrix == null) {
			projectionMatrix = Matrix4f.Projection((float) width / (float) height, fov, near, far);
		} else {
			projectionMatrix = projectionMatrix.updateProjection((float) width / (float) height, fov);
		}
	}

	public int getFboTextureIndex() {
		return fboTextureIndex;
	}
}
