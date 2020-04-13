package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import org.lwjgl.opengl.GL30;

public interface Mesh {

	void create();
	void destroy();
	Vertex[] getVertices();
	int[] getIndices();
	int getVao();
	int getPbo();
	int getIbo();
	int getTbo();
	Material getMaterial();
	int getNbo();

	void initRender();
	void endRender();

	int getVertexCount();
}
