package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.maths.objects.matrix.Matrix4f;

import java.io.IOException;
import java.util.Objects;

public class ModelMesh implements MeshObject {

	private final ModelLoader modelLoader = new ModelLoader();
	private final String filePath;
	private final String texturePath;
	private final Matrix4f transform;
	private Mesh mesh;

	ModelMesh(String filePath, String texturePath, boolean invertedNormals, Matrix4f transform) {

		this.transform = transform;
		this.filePath = filePath;
		this.texturePath = texturePath;
		try {
			mesh = modelLoader.loadModel(filePath, texturePath, invertedNormals);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStringToCompare() {
		return filePath + texturePath;
	}

	@Override
	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ModelMesh modelMesh = (ModelMesh) o;
		return Objects.equals(filePath, modelMesh.filePath) &&
				Objects.equals(texturePath, modelMesh.texturePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filePath, texturePath);
	}

	@Override
	public Matrix4f getMeshTransformation() {
		return transform;
	}
}
