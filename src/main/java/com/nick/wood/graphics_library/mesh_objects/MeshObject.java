package com.nick.wood.graphics_library.mesh_objects;

import com.nick.wood.graphics_library.Mesh;

public interface MeshObject {
	Mesh getMesh();
	MeshTransform getModelTransform();
}
