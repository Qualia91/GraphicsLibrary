package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.maths.objects.Matrix4d;

import java.util.UUID;

public class RenderObject<T> {

	private final T object;
	private final UUID uuid;
	private Matrix4d transform;

	public RenderObject(T object, Matrix4d transform, UUID uuid) {
		this.object = object;
		this.transform = transform;
		this.uuid = uuid;
	}

	public T getObject() {
		return object;
	}

	public Matrix4d getTransform() {
		return transform;
	}

	public void setTransform(Matrix4d transform) {
		this.transform = transform;
	}

	public UUID getUuid() {
		return uuid;
	}

}
