package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Mesh;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.Vec2f;
import com.nick.wood.maths.objects.Vec3d;
import org.lwjgl.assimp.*;

import java.io.IOException;

public class ModelLoader {

	public static Mesh loadModel(String filePath, String texturePath) throws IOException {
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

			Vec3d vertexVec = getVecFromData(vertices, i);
			Vec3d normalVec = getVecFromData(normals, i);
			Vec2f texCoord = Vec2f.ZERO;
			if (aiMesh.mNumUVComponents().get(0) != 0) {
				AIVector3D textCoordAI = aiMesh.mTextureCoords(0).get(i);
				texCoord = new Vec2f(textCoordAI.x(), textCoordAI.y());
			}
			vertexArray[i] = new Vertex(vertexVec, texCoord, normalVec);

		}

		int faceCount = aiMesh.mNumFaces();
		AIFace.Buffer indices = aiMesh.mFaces();
		int[] indexList = new int[faceCount * 3];

		for (int i = 0; i < faceCount; i++) {
			AIFace aiFace = indices.get(i);
			indexList[i * 3 + 0] = aiFace.mIndices().get(0);
			indexList[i * 3 + 1] = aiFace.mIndices().get(1);
			indexList[i * 3 + 2] = aiFace.mIndices().get(2);
		}

		return new Mesh(vertexArray, indexList, new Material(texturePath));
	}

	private static Vec3d getVecFromData(AIVector3D.Buffer buffer, int i) {
		AIVector3D aiVector3D = buffer.get(i);
		return new Vec3d(aiVector3D.x(), aiVector3D.y(), aiVector3D.z());
	}
}
