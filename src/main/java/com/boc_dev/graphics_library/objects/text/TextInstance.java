package com.boc_dev.graphics_library.objects.text;

import com.boc_dev.maths.objects.matrix.Matrix4f;

import java.util.Objects;
import java.util.UUID;

public class TextInstance {

	private final UUID uuid;
	private final String text;
	private final Matrix4f transform;

	public TextInstance(UUID uuid, String text, Matrix4f transform) {
		this.uuid = uuid;
		this.text = text;
		this.transform = transform;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getText() {
		return text;
	}

	public Matrix4f getTransformation() {
		return transform;
	}

}
