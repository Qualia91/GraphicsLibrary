package com.nick.wood.graphics_library.mesh_objects;

import com.nick.wood.graphics_library.Mesh;

import java.io.IOException;

public class ModelMesh implements MeshObject {

	private final String filePath;
	private final String texturePath;
	private final MeshTransform meshTransform;
	private Mesh mesh;

	public ModelMesh(MeshTransform meshTransform, String filePath, String texturePath) {

		this.filePath = filePath;
		this.texturePath = texturePath;
		this.meshTransform = meshTransform;
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
	public MeshTransform getModelTransform() {
		return meshTransform;
	}

}
