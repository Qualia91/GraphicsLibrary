package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Mesh;
import com.nick.wood.maths.objects.Matrix4d;

import java.io.IOException;
import java.util.Objects;

public class ModelMesh implements MeshObject {

	private final String filePath;
	private final String texturePath;
	private final Matrix4d rotationOfModel;
	private Mesh mesh;

	public ModelMesh(String filePath, String texturePath, Matrix4d rotationOfModel) {

		this.rotationOfModel = rotationOfModel;
		this.filePath = filePath;
		this.texturePath = texturePath;
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
	public Matrix4d getRotationOfModel() {
		return rotationOfModel;
	}
}
