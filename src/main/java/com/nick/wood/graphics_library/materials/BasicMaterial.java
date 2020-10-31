package com.nick.wood.graphics_library.materials;

import com.nick.wood.graphics_library.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.UUID;

public class BasicMaterial implements Material {

	private UUID materialID;
	private String texture;

	public BasicMaterial(UUID materialID, String texture) {
		this.materialID = materialID;
		this.texture = texture;
	}

	@Override
	public void render(TextureManager textureManager, Shader shader) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(texture));
	}
}
