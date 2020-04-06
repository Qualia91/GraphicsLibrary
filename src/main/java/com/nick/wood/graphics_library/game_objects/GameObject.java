package com.nick.wood.graphics_library.game_objects;

import com.nick.wood.graphics_library.mesh_objects.MeshGroup;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;

public class GameObject {

	private Vec3d position, scale;
	private Matrix4d rotation;
	private final MeshGroup meshGroup;
	private boolean isPlayer;

	public GameObject(Vec3d position, Matrix4d rotation, Vec3d scale, MeshGroup meshGroup, boolean isPlayer) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.meshGroup = meshGroup;
		this.isPlayer = isPlayer;
	}

	public void update() {
	}

	public boolean isPlayer() {
		return false;
	}

	public Vec3d getPosition() {
		return position;
	}

	public void setPosition(Vec3d position) {
		this.position = position;
	}

	public Matrix4d getRotation() {
		return rotation;
	}

	public void setRotation(Matrix4d rotation) {
		this.rotation = rotation;
	}

	public Vec3d getScale() {
		return scale;
	}

	public void setScale(Vec3d scale) {
		this.scale = scale;
	}

	public MeshGroup getMeshGroup() {
		return meshGroup;
	}

	public void rotateLeft() {
	}

	public void rotateRight() {
	}
}
