package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Mesh;
import com.nick.wood.graphics_library.objects.Transform;

import java.io.IOException;

public class ModelMesh implements MeshObject {

	private final String filePath;
	private final String texturePath;
	private final Transform transform;
	private Mesh mesh;

	public ModelMesh(Transform transform, String filePath, String texturePath) {

		this.filePath = filePath;
		this.texturePath = texturePath;
		this.transform = transform;
		try {
			mesh = ModelLoader.loadModel(filePath, texturePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Transform getModelTransform() {
		return transform;
	}

}
