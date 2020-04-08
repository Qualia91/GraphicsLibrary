package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Mesh;
import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.maths.objects.Matrix4d;

public interface MeshObject {
	Mesh getMesh();

	Matrix4d getRotationOfModel();
}
