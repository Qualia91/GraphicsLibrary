package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.materials.Material;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.nio.charset.StandardCharsets;

public class TextItem implements MeshObject {

	private static final float ZPOS = 0.0f;
	private static final int VERTICES_PER_QUAD = 4;
	private final int fboTextureIndex;
	private Mesh mesh;
	private String text;
	private final int numCols;
	private final int numRows;
	private final Material material;
	private float width = 0f;
	private float height = 0f;
	private Transform transformation;

	// package private so you have to use builder so builder can build mesh's when open gl is initialised
	TextItem(String text, Material material, int numRows, int numCols, Transform transformation, int fboTextureIndex) {
		this.fboTextureIndex = fboTextureIndex;
		this.text = text;
		this.numCols = numCols;
		this.numRows = numRows;
		this.material = material;
		mesh = buildMesh(numCols, numRows);
		this.transformation = transformation;
	}

	public void changeText(String text) {
		this.text = text;
		mesh = buildMesh(numCols, numRows);
	}

	private Mesh buildMesh(int numCols, int numRows) {
		// read text you want to put into texture
		byte[] chars = text.getBytes(StandardCharsets.ISO_8859_1);
		int numChars = chars.length;

		// set up data for mesh
		Vertex[] vertex = new Vertex[numChars * 4];
		int[] indices   = new int[numChars * 6];

		// set up tile info in font image
		float tileWidth = 1f / (float)numCols;
		float tileHeight = 1f / (float)numRows;

		width = numChars * tileWidth;
		height = tileHeight;

		for(int i=0; i<numChars; i++) {
			byte currChar = chars[i];
			int textCoordCol = currChar % numCols;
			int row = currChar / numCols;

			// Build a character tile composed by two triangles

			// Right Top vertex
			vertex[i * 4] =
					new Vertex(
							new Vec3f(ZPOS, (float)(numChars - i)*tileWidth, tileHeight),
							new Vec2f((float)(textCoordCol + 1) / (float)numCols, (float)row / (float)numRows),
							Vec3f.X.neg());
			indices[i*6] = i*VERTICES_PER_QUAD;

			// left Top vertex
			vertex[i * 4 + 1] =
					new Vertex(
							new Vec3f(ZPOS, (float)(numChars - i)*tileWidth + tileWidth, tileHeight),
							new Vec2f((float) textCoordCol / (float)numCols, (float)row / (float)numRows),
							Vec3f.X.neg());
			indices[i*6 + 1] = i*VERTICES_PER_QUAD + 1;

			// right Bottom vertex
			vertex[i * 4 + 2] =
					new Vertex(
							new Vec3f(ZPOS,  (float)(numChars - i)*tileWidth, 0.0f),
							new Vec2f((float) (textCoordCol + 1) / (float)numCols, (float)(row + 1) / (float)numRows),
							Vec3f.X.neg());
			indices[i*6 + 2] = i*VERTICES_PER_QUAD + 2;

			// right Bottom vertex
			indices[i*6 + 3] = i*VERTICES_PER_QUAD + 2;

			// left top vertex
			indices[i*6 + 4] = i*VERTICES_PER_QUAD + 1;

			// left Bottom vertex
			vertex[i * 4 + 3] =
					new Vertex(
							new Vec3f(ZPOS, (float)(numChars - i)*tileWidth + tileWidth, 0.0f),
							new Vec2f((float) textCoordCol / (float)numCols, (float)(row + 1) / (float)numRows),
							Vec3f.X.neg());
			indices[i*6 + 5] = i*VERTICES_PER_QUAD + 3;

		}

		if (mesh != null) {
			mesh.updateMesh(vertex, indices);
			return mesh;
		} else {
			return new Mesh(vertex, indices, material, false, false);
		}

	}

	public int getNumCols() {
		return numCols;
	}

	public int getNumRows() {
		return numRows;
	}

	public String getText() {
		return text;
	}

	@Override
	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Transform getMeshTransformation() {
		return transformation;
	}

	@Override
	public String getStringToCompare() {
		return text;
	}

	@Override
	public int getFboTextureIndex() {
		return fboTextureIndex;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	@Override
	public MeshType getMeshType() {
		return MeshType.TEXT;
	}
}
