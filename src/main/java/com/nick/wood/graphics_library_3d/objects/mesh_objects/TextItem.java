package com.nick.wood.graphics_library_3d.objects.mesh_objects;

import com.nick.wood.graphics_library_3d.Material;
import com.nick.wood.graphics_library_3d.Vertex;
import com.nick.wood.graphics_library_3d.objects.mesh_objects.Mesh;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextItem implements MeshObject {

	private static final float ZPOS = 0.0f;
	private static final int VERTICES_PER_QUAD = 4;
	private Mesh mesh;
	private String text;
	private final int numCols;
	private final int numRows;
	private final Material material;

	// package private so you have to use builder so builder can build mesh's when open gl is initialised
	TextItem(String text, String fontFileName, int numCols, int numRows) {
		super();
		this.text = text;
		this.numCols = numCols;
		this.numRows = numRows;
		material = new Material(fontFileName);
		mesh = buildMesh(numCols, numRows);
	}

	public void changeText(String text) {
		mesh.destroyWithoutDestroyingMaterial();
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

		for(int i=0; i<numChars; i++) {
			byte currChar = chars[i];
			int col = currChar % numCols;
			int row = currChar / numCols;

			// Build a character tile composed by two triangles

			// Left Top vertex
			vertex[i * 4] =
					new Vertex(
							new Vec3f(ZPOS, tileHeight, (float)i*tileWidth),
							new Vec2f((float)col / (float)numCols, (float)row / (float)numRows),
							//new Vec2f(0, 0),
							Vec3f.X.neg());
			indices[i*6] = i*VERTICES_PER_QUAD;

			// Left Bottom vertex
			vertex[i * 4 + 1] =
					new Vertex(
							new Vec3f(ZPOS,  0.0f,(float)i*tileWidth),
							new Vec2f((float)col / (float)numCols, (float)(row + 1) / (float)numRows),
							//new Vec2f(0, 1),
							Vec3f.X.neg());
			indices[i*6 + 1] = i*VERTICES_PER_QUAD + 1;

			// Right Bottom vertex
			vertex[i * 4 + 2] =
					new Vertex(
							new Vec3f(ZPOS, 0.0f, (float)i*tileWidth + tileWidth),
							new Vec2f((float)(col + 1) / (float)numCols, (float)(row + 1) / (float)numRows),
							//new Vec2f(1, 1),
							Vec3f.X.neg());
			indices[i*6 + 2] = i*VERTICES_PER_QUAD + 2;

			// Right Top vertex
			vertex[i * 4 + 3] =
					new Vertex(
							new Vec3f(ZPOS, tileHeight, (float)i*tileWidth + tileWidth),
							new Vec2f((float)(col + 1) / (float)numCols, (float)row / (float)numRows),
							//new Vec2f(1, 0),
							Vec3f.X.neg());
			indices[i*6 + 3] = i*VERTICES_PER_QUAD + 3;

			// Add indices por left top and bottom right vertices
			indices[i*6 + 4] = i*VERTICES_PER_QUAD;
			indices[i*6 + 5] = i*VERTICES_PER_QUAD + 2;
		}

		return new Mesh(vertex, indices, material, false);

	}

	public String getText() {
		return text;
	}

	@Override
	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Matrix4f getMeshTransformation() {
		return Matrix4f.Identity;
	}

	@Override
	public String getStringToCompare() {
		return text;
	}
}
