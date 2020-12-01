package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;

public class ChunkMeshRemoveEvent implements RenderUpdateEvent<String> {

	private final String name;

	public ChunkMeshRemoveEvent(String name) {
		this.name = name;
	}

	@Override
	public String getData() {
		return name;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.DESTROY;
	}

	public void applyToGraphicsEngine(Window window) {
		window.getMeshManager().destroyMesh(name);
	}
}
