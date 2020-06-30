package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.maths.objects.srt.Transform;

import java.io.File;

public class MeshBuilder {

	// todo add model map here so i dont remake models that are already made

	private MeshType meshType = MeshType.SPHERE;
	private boolean invertedNormals = false;
	private String texture = "/textures/white.png";
	private String normalTexture = null;
	private Transform transformation = Transform.Identity;
	private int triangleNumber = 5;
	private String text = "0";
	private String fontFile = "/fonts/verandaGreenBold.png";
	private int rowNum = 16;
	private int colNum = 16;
	private int waterSquareWidth = 100;
	private int waterHeight = 0;
	private String modelFile;
	private float[][] terrainHeightMap = new float[][]{
			{0, 0},
			{0, 0},
	};
	private double cellSpace = 1;
	private int fboTextureIndex = -1;

	public MeshBuilder() {
		modelFile = new File(MeshBuilder.class.getResource("/models/sphere.obj").getFile()).getAbsolutePath();
	}

	public MeshObject build() {

		Material material = new Material(texture);

		if (normalTexture != null) {
			material.setNormalMap(normalTexture);
		}

		Material fontMaterial = new Material(fontFile);

		MeshObject meshObject = null;
		switch (meshType) {
			case SPHERE:
				meshObject = new SphereMesh(triangleNumber, material, invertedNormals, transformation, fboTextureIndex);
				break;
			case CUBOID:
				meshObject = new CubeMesh(material, invertedNormals, transformation, fboTextureIndex);
				break;
			case MODEL:
				meshObject = new ModelMesh(modelFile, material, invertedNormals, transformation, fboTextureIndex);
				break;
			case SQUARE:
				meshObject = new Square(material, transformation, fboTextureIndex);
				break;
			case TEXT:
				meshObject = new TextItem(text, fontMaterial, rowNum, colNum, transformation, fboTextureIndex);
				break;
			case TERRAIN:
				meshObject = new Terrain(terrainHeightMap, material, cellSpace, meshType, fboTextureIndex);
				break;
			case WATER:
				meshObject = new Water(waterSquareWidth, waterHeight, material, cellSpace, meshType, fboTextureIndex);
				break;
			case POINT:
				meshObject = new Point(transformation, material, fboTextureIndex);
				break;
			case TRIANGLE:
				meshObject = new Triangle(transformation, material, triangleNumber, invertedNormals, fboTextureIndex);
				break;
			case CIRCLE:
				meshObject = new CircleMesh(transformation, material, triangleNumber, fboTextureIndex);
				break;
		};

		/** for java 14
		 * MeshObject meshObject = switch (meshType) {
		 * 			case SPHERE -> new SphereMesh(triangleNumber, material, invertedNormals, transformation);
		 * 			case CUBOID -> new CubeMesh(material, invertedNormals, transformation);
		 * 			case MODEL -> new ModelMesh(modelFile, material, invertedNormals, transformation);
		 * 			case SQUARE -> new Square(material, transformation);
		 * 			case TEXT -> new TextItem(text, fontFile, rowNum, colNum, transformation);
		 * 			case TERRAIN -> new Terrain(terrainHeightMap, material, cellSpace, meshType);
		 * 			case WATER -> new Terrain(waterSquareWidth, waterHeight, material, cellSpace, meshType);
		 * 			case POINT -> new Point(transformation, material);
		 * 			case TRIANGLE -> new Triangle(transformation, triangleNumber, invertedNormals);
		 *                };
		 */

		return meshObject;
	}

	public MeshBuilder setWaterSquareWidth(int waterSquareWidth) {
		this.waterSquareWidth = waterSquareWidth;
		return this;
	}

	public MeshBuilder setWaterHeight(int waterHeight) {
		this.waterHeight = waterHeight;
		return this;
	}

	public MeshBuilder setModelFile(String modelFile) {
		this.modelFile = modelFile;
		return this;
	}

	public MeshBuilder setText(String text) {
		this.text = text;
		return this;
	}

	;

	public MeshBuilder setFontFile(String fontFile) {
		this.fontFile = fontFile;
		return this;
	}

	;

	public MeshBuilder setRowNumber(int rowNum) {
		this.rowNum = rowNum;
		return this;
	}

	;

	public MeshBuilder setColNumber(int colNum) {
		this.colNum = colNum;
		return this;
	}

	;

	public MeshBuilder setMeshType(MeshType meshType) {
		this.meshType = meshType;
		return this;
	}

	;

	public MeshBuilder setInvertedNormals(boolean invertedNormals) {
		this.invertedNormals = invertedNormals;
		return this;
	}

	;

	public MeshBuilder setTexture(String texture) {
		this.texture = texture;
		return this;
	}

	;

	public MeshBuilder setNormalTexture(String normalTexture) {
		this.normalTexture = normalTexture;
		return this;
	}

	;

	public MeshBuilder setTransform(Transform transformation) {
		this.transformation = transformation;
		return this;
	}

	;

	public MeshBuilder setTriangleNumber(int triangleNumber) {
		this.triangleNumber = triangleNumber;
		return this;
	}

	;

	public MeshBuilder setTerrainHeightMap(float[][] terrainHeightMap) {
		this.terrainHeightMap = terrainHeightMap;
		return this;
	}

	public MeshBuilder setCellSpace(double cellSpace) {
		this.cellSpace = cellSpace;
		return this;
	}

	public MeshBuilder setTextureFboIndex(int fboTextureIndex) {
		this.fboTextureIndex = fboTextureIndex;
		return this;
	}
}
