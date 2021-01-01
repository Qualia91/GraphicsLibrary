package com.boc_dev.graphics_library.objects.text;

import com.boc_dev.maths.objects.matrix.Matrix4f;

public class TextInstance {

	private final String text;
	private final Matrix4f transform;

	public TextInstance(String text, Matrix4f transform) {
		this.text = text;
		this.transform = transform;
	}

	public String getText() {
		return text;
	}

	public Matrix4f getTransformation() {
		return transform;
	}
}
