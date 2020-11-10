package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;

public class HeightMapMeshCreateEvent implements RenderUpdateEvent<String> {

	private final String name;
	private final float[][] grid;
	private final double cellSpace;


	public HeightMapMeshCreateEvent(String name, float[][] grid, double cellSpace) {
		this.name = name;
		this.grid = grid;
		this.cellSpace = cellSpace;
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
		window.getMeshManager().createMesh(grid, cellSpace, name);
	}
}
