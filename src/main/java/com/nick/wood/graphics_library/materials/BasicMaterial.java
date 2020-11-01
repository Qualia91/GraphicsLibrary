package com.nick.wood.graphics_library.materials;

import com.nick.wood.graphics_library.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import java.util.UUID;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

public class BasicMaterial implements Material {

	private UUID materialID;
	private String texture;

	public BasicMaterial(UUID materialID, String texture) {
		this.materialID = materialID;
		this.texture = texture;
	}

	@Override
	public void initRender(TextureManager textureManager, Shader shader) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(texture));
	}

	@Override
	public void endRender() {
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glDisable(GL13.GL_TEXTURE0);
	}
}
