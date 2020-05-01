package com.nick.wood.graphics_library_3d.objects;

import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Transform {

	private Vec3f position, scale;
	private Matrix4f rotation;

	public Transform(Vec3f position, Vec3f scale, Matrix4f rotation) {
		this.position = position;
		this.scale = scale;
		this.rotation = rotation;
	}

	public Matrix4f getTransform() {
		return Matrix4f.Transform(position, rotation, scale);
	}

	public Vec3f getPosition() {
		return position;
	}

	public void setPosition(Vec3f position) {
		this.position = position;
	}

	public Vec3f getScale() {
		return scale;
	}

	public void setScale(Vec3f scale) {
		this.scale = scale;
	}

	public Matrix4f getRotation() {
		return rotation;
	}

	public void setRotation(Matrix4f rotation) {
		this.rotation = rotation;
	}
}
