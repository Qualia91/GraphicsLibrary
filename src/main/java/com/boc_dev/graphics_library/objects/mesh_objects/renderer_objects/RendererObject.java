package com.boc_dev.graphics_library.objects.mesh_objects.renderer_objects;

import com.boc_dev.graphics_library.objects.mesh_objects.Vertex;

public interface RendererObject {
	void create(Vertex[] vertices, int[] indices);

	void initRender();

	void endRender();

	void destroy();
}
