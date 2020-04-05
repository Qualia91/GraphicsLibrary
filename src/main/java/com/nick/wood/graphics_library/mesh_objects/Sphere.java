package com.nick.wood.graphics_library.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Mesh;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec2f;
import com.nick.wood.maths.objects.Vec3d;

public class Sphere implements MeshObject {

	private Vec3d position, scale;
	private Matrix4d rotation;
	private final int triangleNumber;

	private Mesh mesh;

	public Sphere(Vec3d translation, Vec3d dimensions, Matrix4d rotation, int triangleNumber) {

		this.position = translation;
		this.scale = dimensions;
		this.rotation = rotation;
		this.triangleNumber = triangleNumber;

		Vec3d startFrontLeft = new Vec3d(-1.0, 1.0, 0.0);
		Vec3d startFrontRight = new Vec3d(-1.0, -1.0, 0.0);
		Vec3d startTop = new Vec3d(0.0, 0.0, 1.0);

		int vertexStartingNumber = 0;
		int indexCounter = 0;

		int pascalNum = (((triangleNumber + 1) * (triangleNumber + 1) + (triangleNumber + 1)) / 2);

		int numOfVerts = (((triangleNumber + 1) * (triangleNumber + 1) + (triangleNumber + 1)) / 2) * 8;
		int numOfIndices = (3 * ((triangleNumber + 1) * (triangleNumber + 1))) * 8;
		int[] indexList = new int[numOfIndices];

		float spacingBetweenTextCoord = 1.0f / (triangleNumber * 8.0f);
		float xCoord = 0.0f;

		Vertex[] vertices = new Vertex[numOfVerts];

		for (int zRotation = 0; zRotation < 360; zRotation+= 90) {

			Matrix4d zRotationMatrix = Matrix4d.Rotation(zRotation, Vec3d.Z);

			float yCoord = 0.0f;

			for (int yRotation = 0; yRotation < 360; yRotation+= 180) {

				Vertex[][] vertexSlicesArray = new Vertex[(triangleNumber+1)][];

				Matrix4d matrixRotation = zRotationMatrix.multiply(Matrix4d.Rotation(yRotation, Vec3d.Y));

				Vec3d frontLeft = matrixRotation.multiply(startFrontLeft);
				Vec3d frontRight = matrixRotation.multiply(startFrontRight);
				Vec3d top = matrixRotation.multiply(startTop);

				double lengthOfTriangleSide =  top.subtract(frontLeft).length() / triangleNumber;

				Vec3d frontLeftToTopVec = top.subtract(frontLeft).normalise().scale(lengthOfTriangleSide);
				Vec3d topToFrontRightVec = top.subtract(frontRight).normalise().scale(lengthOfTriangleSide).neg();

				// work out vertices in strips
				for (int triangleUpIndex = 0; triangleUpIndex < triangleNumber + 1; triangleUpIndex++) {

					Vertex[] vertexArray = new Vertex[triangleUpIndex+1];

					Vec3d startingPos = frontLeft.add(frontLeftToTopVec.scale(triangleUpIndex));

					// for every triangle side going back down, so max is current triangleUpIndex inclusive
					for (int triangleDownIndex = 0; triangleDownIndex <= triangleUpIndex; triangleDownIndex++) {

						Vec3d newPos = startingPos.add(topToFrontRightVec.scale(triangleDownIndex));

						vertexArray[triangleDownIndex] = new Vertex(
								newPos.normalise().scale(0.5),
								newPos,
								new Vec2f(xCoord, yCoord)
						);

						yCoord += spacingBetweenTextCoord;

					}

					vertexSlicesArray[triangleUpIndex] = vertexArray;

				}

				// use strips to work out indices for triangles
				// start with current strip and only go up to second to last strip
				for (int sliceIndex = 0; sliceIndex < vertexSlicesArray.length - 1; sliceIndex++) {

					Vertex[] startingVertexArray = vertexSlicesArray[sliceIndex];

					// for each vertex in previous slice, get the 2 vertices it links to in the current slice
					for (int i = 0; i < startingVertexArray.length; i++) {
						int startIndex = ((sliceIndex * sliceIndex) + sliceIndex) / 2;
						vertices[vertexStartingNumber + startIndex + i] = startingVertexArray[i];

						// get the next strips vertex array
						Vertex[] nextVertexArray = vertexSlicesArray[sliceIndex + 1];

						int nextSliceIndex = sliceIndex + 1;
						int nextStartIndex = ((nextSliceIndex * nextSliceIndex) + nextSliceIndex) / 2;
						int nextIndex = nextStartIndex + i;
						int nextNextIndex = nextStartIndex + i + 1;
						// get vertex in the next strip equal to the current index of vertex, and +1 to this index
						vertices[vertexStartingNumber + nextIndex] = nextVertexArray[i];
						vertices[vertexStartingNumber + nextNextIndex] = nextVertexArray[i + 1];

						// add these indexes onto the index array
						indexList[vertexStartingNumber + indexCounter++] = vertexStartingNumber + startIndex + i;
						indexList[vertexStartingNumber + indexCounter++] = vertexStartingNumber + nextIndex;
						indexList[vertexStartingNumber + indexCounter++] = vertexStartingNumber + nextNextIndex;
					}

					// now do the ones i missed. must be a better way of doing this...
					if (sliceIndex != 0) {
						for (int i = 0; i < startingVertexArray.length - 1; i++) {
							int startIndex = i + ((sliceIndex * sliceIndex) + sliceIndex) / 2;
							int nextStartIndex = startIndex + 1;

							int nextSliceIndex = sliceIndex + 1;
							int nextSliceVertexIndex = (((nextSliceIndex * nextSliceIndex) + nextSliceIndex) / 2) + 1 + i;

							// add these indexes onto the index array
							indexList[vertexStartingNumber + indexCounter++] = vertexStartingNumber + startIndex;
							indexList[vertexStartingNumber + indexCounter++] = vertexStartingNumber + nextSliceVertexIndex;
							indexList[vertexStartingNumber + indexCounter++] = vertexStartingNumber + nextStartIndex;
						}
					}

				}

				vertexStartingNumber += pascalNum;

				xCoord += spacingBetweenTextCoord;

			}
		}



		mesh = new Mesh(vertices, indexList, new Material("/textures/texture.png"));
	}

	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Matrix4d getTransformation() {
		return Matrix4d.Transform(position, rotation, scale);
	}
}
