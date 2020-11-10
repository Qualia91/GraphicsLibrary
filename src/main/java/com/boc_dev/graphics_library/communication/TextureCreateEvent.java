package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;

import java.io.IOException;

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
