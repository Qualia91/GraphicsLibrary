package com.nick.wood.graphics_library.mesh_objects;

import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;

public class MeshTransform {

	private Vec3d position, scale;
	private Matrix4d rotation;

	public MeshTransform(Vec3d position, Vec3d scale, Matrix4d rotation) {
		this.position = position;
		this.scale = scale;
		this.rotation = rotation;
	}

	public Matrix4d getTransform() {
		return Matrix4d.Transform(position, rotation, scale);
	}

	public Vec3d getPosition() {
		return position;
	}

	public void setPosition(Vec3d position) {
		this.position = position;
	}

	public Vec3d getScale() {
		return scale;
	}

	public void setScale(Vec3d scale) {
		this.scale = scale;
	}

	public Matrix4d getRotation() {
		return rotation;
	}

	public void setRotation(Matrix4d rotation) {
		this.rotation = rotation;
	}
}
