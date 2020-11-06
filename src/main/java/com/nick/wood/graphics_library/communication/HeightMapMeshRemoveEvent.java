package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;

public class HeightMapMeshRemoveEvent implements RenderUpdateEvent<String> {

	private final String name;

	public HeightMapMeshRemoveEvent(String name) {
		this.name = name;
	}

	@Override
	public String getData() {
		return name;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		window.getMeshManager().destroyMesh(name);
	}
}
