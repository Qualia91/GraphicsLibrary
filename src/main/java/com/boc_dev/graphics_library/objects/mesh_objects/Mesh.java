package com.boc_dev.graphics_library.objects.mesh_objects;

import com.boc_dev.graphics_library.objects.DrawVisitor;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;

import java.util.ArrayList;

public interface Mesh {

	void create();

	void destroy();

	void initRender();

	void draw(DrawVisitor drawVisitor, ArrayList<InstanceObject> value);

	void endRender();

	int size();

	MeshType getType();
}
