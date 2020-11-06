package com.nick.wood.graphics_library.objects.mesh_objects.renderer_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.Vertex;

public interface RendererObject {
	void create(Vertex[] vertices, int[] indices);

	void initRender();

	void endRender();

	void destroy();
}
