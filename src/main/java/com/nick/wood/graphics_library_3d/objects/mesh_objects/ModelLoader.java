package com.nick.wood.graphics_library_3d.objects.mesh_objects;

import com.nick.wood.graphics_library_3d.Material;
import com.nick.wood.graphics_library_3d.Vertex;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.lwjgl.assimp.*;

import java.io.IOException;

public class ModelLoader {

	public Mesh loadModel(String filePath, String texturePath, boolean invertedNormals) throws IOException {
		// load 3d model data
		AIScene aiScene = Assimp.aiImportFile(filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate);

		if (aiScene == null) {
			throw new IOException(Assimp.aiGetErrorString());
		}

		// get first mesh in file
		AIMesh aiMesh = AIMesh.create(aiScene.mMeshes().get(0));
		int vertexCount = aiMesh.mNumVertices();

		AIVector3D.Buffer vertices = aiMesh.mVertices();
		AIVector3D.Buffer normals = aiMesh.mNormals();

		Vertex[] vertexArray = new Vertex[vertexCount];

		for (int i = 0; i < vertexCount; i++) {

			Vec3f vertexVec = getVecFromData(vertices, i);
			Vec3f normalVec = getVecFromData(normals, i);
			if (invertedNormals) {
				normalVec = normalVec.neg();
			}
			Vec2f texCoord = Vec2f.ZERO;
			if (aiMesh.mTextureCoords(0) != null) {
				AIVector3D textCoordAI = aiMesh.mTextureCoords(0).get(i);
				texCoord = new Vec2f(textCoordAI.x(), textCoordAI.y());
			}
			vertexArray[i] = new Vertex(vertexVec, texCoord, normalVec);

		}

		int faceCount = aiMesh.mNumFaces();
		AIFace.Buffer indices = aiMesh.mFaces();
		int[] indexList = new int[faceCount * 3];

		if (invertedNormals) {
			for (int i = 0; i < faceCount; i++) {
				AIFace aiFace = indices.get(i);
				indexList[i * 3 + 2] = aiFace.mIndices().get(0);
				indexList[i * 3 + 1] = aiFace.mIndices().get(1);
				indexList[i * 3 + 0] = aiFace.mIndices().get(2);
			}
		} else {
			for (int i = 0; i < faceCount; i++) {
				AIFace aiFace = indices.get(i);
				indexList[i * 3 + 0] = aiFace.mIndices().get(0);
				indexList[i * 3 + 1] = aiFace.mIndices().get(1);
				indexList[i * 3 + 2] = aiFace.mIndices().get(2);
			}
		}

		return new Mesh(vertexArray, indexList, new Material(texturePath), invertedNormals);
	}

	private Vec3f getVecFromData(AIVector3D.Buffer buffer, int i) {
		AIVector3D aiVector3D = buffer.get(i);
		return new Vec3f(aiVector3D.x(), aiVector3D.y(), aiVector3D.z());
	}
}
