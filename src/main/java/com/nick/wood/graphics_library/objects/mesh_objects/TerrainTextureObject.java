package com.nick.wood.graphics_library.objects.mesh_objects;

public class TerrainTextureObject {

	private final float height;
	private final float transitionWidth;
	private final String texturePath;
	private final String normalPath;

	public TerrainTextureObject(float height, float transitionWidth, String texturePath, String normalPath) {
		this.height = height;
		this.transitionWidth = transitionWidth;
		this.texturePath = texturePath;
		this.normalPath = normalPath;
	}

	public float getHeight() {
		return height;
	}

	public float getTransitionWidth() {
		return transitionWidth;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public String getNormalPath() {
		return normalPath;
	}
}
