package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.maths.objects.srt.Transform;

public interface MeshObject {
	Mesh getMesh();
	Transform getMeshTransformation();
	String getStringToCompare();

	// temp
	default void setTextureViaFBO(boolean flag) {

	}

	default boolean isTextureViaFBOFlag() {
		return false;
	}
}
