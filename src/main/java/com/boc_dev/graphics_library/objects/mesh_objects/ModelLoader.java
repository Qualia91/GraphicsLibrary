package com.boc_dev.graphics_library.objects.mesh_objects;

import com.boc_dev.graphics_library.objects.mesh_objects.renderer_objects.RendererObject;
import com.boc_dev.graphics_library.utils.FileUtils;
import com.boc_dev.maths.objects.vector.Vec2f;
import com.boc_dev.maths.objects.vector.Vec3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ModelLoader {

	public Mesh loadModel(String filePath, RendererObject rendererObject) throws IOException, URISyntaxException {

		String lib_data_file_path = System.getenv("GRAPHICS_LIB_DATA") + "\\" + filePath;

		String default_data_file_path = "";
		URL url = getClass().getResource("/" + filePath);
		if (url != null) {
			URI uri = url.toURI();
			File file = new File(uri);

			if (file.exists()) {
				default_data_file_path = file.getAbsolutePath();
				System.out.println("HERE");
			} else {
				System.out.println(file.toString());
			}
		}

		if (!new File(lib_data_file_path).exists()) {
			lib_data_file_path = default_data_file_path;
		}


		AIScene aiScene = Assimp.aiImportFile(lib_data_file_path, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace);

		if (aiScene == null) {
			throw new IOException(Assimp.aiGetErrorString());
		}

		// get first mesh in file
		AIMesh aiMesh = AIMesh.create(aiScene.mMeshes().get(0));
		int vertexCount = aiMesh.mNumVertices();

		AIVector3D.Buffer vertices = aiMesh.mVertices();
		AIVector3D.Buffer normals = aiMesh.mNormals();
		AIVector3D.Buffer tangents = aiMesh.mTangents();
		AIVector3D.Buffer bitangents = aiMesh.mBitangents();

		Vertex[] vertexArray = new Vertex[vertexCount];

		for (int i = 0; i < vertexCount; i++) {

			Vec3f vertexVec = getVecFromData(vertices, i);
			Vec3f normalVec = getVecFromData(normals, i);
			Vec3f tangentVec = getVecFromData(tangents, i);
			Vec3f bitangentVec = getVecFromData(bitangents, i);
			Vec2f texCoord = Vec2f.ZERO;
			if (aiMesh.mTextureCoords(0) != null) {
				AIVector3D textCoordAI = aiMesh.mTextureCoords(0).get(i);
				texCoord = new Vec2f(textCoordAI.x(), textCoordAI.y());
			}
			vertexArray[i] = new Vertex(vertexVec, texCoord, normalVec, tangentVec, bitangentVec);

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

		return new SingleMesh(vertexArray, indexList, rendererObject);
	}

	private Vec3f getVecFromData(AIVector3D.Buffer buffer, int i) {
		AIVector3D aiVector3D = buffer.get(i);
		return new Vec3f(aiVector3D.x(), aiVector3D.y(), aiVector3D.z());
	}
}
