package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Triangle implements MeshObject {
	
	private Matrix4f transformation;
	private final Mesh mesh;
	private final int triangleNumber;

	public Triangle(Matrix4f transform, int triangleNumber, boolean invertedNormals) {

		this.transformation = transform;
		this.triangleNumber = triangleNumber;

		Vertex[][] vertexSlicesArray = new Vertex[triangleNumber+1][];

		Vec3f frontLeft = new Vec3f(-1, 1, 0);
		Vec3f frontRight = new Vec3f(-1, -1, 0);
		Vec3f top = new Vec3f(0, 0, 1);

		float lengthOfTriangleSide =  top.subtract(frontLeft).length() / triangleNumber;

		Vec3f frontLeftToTopVec = top.subtract(frontLeft).normalise().scale(lengthOfTriangleSide);
		Vec3f topToFrontRightVec = top.subtract(frontRight).normalise().scale(lengthOfTriangleSide).neg();

		// work out vertices in strips
		for (int triangleUpIndex = 0; triangleUpIndex < triangleNumber + 1; triangleUpIndex++) {

			Vertex[] vertexArray = new Vertex[triangleUpIndex+1];

			Vec3f startingPos = frontLeft.add(frontLeftToTopVec.scale(triangleUpIndex));

			// for every triangle side going back down, so max is current triangleUpIndex inclusive
			for (int triangleDownIndex = 0; triangleDownIndex <= triangleUpIndex; triangleDownIndex++) {

				Vec3f newPos = startingPos.add(topToFrontRightVec.scale(triangleDownIndex));

				vertexArray[triangleDownIndex] = new Vertex(
						newPos.normalise().scale(0.5f),
						new Vec2f(0.0f, 0.0f),
						Vec3f.X
				);

			}

			vertexSlicesArray[triangleUpIndex] = vertexArray;

		}

		int numOfVerts = ((triangleNumber + 1) * (triangleNumber + 1) + (triangleNumber + 1)) / 2;
		int numOfIndices = 3 * (triangleNumber * triangleNumber);
		int[] indexList = new int[numOfIndices];
		int indexCounter = 0;

		Vertex[] vertices = new Vertex[numOfVerts];

		// use strips to work out indices for triangles
		// start with current strip and only go up to second to last strip
		for (int sliceIndex = 0; sliceIndex < vertexSlicesArray.length - 1; sliceIndex++) {

			Vertex[] startingVertexArray = vertexSlicesArray[sliceIndex];

			// for each vertex in previous slice, get the 2 vertices it links to in the current slice
			for (int i = 0; i < startingVertexArray.length; i++) {
				int startIndex = ((sliceIndex * sliceIndex) + sliceIndex) / 2;
				vertices[startIndex + i] = startingVertexArray[i];

				// get the next strips vertex array
				Vertex[] nextVertexArray = vertexSlicesArray[sliceIndex + 1];

				int nextSliceIndex = sliceIndex + 1;
				int nextStartIndex = ((nextSliceIndex * nextSliceIndex) + nextSliceIndex) / 2;
				int nextIndex = nextStartIndex + i;
				int nextNextIndex = nextStartIndex + i + 1;
				// get vertex in the next strip equal to the current index of vertex, and +1 to this index
				vertices[nextIndex] = nextVertexArray[i];
				vertices[nextNextIndex] = nextVertexArray[i + 1];

				// add these indexes onto the index array
				indexList[indexCounter++] = startIndex + i;
				indexList[indexCounter++] = nextIndex;
				indexList[indexCounter++] = nextNextIndex;
			}

			// now do the ones i missed. must be a better way of doing this...
			if (sliceIndex != 0) {
				for (int i = 0; i < startingVertexArray.length - 1; i++) {
					int startIndex = i + ((sliceIndex * sliceIndex) + sliceIndex) / 2;
					int nextStartIndex = startIndex + 1;

					int nextSliceIndex = sliceIndex + 1;
					int nextSliceVertexIndex = (((nextSliceIndex * nextSliceIndex) + nextSliceIndex) / 2) + 1 + i;

					// add these indexes onto the index array
					indexList[indexCounter++] = startIndex;
					indexList[indexCounter++] = nextSliceVertexIndex;
					indexList[indexCounter++] = nextStartIndex;
				}
			}

		}

		mesh = new Mesh(vertices, indexList, new Material("/textures/texture.png"), invertedNormals, false);
	}

	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Matrix4f getMeshTransformation() {
		return transformation;
	}

	@Override
	public String getStringToCompare() {
		return "TRIANGLE" + mesh.getMaterial().getPath();
	}

}
