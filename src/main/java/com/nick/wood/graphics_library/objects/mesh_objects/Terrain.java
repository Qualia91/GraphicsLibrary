package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.Arrays;

public class Terrain implements MeshObject {

	private final Material material;
	private final float[][] terrainHeightMap;
	private final Mesh mesh;
	private final double cellSpace;
	private final int width;

	// package private so you have to use builder so builder can build mesh's when open gl is initialised
	Terrain(float[][] terrainHeightMap, Material material, double cellSpace) {
		super();
		this.terrainHeightMap = terrainHeightMap;
		this.cellSpace = cellSpace;
		this.material = material;
		this.width = terrainHeightMap.length;
		this.mesh = buildMesh(terrainHeightMap, cellSpace);
	}

	// package private so you have to use builder so builder can build mesh's when open gl is initialised
	Terrain(int size, int height, Material material, double cellSpace) {
		super();
		this.terrainHeightMap = new float[size][size];
		for (float[] floats : this.terrainHeightMap) {
			Arrays.fill(floats, height);
		}
		this.cellSpace = cellSpace;
		this.material = material;
		this.width = size;
		this.mesh = buildMesh(terrainHeightMap, cellSpace);
	}

	private Mesh buildMesh(float[][] terrainHeightMap, double cellSpace) {

		// set up data for mesh
		Vertex[] vertex = new Vertex[terrainHeightMap.length * terrainHeightMap[0].length];
		int[] indices = new int[
				(terrainHeightMap.length - 1) *
						(terrainHeightMap[0].length - 1) *
						6
				];

		// make all vertex's
		for (int x = 0; x < terrainHeightMap.length; x++) {
			for (int y = 0; y < terrainHeightMap[x].length; y++) {

				vertex[y * width + x] =
						new Vertex(
								new Vec3f((float) cellSpace * x, (float) cellSpace * y, terrainHeightMap[x][y]),
								new Vec2f(1 - ((float)y/terrainHeightMap[y].length), 1 - ((float)x/terrainHeightMap.length)),
								Vec3f.Z,
								Vec3f.Z,
								Vec3f.Z);
			}
		}

		// now go through and fix all normals.
		// normals should be the normal of the plane made by the 4 vertex around the point that make a square
		// start one in and end one before the ends
		// make the edges have the same as the next lines in
		for (int y = 1; y < terrainHeightMap.length - 1; y++) {
			for (int x = 1; x < terrainHeightMap[y].length - 1; x++) {

				// up
				Vec3f up = vertex[(y - 1) * width + x].getPos();

				// down
				Vec3f down = vertex[(y + 1) * width + x].getPos();

				// left
				Vec3f left = vertex[y * width + x - 1].getPos();

				// right
				Vec3f right = vertex[y * width + x + 1].getPos();

				Vec3f normalOne = normalOfTriangle(up, down, left);
				Vec3f normalTwo = normalOfTriangle(down, up, right);

				Vec3f norm = normalOne.add(normalTwo).scale(0.5f).normalise();

				vertex[y * width + x].setNormal(norm);
				vertex[y * width + x].setTangent(up);
				vertex[y * width + x].setBitangent(left);

				if (y == 1) {
					vertex[x].setNormal(norm);
					vertex[x].setTangent(up);
					vertex[x].setBitangent(left);
				}

				if (x == 1) {
					vertex[y * width].setNormal(norm);
					vertex[y * width].setTangent(up);
					vertex[y * width].setBitangent(left);
				}

				if (y == terrainHeightMap.length - 2) {
					vertex[(y + 1) * width + x].setNormal(norm);
					vertex[(y + 1) * width + x].setTangent(up);
					vertex[(y + 1) * width + x].setBitangent(left);
				}

				if (x == terrainHeightMap[y].length - 2) {
					vertex[y * width + x + 1].setNormal(norm);
					vertex[y * width + x + 1].setTangent(up);
					vertex[y * width + x + 1].setBitangent(left);
				}
			}
		}

		// now do indices
		int indexCount = 0;
		for (int y = 0; y < terrainHeightMap.length - 1; y++) {
			for (int x = 0; x < terrainHeightMap[y].length - 1; x++) {

				// by each square
				// top left vertex
				indices[indexCount++] = (y*width) + x;

				// top right
				indices[indexCount++] = (y*width) + x + 1;

				// bottom left
				indices[indexCount++] = ((y+1)*width) + x;

				// top right
				indices[indexCount++] = (y*width) + x + 1;

				// bottom right
				indices[indexCount++] = ((y+1)*width) + x + 1;

				// bottom left
				indices[indexCount++] = ((y+1)*width) + x;

			}
		}

		return new Mesh(vertex, indices, material, false, true);

	}

	private Vec3f normalOfTriangle(Vec3f a, Vec3f b, Vec3f c) {
		// normal of triangle one (up. down, left)
		// line from down to up
		Vec3f ab = b.subtract(a);
		// line fro down to left
		Vec3f ac = c.subtract(a);
		// normal is cross prod
		return ab.cross(ac);
	}

	@Override
	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Transform getMeshTransformation() {
		return Transform.Identity;
	}

	@Override
	public String getStringToCompare() {
		return "TERRAIN" + Arrays.deepHashCode(terrainHeightMap) + cellSpace;
	}

}
