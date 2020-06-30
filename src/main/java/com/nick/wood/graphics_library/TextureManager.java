package com.nick.wood.graphics_library;

import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;

public class TextureManager {

	private final HashMap<String, Texture> stringTextureHashMap;

	public TextureManager() {
		this.stringTextureHashMap = new HashMap<>();
	}

	public int getTextureId(String texturePath) {

		if (stringTextureHashMap.containsKey(texturePath)) {
			return stringTextureHashMap.get(texturePath).getId();
		}

		Texture texture = new Texture(texturePath, GL11.GL_LINEAR);
		try {
			texture.create();
			stringTextureHashMap.put(texturePath, texture);
		} catch (IOException e) {
			System.out.println("Texture with path " + texturePath + " was unable to load. Using default texture.");
			texture = stringTextureHashMap.get("DEFAULT");
		}

		return texture.getId();
	}

	public void create() throws IOException {

		// set up a default texture for when they fail
		Texture texture = new Texture("/textures/red.png", GL11.GL_LINEAR);
		texture.create();

		this.stringTextureHashMap.put("DEFAULT", texture);
	}

	public void destroy() {
		for (Texture texture : this.stringTextureHashMap.values()) {
			texture.destroy();
		}
	}
}
