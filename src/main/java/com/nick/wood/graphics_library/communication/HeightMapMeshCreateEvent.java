package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.mesh_objects.Model;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

import java.io.IOException;
import java.util.ArrayList;

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
