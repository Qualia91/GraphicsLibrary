package com.boc_dev.graphics_library.objects.managers;

import com.boc_dev.graphics_library.objects.materials.Texture;
import com.boc_dev.graphics_library.objects.materials.FboTexture;
import com.boc_dev.graphics_library.objects.materials.LoadedTexture;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;

public class TextureManager {

	private final HashMap<String, Texture> texturePathLoadedTextureHashMap;

	public TextureManager() {
		this.texturePathLoadedTextureHashMap = new HashMap<>();
	}

	public int getTextureId(String texturePath) {
//		System.out.println("Textures: " + texturePathLoadedTextureHashMap.size());
//		texturePathLoadedTextureHashMap.forEach((s, texture) -> System.out.println(s + " " + texture.getId()));
		return texturePathLoadedTextureHashMap.getOrDefault(texturePath, texturePathLoadedTextureHashMap.get("DEFAULT")).getId();

	}

	public void create(String defaultTexturePath) throws IOException {

		// set up a default texture for when they fail
		Texture texture = new LoadedTexture(defaultTexturePath, GL11.GL_LINEAR);
		texture.create();

		Texture dudv = new LoadedTexture("/textures/waterDuDvMap.jpg", GL11.GL_LINEAR);
		dudv.create();
		Texture waterNormal = new LoadedTexture("/normalMaps/waterNormalMap.jpg", GL11.GL_LINEAR);
		dudv.create();

		this.texturePathLoadedTextureHashMap.put("DEFAULT", texture);
		this.texturePathLoadedTextureHashMap.put("/textures/waterDuDvMap.jpg", dudv);
		this.texturePathLoadedTextureHashMap.put("/normalMaps/waterNormalMap.jpg", waterNormal);
	}

	public void destroy() {
		for (Texture texture : this.texturePathLoadedTextureHashMap.values()) {
			texture.destroy();
		}
	}

	public void addTexture(String name, int textureId) {
		// if texture is already in there, change the texture id in the texture. If not, create a new fboTexture
		if (texturePathLoadedTextureHashMap.containsKey(name)) {
			// this is for overwriting textures with new id's (think fbos and shit)
			texturePathLoadedTextureHashMap.get(name).setId(textureId);
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
