package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;

public class SkyboxRemoveEvent implements RenderUpdateEvent<String> {

	private final String layerName;

	public SkyboxRemoveEvent(String layerName) {
		this.layerName = layerName;
	}

	@Override
	public String getData() {
		return layerName;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.DESTROY;
	}

	public void applyToGraphicsEngine(Window window) {
		// if the layer does not exist, just ignore this as its clearly not being rendered (or something has gone wrong)
		if (window.getRenderGraphs().get(layerName) == null) {
			System.err.println("Skybox cannot ben removed as layer " + layerName + " does not exist");
			return;
		}

		window.getRenderGraphs().get(layerName).setSkybox(null);

	}
}
