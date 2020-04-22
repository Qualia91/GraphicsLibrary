package com.nick.wood.graphics_library.objects;

import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Camera {

	// this is to get world in sensible coordinate system to start with
	private final static Vec3f startingCameraRotation = new Vec3f(-90.0f, 180.0f, 90.0f);

	private final Vec3f initialRot;
	private Vec3f pos;
	private Vec3f rot;
	private float moveSpeed;
	private float sensitivity;
	private float x;
	private float z;
	private float y;

	public Camera(Vec3f pos, Vec3f rot, float moveSpeed, float sensitivity) {
		rot = rot.add(startingCameraRotation);
		this.initialRot = rot;
		this.pos = pos;
		this.rot = rot;
		this.moveSpeed = moveSpeed;
		this.sensitivity = sensitivity;
	}

	public Vec3f getInitialRot() {
		return initialRot;
	}

	public double getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public Vec3f getPos() {

		// get game object to world transformation
		return pos;
	}

	public void setPos(Vec3f pos) {
		this.pos = pos;
	}

	public Vec3f getRot() {
		return rot;
	}

	public void setRot(Vec3f rot) {
		this.rot = rot;
	}

	public void left() {
		pos = pos.add(new Vec3f(x, y, 0.0f));
	}

	public void right() {
		pos = pos.add(new Vec3f(-x, -y, 0.0f));
	}

	public void forward() {
		pos = pos.add(new Vec3f(y, -x, z));
	}

	public void back() {
		pos = pos.add(new Vec3f(-y, x, -z));
	}

	public void up() {
		pos = pos.add(new Vec3f(0.0f, 0.0f, moveSpeed));
	}

	public void down() {
		pos = pos.add(new Vec3f(0.0f, 0.0f, -moveSpeed));
	}

	public void rotate(float dx, float dy) {
		float newX = rot.getX()-dy*sensitivity;
		float newZ = rot.getZ()-dx*sensitivity;

		rot = makeSensible(newX, rot.getY(), newZ);

		this.x = (float) Math.cos(Math.toRadians(rot.getZ())) * moveSpeed;
		this.y = (float) Math.sin(Math.toRadians(rot.getZ())) * moveSpeed;
		this.z = (float) Math.cos(Math.toRadians(rot.getX())) * moveSpeed;
	}

	private Vec3f makeSensible(float x, float y, float z) {
		// to keep z rotation between 0 - 360
		if (z >= 360) {
			z -= 360;
		} else if (z < 0) {
			z += 360;
		}
		// to stop flipping over onto head
		if (x > 0) {
			x = 0;
		}
		if (x < -180) {
			x = -180;
		}
		return new Vec3f(x, y, z);
	}

	public Matrix4f getView(Matrix4f cameraTransform) {

		return Matrix4f.View(cameraTransform.multiply(pos), cameraTransform.rotate(rot));
	}
}
