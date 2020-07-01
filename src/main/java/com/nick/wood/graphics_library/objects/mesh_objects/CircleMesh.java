package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.materials.Material;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class CircleMesh implements MeshObject {

	private final Mesh mesh;
	private final Material material;
	private final String fboCameraName;
	private final int numberOfPointsAroundEdge;
	private Transform transformation;

	// package private so you have to use builder so builder can build mesh's when open gl is initialised
	CircleMesh(Transform transformation, Material material, int numberOfPointsAroundEdge, String fboCameraName) {
		this.fboCameraName = fboCameraName;
		this.transformation = transformation;
		this.numberOfPointsAroundEdge = numberOfPointsAroundEdge;

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
			indexArray[3*i] = i % (numberOfPointsAroundEdge);

			// edge bottom
			indexArray[3*i + 1] = (i + 1) % (numberOfPointsAroundEdge);

			// edge top
			indexArray[3*i + 2] = numberOfPointsAroundEdge + 1;

		}

		mesh = new Mesh(
				vertexArray,
				indexArray,
				material,
				false,
				true);
		this.material = material;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setTransformation(Transform transformation) {
		this.transformation = transformation;
	}

	@Override
	public Transform getMeshTransformation() {
		return transformation;
	}

	@Override
	public String getStringToCompare() {
		return MeshType.CIRCLE.toString() + numberOfPointsAroundEdge + material.getTexturePath() + fboCameraName;
	}

	@Override
	public String getFboTextureCameraName() {
		return fboCameraName;
	}

	@Override
	public MeshType getMeshType() {
		return MeshType.CIRCLE;
	}
}
