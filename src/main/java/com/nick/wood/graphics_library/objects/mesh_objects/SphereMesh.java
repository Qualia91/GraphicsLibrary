package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.Matrix4f;
import com.nick.wood.maths.objects.Vec2f;
import com.nick.wood.maths.objects.Vec3f;

public class SphereMesh implements MeshObject {

	private final int triangleNumber;
	private Mesh mesh;

	public SphereMesh(int triangleNumber, Material material, boolean invertedNormals ) {

		this.triangleNumber = triangleNumber;

		int normalSign = invertedNormals ? 1 : -1;

		Vec3f startFrontLeft = new Vec3f(-1.0f, 1.0f, 0.0f);
		Vec3f startFrontRight = new Vec3f(-1.0f, -1.0f, 0.0f);
		Vec3f startTop = new Vec3f(0.0f, 0.0f, 1.0f);

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

			Matrix4f zRotationMatrix = Matrix4f.Rotation(zRotation, Vec3f.Z);

			float yCoord = 0.0f;

			for (int yRotation = 0; yRotation < 360; yRotation+= 180) {

				Vertex[][] vertexSlicesArray = new Vertex[(triangleNumber+1)][];

				Matrix4f matrixRotation = zRotationMatrix.multiply(Matrix4f.Rotation(yRotation, Vec3f.Y));

				Vec3f frontLeft = matrixRotation.multiply(startFrontLeft);
				Vec3f frontRight = matrixRotation.multiply(startFrontRight);
				Vec3f top = matrixRotation.multiply(startTop);

				float lengthOfTriangleSide =  top.subtract(frontLeft).length() / triangleNumber;

				Vec3f frontLeftToTopVec = top.subtract(frontLeft).normalise().scale(lengthOfTriangleSide);
				Vec3f topToFrontRightVec = top.subtract(frontRight).normalise().scale(lengthOfTriangleSide).neg();

				// work out vertices in strips
				for (int triangleUpIndex = 0; triangleUpIndex < triangleNumber + 1; triangleUpIndex++) {

					Vertex[] vertexArray = new Vertex[triangleUpIndex+1];

					Vec3f startingPos = frontLeft.add(frontLeftToTopVec.scale(triangleUpIndex));

					// for every triangle side going back down, so max is current triangleUpIndex inclusive
					for (int triangleDownIndex = 0; triangleDownIndex <= triangleUpIndex; triangleDownIndex++) {

						Vec3f newPosNorm = startingPos.add(topToFrontRightVec.scale(triangleDownIndex)).normalise();

						vertexArray[triangleDownIndex] = new Vertex(
								newPosNorm.scale(0.5f),
								new Vec2f(xCoord, yCoord),
								newPosNorm.scale(normalSign)
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


		// now reverse order on index so triangles are drawn correctly (because i did it wrong...)
		if (!invertedNormals) {
			int[] temp = new int[indexList.length];
			for (int i = 0; i < indexList.length; i++) {
				temp[i] = indexList[indexList.length - 1 - i];
			}
			indexList = temp;
		}


		mesh = new Mesh(vertices, indexList, material);
	}

	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Matrix4f getRotationOfModel() {
		return Matrix4f.Identity;
	}

	@Override
	public String getStringToCompare() {
		return "SPHERE";
	}

}
