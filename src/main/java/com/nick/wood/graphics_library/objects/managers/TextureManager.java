package com.nick.wood.graphics_library.objects.managers;

import com.nick.wood.graphics_library.objects.materials.FboTexture;
import com.nick.wood.graphics_library.objects.materials.LoadedTexture;
import com.nick.wood.graphics_library.objects.materials.Texture;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;

public class TextureManager {

	private final HashMap<String, Texture> texturePathLoadedTextureHashMap;

	public TextureManager() {
		this.texturePathLoadedTextureHashMap = new HashMap<>();
	}

	public int getTextureId(String texturePath) {

		return texturePathLoadedTextureHashMap.getOrDefault(texturePath, texturePathLoadedTextureHashMap.get("DEFAULT")).getId();

	}

	public void create(String defaultTexturePath) throws IOException {

		// set up a default texture for when they fail
		Texture texture = new LoadedTexture(defaultTexturePath, GL11.GL_LINEAR);
		texture.create();

		this.texturePathLoadedTextureHashMap.put("DEFAULT", texture);
	}

	public void destroy() {
		for (Texture texture : this.texturePathLoadedTextureHashMap.values()) {
			texture.destroy();
		}
	}

	public void addTexture(String name, int textureId) {
		// if texture is already in there, change the texture id in the texture. If not, create a new fboTexture
		if (texturePathLoadedTextureHashMap.containsKey(name)) {
			System.out.println("What is happening here? A texture is already there");
//			texturePathLoadedTextureHashMap.get(name).setId(textureId);
		} else {
			texturePathLoadedTextureHashMap.put(name, new FboTexture(textureId));
		}
	}

	public void addTexture(String path) throws IOException {
		LoadedTexture loadedTexture = new LoadedTexture(path, GL11.GL_LINEAR);
		loadedTexture.create();
		texturePathLoadedTextureHashMap.put(path, loadedTexture);
	}
}
