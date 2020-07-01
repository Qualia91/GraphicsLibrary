package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.materials.Material;
import com.nick.wood.maths.objects.srt.Transform;

import java.io.IOException;
import java.util.Objects;

public class ModelMesh implements MeshObject {

	private final ModelLoader modelLoader = new ModelLoader();
	private final String filePath;
	private final Material material;
	private final Transform transform;
	private Mesh mesh;
	private String fboCameraName;

	ModelMesh(String filePath, Material material, boolean invertedNormals, Transform transform, String fboCameraName) {

		this.fboCameraName = fboCameraName;
		this.transform = transform;
		this.filePath = filePath;
		this.material = material;
		try {
			mesh = modelLoader.loadModel(filePath, material, invertedNormals);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Material getMaterial() {
		return material;
	}

	public String getStringToCompare() {
		return filePath + material.getTexturePath() + fboCameraName;
	}

	@Override
	public String getFboTextureCameraName() {
		return fboCameraName;
	}

	public String getFilePath() {
		return filePath;
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
				Objects.equals(material.getTexturePath(), modelMesh.getMaterial().getTexturePath());
	}

	@Override
	public int hashCode() {
		return Objects.hash(filePath, getMaterial().getTexturePath());
	}

	@Override
	public Transform getMeshTransformation() {
		return transform;
	}

	@Override
	public MeshType getMeshType() {
		return MeshType.MODEL;
	}
}
