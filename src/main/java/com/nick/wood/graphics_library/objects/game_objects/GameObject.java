package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshGroup;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;

public class GameObject {

	private final Transform transform;
	private final MeshGroup meshGroup;
	private boolean isPlayer;

	public GameObject(Vec3d position, Matrix4d rotation, Vec3d scale, MeshGroup meshGroup, boolean isPlayer) {
		this.transform = new Transform(position, scale, rotation);
		this.meshGroup = meshGroup;
		this.isPlayer = isPlayer;
	}

	public void update() {
	}

	public boolean isPlayer() {
		return false;
	}

	public Vec3d getPosition() {
		return transform.getPosition();
	}

	public void setPosition(Vec3d position) {
		this.transform.setPosition(position);
	}

	public Matrix4d getRotation() {
		return transform.getRotation();
	}

	public void setRotation(Matrix4d rotation) {
		transform.setRotation(rotation);
	}

	public Vec3d getScale() {
		return transform.getScale();
	}

	public void setScale(Vec3d scale) {
		transform.setScale(scale);
	}

	public MeshGroup getMeshGroup() {
		return meshGroup;
	}

	public void rotateLeft() {
	}

	public void rotateRight() {
	}
}
