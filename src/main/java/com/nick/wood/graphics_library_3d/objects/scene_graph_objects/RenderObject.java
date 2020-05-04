package com.nick.wood.graphics_library_3d.objects.scene_graph_objects;

import com.nick.wood.maths.objects.matrix.Matrix4f;

import java.util.UUID;

public class RenderObject<T> {

	private final T object;
	private final UUID uuid;
	private Matrix4f transform;

	public RenderObject(T object, Matrix4f transform, UUID uuid) {
		this.object = object;
		this.transform = transform;
		this.uuid = uuid;
	}

	public T getObject() {
		return object;
	}

	public Matrix4f getTransform() {
		return transform;
	}

	public void setTransform(Matrix4f transform) {
		this.transform = transform;
	}

	public UUID getUuid() {
		return uuid;
	}

}
