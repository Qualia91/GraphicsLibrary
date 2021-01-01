package com.boc_dev.graphics_library.objects.text;

import com.boc_dev.graphics_library.Shader;
import com.boc_dev.graphics_library.objects.managers.TextureManager;
import com.boc_dev.graphics_library.objects.materials.LoadedTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Font {

	private final String fontName;
	private final LoadedTexture fontTextureAtlas;
	private final CharacterData characterData;

	public Font(String fontName) {
		this.fontName = fontName;
		fontTextureAtlas = new LoadedTexture("/fonts/" + fontName + ".png", GL11.GL_LINEAR);

		URL url = getClass().getResource("/fonts/" + fontName + ".fnt");

		File file = null;

		if (url == null) {
			// try to find it in the user input folder via environment variable
			file = new File(System.getenv("GRAPHICS_LIB_DATA") + "/fonts/" + fontName + ".fnt");
		} else {
			file = new File(url.getFile());
		}

		this.characterData = new CharacterData(file);
	}

	public void create() throws IOException {
		fontTextureAtlas.create();
	}

	public void destroy() {
		fontTextureAtlas.destroy();
	}

	public String getFontName() {
		return fontName;
	}

	public LoadedTexture getFontTextureAtlas() {
		return fontTextureAtlas;
	}

	public CharacterData getCharacterData() {
		return characterData;
	}

	public void initRender(Shader shader) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureAtlas.getId());

		shader.setUniform("material.hasNormalMap", 0);

	}

	public void endRender() {
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}
