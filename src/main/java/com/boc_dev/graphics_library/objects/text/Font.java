package com.boc_dev.graphics_library.objects.text;

import com.boc_dev.graphics_library.Shader;
import com.boc_dev.graphics_library.objects.managers.TextureManager;
import com.boc_dev.graphics_library.objects.materials.LoadedTexture;
import com.boc_dev.graphics_library.utils.FileUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Font {

	private final String fontName;
	private final LoadedTexture fontTextureAtlas;
	private final CharacterData characterData;

	public Font(String fontName) {
		this.fontName = fontName;

		fontTextureAtlas = new LoadedTexture("/fonts/" + fontName + ".png", GL11.GL_LINEAR);

		String descriptor = "/fonts/" + fontName + ".png";

		InputStream inputStream = new FileUtils().loadFile("/fonts/" + fontName + ".fnt");

		this.characterData = new CharacterData(descriptor, inputStream);
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
