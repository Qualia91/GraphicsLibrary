package com.nick.wood.graphics_library.objects.render_scene;

import com.nick.wood.maths.objects.matrix.Matrix4f;

import java.util.Objects;
import java.util.UUID;

public class InstanceObject {

	private final UUID uuid;
	private Matrix4f transformation;
	private Matrix4f transformationInverse;

	public InstanceObject(UUID uuid, Matrix4f transformation) {
		this.uuid = uuid;
		this.transformation = transformation;
		this.transformationInverse = transformation.invert();
	}

	public UUID getUuid() {
		return uuid;
	}

	public Matrix4f getTransformation() {
		return transformation;
	}

	public Matrix4f getTransformationInverse() {
		return transformationInverse;
	}

	public void setTransformation(Matrix4f transformation) {
		this.transformation = transformation;
		this.transformationInverse = transformation.invert();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InstanceObject that = (InstanceObject) o;
		return Objects.equals(uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}
}
