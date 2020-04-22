package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class CubeMesh implements MeshObject {

	private final Matrix4f rotationOfModel = Matrix4f.Identity;
	private final Mesh mesh;

	private int[] triangleIndexes = new int[]{
			//Back face
			0, 1, 3,
			3, 1, 2,

			//Front face
			4, 5, 7,
			7, 5, 6,

			//Right face
			8, 9, 11,
			11, 9, 10,

			//Left face
			12, 13, 15,
			15, 13, 14,

			//Top face
			16, 17, 19,
			19, 17, 18,

			//Bottom face
			20, 21, 23,
			23, 21, 22
	};

	public CubeMesh(boolean invertedNormals, Material material) {

		int normalSign = 1;
		if (invertedNormals) {
			normalSign = -1;
			int[] temp = new int[triangleIndexes.length];
			for (int i = 0; i < triangleIndexes.length; i++) {
				temp[i] = triangleIndexes[triangleIndexes.length - 1 - i];
			}
			triangleIndexes = temp;
		}

		mesh = new Mesh(new Vertex[]{

				//Back Face
				new Vertex(new Vec3f(0.5f, 0.5f, 0.5f), new Vec2f(1.0f, 0.0f), Vec3f.X.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, 0.5f), new Vec2f(1.0f, 1.0f), Vec3f.X.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, -0.5f), new Vec2f(0.0f, 1.0f), Vec3f.X.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, 0.5f, -0.5f), new Vec2f(0.0f, 0.0f), Vec3f.X.scale(normalSign)),

				//Front Face
				new Vertex(new Vec3f(-0.5f, 0.5f, -0.5f), new Vec2f(0.0f, 0.0f), Vec3f.X.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, -0.5f), new Vec2f(0.0f, 1.0f), Vec3f.X.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, 0.5f), new Vec2f(1.0f, 1.0f), Vec3f.X.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, 0.5f, 0.5f), new Vec2f(1.0f, 0.0f), Vec3f.X.scale(-normalSign)),

				//Bottom Face
				new Vertex(new Vec3f(0.5f, 0.5f, -0.5f), new Vec2f(1.0f, 0.0f), Vec3f.Z.scale(-normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, -0.5f), new Vec2f(1.0f, 1.0f), Vec3f.Z.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, -0.5f), new Vec2f(0.0f, 1.0f), Vec3f.Z.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, 0.5f, -0.5f), new Vec2f(0.0f, 0.0f), Vec3f.Z.scale(-normalSign)),

				//Top Face
				new Vertex(new Vec3f(-0.5f, 0.5f, 0.5f), new Vec2f(0.0f, 0.0f), Vec3f.Z.scale(normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, 0.5f), new Vec2f(0.0f, 1.0f), Vec3f.Z.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, 0.5f), new Vec2f(1.0f, 1.0f), Vec3f.Z.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, 0.5f, 0.5f), new Vec2f(1.0f, 0.0f), Vec3f.Z.scale(normalSign)),

				//Left Face
				new Vertex(new Vec3f(0.5f, 0.5f, 0.5f), new Vec2f(1.0f, 0.0f), Vec3f.Y.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, 0.5f, -0.5f), new Vec2f(1.0f, 1.0f), Vec3f.Y.scale(normalSign)),
				new Vertex(new Vec3f(-0.5f, 0.5f, -0.5f), new Vec2f(0.0f, 1.0f), Vec3f.Y.scale(normalSign)),
				new Vertex(new Vec3f(-0.5f, 0.5f, 0.5f), new Vec2f(0.0f, 0.0f), Vec3f.Y.scale(normalSign)),

				//Right Face
				new Vertex(new Vec3f(-0.5f, -0.5f, 0.5f), new Vec2f(0.0f, 0.0f), Vec3f.Y.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, -0.5f), new Vec2f(0.0f, 1.0f), Vec3f.Y.scale(-normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, -0.5f), new Vec2f(1.0f, 1.0f), Vec3f.Y.scale(-normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, 0.5f), new Vec2f(1.0f, 0.0f), Vec3f.Y.scale(-normalSign)),
		},
				triangleIndexes,
				material);
	}

	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Matrix4f getRotationOfModel() {
		return rotationOfModel;
	}

	@Override
	public String getStringToCompare() {
		return "CUBE";
	}

}
