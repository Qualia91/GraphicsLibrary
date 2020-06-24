package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.maths.objects.srt.Transform;

public interface MeshObject {
	MeshType getMeshType();
	Mesh getMesh();
	Transform getMeshTransformation();
	String getStringToCompare();
	int getFboTextureIndex();
}
