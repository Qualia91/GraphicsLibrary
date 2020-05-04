package com.nick.wood.graphics_library_3d.objects.mesh_objects;

import com.nick.wood.maths.objects.matrix.Matrix4f;

public interface MeshObject {
	Mesh getMesh();
	Matrix4f getMeshTransformation();
	String getStringToCompare();
}
