package com.boc_dev.graphics_library.objects.materials;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class FboTexture implements Texture {

	private int textureId;

	public FboTexture(int textureId) {
		this.textureId = textureId;
	}

	@Override
	public void setId(int textureId) {
		this.textureId = textureId;
	}

	@Override
	public void destroy() {

	}

	@Override
	public void create() throws IOException {
		glBindTexture(GL_TEXTURE_2D, 0);
		glDeleteTextures(textureId);
	}

	@Override
	public int getId() {
		return textureId;
	}
}
