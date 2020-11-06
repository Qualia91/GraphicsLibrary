package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.renderer_objects.RendererObject;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class BasicMeshCreator {

	public Mesh createCircle(int numberOfPointsAroundEdge, RendererObject rendererObject) {

		Vertex[] vertexArray = new Vertex[numberOfPointsAroundEdge + 2];

		// put the center as 0
		vertexArray[numberOfPointsAroundEdge + 1] = new Vertex(
				Vec3f.ZERO,
				new Vec2f(0.5f, 0.5f),
				Vec3f.X.neg(),
				Vec3f.Y.neg(),
				Vec3f.Z.neg()
		);

		double angleStep = (Math.PI * 2) / numberOfPointsAroundEdge;
		// plus one so we have a vertex at the end to to make the next loop easier
		for (int i = 0; i < numberOfPointsAroundEdge + 1; i++) {
			float y = (float) (0.5 * Math.sin(angleStep * i));
			float z = (float) (0.5 * Math.cos(angleStep * i));
			vertexArray[i] = new Vertex(
					new Vec3f(0, y, z),
					new Vec2f(-y + 0.5f, -z + 0.5f),
					Vec3f.X.neg(),
					Vec3f.Y.neg(),
					Vec3f.Z.neg()
			);
		}

		// now do indices
		int[] indexArray = new int[vertexArray.length * 3];

		for (int i = 0; i < numberOfPointsAroundEdge + 1; i++) {

			// center
			indexArray[3 * i] = i % (numberOfPointsAroundEdge);

			// edge bottom
			indexArray[3 * i + 1] = (i + 1) % (numberOfPointsAroundEdge);

			// edge top
			indexArray[3 * i + 2] = numberOfPointsAroundEdge + 1;

		}

		return new SingleMesh(vertexArray, indexArray, rendererObject);

	}

	public Mesh createCube(boolean invertedNormals, RendererObject rendererObject) {
		int[] indices = new int[]{
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

		int normalSign = 1;
		if (invertedNormals) {
			normalSign = -1;
			int[] temp = new int[indices.length];
			for (int i = 0; i < indices.length; i++) {
				temp[i] = indices[indices.length - 1 - i];
			}
			indices = temp;
		}

		float xSeg = (float) (1.0 / 3.0);
		double xSegDouble = 1.0 / 3.0;
		float ySeg = 0.5f;

		Vertex[] vertices = new Vertex[]{

				//Front Face
				new Vertex(new Vec3f(0.5f, 0.5f, 0.5f), new Vec2f(xSeg, ySeg), Vec3f.X.scale(normalSign), Vec3f.Y.scale(normalSign), Vec3f.Z.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, 0.5f), new Vec2f(0, ySeg), Vec3f.X.scale(normalSign), Vec3f.Y.scale(normalSign), Vec3f.Z.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, -0.5f), new Vec2f(0, 1), Vec3f.X.scale(normalSign), Vec3f.Y.scale(normalSign), Vec3f.Z.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, 0.5f, -0.5f), new Vec2f(xSeg, 1), Vec3f.X.scale(normalSign), Vec3f.Y.scale(normalSign), Vec3f.Z.scale(normalSign)),

				//Back Face
				new Vertex(new Vec3f(-0.5f, 0.5f, -0.5f), new Vec2f(xSeg, ySeg), Vec3f.X.scale(-normalSign), Vec3f.Y.scale(-normalSign), Vec3f.Z.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, -0.5f), new Vec2f((float) (xSegDouble * 2), ySeg), Vec3f.X.scale(-normalSign), Vec3f.Y.scale(-normalSign), Vec3f.Z.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, 0.5f), new Vec2f((float) (xSegDouble * 2), 0), Vec3f.X.scale(-normalSign), Vec3f.Y.scale(-normalSign), Vec3f.Z.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, 0.5f, 0.5f), new Vec2f(xSeg, 0), Vec3f.X.scale(-normalSign), Vec3f.Y.scale(-normalSign), Vec3f.Z.scale(-normalSign)),

				//Bottom Face
				new Vertex(new Vec3f(0.5f, 0.5f, -0.5f), new Vec2f((float) (xSegDouble * 2), ySeg), Vec3f.Z.scale(-normalSign), Vec3f.X.scale(-normalSign), Vec3f.Y.scale(-normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, -0.5f), new Vec2f((float) (xSegDouble * 2), 1), Vec3f.Z.scale(-normalSign), Vec3f.X.scale(-normalSign), Vec3f.Y.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, -0.5f), new Vec2f(1, 1), Vec3f.Z.scale(-normalSign), Vec3f.X.scale(-normalSign), Vec3f.Y.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, 0.5f, -0.5f), new Vec2f(1, ySeg), Vec3f.Z.scale(-normalSign), Vec3f.X.scale(-normalSign), Vec3f.Y.scale(-normalSign)),

				//Top Face
				new Vertex(new Vec3f(-0.5f, 0.5f, 0.5f), new Vec2f(xSeg, ySeg), Vec3f.Z.scale(normalSign), Vec3f.X.scale(normalSign), Vec3f.Y.scale(normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, 0.5f), new Vec2f(xSeg, 1), Vec3f.Z.scale(normalSign), Vec3f.X.scale(normalSign), Vec3f.Y.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, 0.5f), new Vec2f((float) (xSegDouble * 2), 1), Vec3f.Z.scale(normalSign), Vec3f.X.scale(normalSign), Vec3f.Y.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, 0.5f, 0.5f), new Vec2f((float) (xSegDouble * 2), ySeg), Vec3f.Z.scale(normalSign), Vec3f.X.scale(normalSign), Vec3f.Y.scale(normalSign)),

				//Left Face
				new Vertex(new Vec3f(0.5f, 0.5f, 0.5f), new Vec2f(0, 0), Vec3f.Y.scale(normalSign), Vec3f.Z.scale(normalSign), Vec3f.X.scale(normalSign)),
				new Vertex(new Vec3f(0.5f, 0.5f, -0.5f), new Vec2f(0, ySeg), Vec3f.Y.scale(normalSign), Vec3f.Z.scale(normalSign), Vec3f.X.scale(normalSign)),
				new Vertex(new Vec3f(-0.5f, 0.5f, -0.5f), new Vec2f(xSeg, ySeg), Vec3f.Y.scale(normalSign), Vec3f.Z.scale(normalSign), Vec3f.X.scale(normalSign)),
				new Vertex(new Vec3f(-0.5f, 0.5f, 0.5f), new Vec2f(xSeg, 0), Vec3f.Y.scale(normalSign), Vec3f.Z.scale(normalSign), Vec3f.X.scale(normalSign)),

				//Right Face
				new Vertex(new Vec3f(-0.5f, -0.5f, 0.5f), new Vec2f((float) (xSegDouble * 2), 0.0f), Vec3f.Y.scale(-normalSign), Vec3f.Z.scale(-normalSign), Vec3f.X.scale(-normalSign)),
				new Vertex(new Vec3f(-0.5f, -0.5f, -0.5f), new Vec2f((float) (xSegDouble * 2), ySeg), Vec3f.Y.scale(-normalSign), Vec3f.Z.scale(-normalSign), Vec3f.X.scale(-normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, -0.5f), new Vec2f(1.0f, ySeg), Vec3f.Y.scale(-normalSign), Vec3f.Z.scale(-normalSign), Vec3f.X.scale(-normalSign)),
				new Vertex(new Vec3f(0.5f, -0.5f, 0.5f), new Vec2f(1.0f, 0.0f), Vec3f.Y.scale(-normalSign), Vec3f.Z.scale(-normalSign), Vec3f.X.scale(-normalSign))};

		return new SingleMesh(vertices, indices, rendererObject);
	}

	public Mesh createSquare(RendererObject rendererObject) {
		Vertex[] vertices = new Vertex[] {
				new Vertex(new Vec3f(0.0f, -0.5f,  0.5f), new Vec2f(1.0f, 0.0f), Vec3f.X.neg(), Vec3f.Y.neg(), Vec3f.Z.neg()),
				new Vertex(new Vec3f(0.0f,  0.5f,  0.5f), new Vec2f(0.0f, 0.0f), Vec3f.X.neg(), Vec3f.Y.neg(), Vec3f.Z.neg()),
				new Vertex(new Vec3f(0.0f,  0.5f, -0.5f), new Vec2f(0.0f, 1.0f), Vec3f.X.neg(), Vec3f.Y.neg(), Vec3f.Z.neg()),
				new Vertex(new Vec3f(0.0f, -0.5f, -0.5f), new Vec2f(1.0f, 1.0f), Vec3f.X.neg(), Vec3f.Y.neg(), Vec3f.Z.neg())};
		int[] indices = new int[]{
				0, 1, 2,
				3, 0, 2
		};
		return new SingleMesh(vertices, indices, rendererObject);
	}



}
