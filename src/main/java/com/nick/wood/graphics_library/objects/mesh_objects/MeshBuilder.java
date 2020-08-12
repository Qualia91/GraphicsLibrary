package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.materials.Material;
import com.nick.wood.graphics_library.objects.TerrainTextureObject;
import com.nick.wood.maths.objects.srt.Transform;

import java.io.File;
import java.util.ArrayList;

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
	private final ArrayList<TerrainTextureObject> terrainTextureObjects = new ArrayList<>();
	// this is used as the id for meshes that want to be textured via fbos. the fbos are created using fbo cameras so
	// the id is the name of the camera that creates it.
	private String fboCameraName = "";

	public MeshBuilder() {
		modelFile = new File(MeshBuilder.class.getResource("/models/sphere.obj").getFile()).getAbsolutePath();
	}

	public MeshObject build() {

		Material material = new Material(texture);

		if (normalTexture != null) {
			material.setNormalMap(normalTexture);
		}

		Material fontMaterial = new Material(fontFile);

		createMaterialId(material);
		createMaterialId(material);

		MeshObject meshObject = null;
		switch (meshType) {
			case SPHERE:
				meshObject = new SphereMesh(triangleNumber, material, invertedNormals, transformation, fboCameraName);
				break;
			case CUBOID:
				meshObject = new CubeMesh(material, invertedNormals, transformation, fboCameraName);
				break;
			case MODEL:
				meshObject = new ModelMesh(modelFile, material, invertedNormals, transformation, fboCameraName);
				break;
			case SQUARE:
				meshObject = new Square(material, transformation, fboCameraName);
				break;
			case TEXT:
				meshObject = new TextItem(text, fontMaterial, rowNum, colNum, transformation, fboCameraName);
				break;
			case TERRAIN:
				meshObject = new Terrain(terrainHeightMap, material, terrainTextureObjects, cellSpace, meshType, fboCameraName);
				break;
			case WATER:
				meshObject = new Water(waterSquareWidth, waterHeight, material, cellSpace, meshType, fboCameraName);
				break;
			case POINT:
				meshObject = new Point(transformation, material, fboCameraName);
				break;
			case TRIANGLE:
				meshObject = new Triangle(transformation, material, triangleNumber, invertedNormals, fboCameraName);
				break;
			case CIRCLE:
				meshObject = new CircleMesh(transformation, material, triangleNumber, fboCameraName);
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

	private void createMaterialId(Material material) {
		material.setId(material.getTexturePath() + material.getNormalMapPath());
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

	public MeshBuilder addTerrainTextureObject(TerrainTextureObject terrainTextureObject) {
		this.terrainTextureObjects.add(terrainTextureObject);
		return this;
	}

	public MeshBuilder setTextureFboCameraName(String fboCameraName) {
		this.fboCameraName = fboCameraName;
		return this;
	}
}
