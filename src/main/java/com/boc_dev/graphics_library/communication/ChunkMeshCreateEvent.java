package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;
import com.boc_dev.maths.objects.vector.Vec3f;

public class ChunkMeshCreateEvent implements RenderUpdateEvent<String> {

	private final String name;
	private final Vec3f[] vertex;

	public ChunkMeshCreateEvent(String name, Vec3f[] vertex) {
		this.name = name;
		this.vertex = vertex;
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
		window.getMeshManager().createMesh(vertex, name);
	}
}
