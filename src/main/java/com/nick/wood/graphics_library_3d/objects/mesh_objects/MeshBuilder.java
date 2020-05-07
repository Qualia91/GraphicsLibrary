package com.nick.wood.graphics_library_3d.objects.mesh_objects;

import com.nick.wood.graphics_library_3d.Material;
import com.nick.wood.maths.objects.matrix.Matrix4f;

public class MeshBuilder {

	private MeshType meshType = MeshType.SPHERE;
	private boolean invertedNormals = false;
	private String texture = "/textures/white.png";
	private Matrix4f transformation = Matrix4f.Identity;
	private int triangleNumber = 5;
	private Material material;
	private String text = "DEFAULT_STRING";
	private String fontFile = "/font/gothic.png";
	private int rowNum = 16;
	private int colNum = 16;
	private String modelFile = "D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\sphere.obj";

	public MeshObject build() {

		if (material == null) {
			material = new Material(texture);
		}

		return switch (meshType) {
			case SPHERE -> new SphereMesh(triangleNumber, material, invertedNormals, transformation);
			case CUBOID -> new CubeMesh(material, invertedNormals, transformation);
			case MODEL -> new ModelMesh(modelFile, texture, invertedNormals, transformation);
			case SQUARE -> new Square(material, transformation);
			case TEXT -> new TextItem(text, fontFile, rowNum, colNum);
			default -> new SphereMesh(1, new Material("/textures/white.png"), true, Matrix4f.Identity);
		};
	}

	public MeshBuilder setModelFile(String modelFile) {
		this.modelFile = modelFile;
		return this;
	}
	public MeshBuilder setText(String text) {
		this.text = text;
		return this;
	};
	public MeshBuilder setFontFile(String fontFile) {
		this.fontFile = fontFile;
		return this;
	};
	public MeshBuilder setRowNumber(int rowNum) {
		this.rowNum = rowNum;
		return this;
	};
	public MeshBuilder setColNumber(int colNum) {
		this.colNum = colNum;
		return this;
	};
	public MeshBuilder setMeshType(MeshType meshType) {
		this.meshType = meshType;
		return this;
	};
	public MeshBuilder setInvertedNormals(boolean invertedNormals) {
		this.invertedNormals = invertedNormals;
		return this;
	};
	public MeshBuilder setTexture(String texture) {
		this.texture = texture;
		return this;
	};
	public MeshBuilder setTransform(Matrix4f transformation) {
		this.transformation = transformation;
		return this;
	};
	public MeshBuilder setTriangleNumber(int triangleNumber) {
		this.triangleNumber = triangleNumber;
		return this;
	};
	public MeshBuilder setMaterial(Material material) {
		this.material = material;
		return this;
	};

}