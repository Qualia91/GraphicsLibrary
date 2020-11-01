package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.DrawVisitor;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public interface Mesh {

	void destroy();

	void initRender();

	int getIbo();

	int[] getIndices();

	void endRender();

	FloatBuffer getModelViewBuffer();

	void draw(DrawVisitor drawVisitor, ArrayList<InstanceObject> value);
}
