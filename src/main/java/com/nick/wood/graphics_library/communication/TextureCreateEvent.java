package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.materials.Material;

import java.io.IOException;
import java.util.UUID;

public class TextureCreateEvent implements RenderUpdateEvent<String> {

	private final String path;

	public TextureCreateEvent(String path) {
		this.path = path;
	}

	@Override
	public String getData() {
		return path;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		// add material to material manager
		try {
			window.getTextureManager().addTexture(path);
		} catch (IOException e) {
			System.out.println("Texture " + path + " could not be loaded, default will be used");
		}
	}
}
