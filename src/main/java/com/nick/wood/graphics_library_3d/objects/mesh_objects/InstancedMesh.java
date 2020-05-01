package com.nick.wood.graphics_library_3d.objects.mesh_objects;

import com.nick.wood.graphics_library_3d.Material;
import com.nick.wood.graphics_library_3d.Vertex;
import com.nick.wood.maths.objects.matrix.Matrix4d;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class InstancedMesh {

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int VECTOR4F_SIZE_BYTES = 4 * InstancedMesh.FLOAT_SIZE_BYTES;
	private static final int MATRIX_SIZE_BYTES = InstancedMesh.MATRIX_SIZE_FLOATS * InstancedMesh.FLOAT_SIZE_BYTES;

	private final MeshCommonData meshCommonData;
	private final Matrix4d modelViewMatrix;
	private final Matrix4d lightViewMatrix;
	private final int numInstances;
	private int modelViewVBO;
	private int lightViewVBO;
	private FloatBuffer instanceDataBuffer;

	public InstancedMesh(Vertex[] vertices, int[] indices, Material material, Matrix4d modelViewMatrix, Matrix4d lightViewMatrix, int numInstances) {
		meshCommonData = new MeshCommonData(vertices, indices, material);
		this.modelViewMatrix = modelViewMatrix;
		this.lightViewMatrix = lightViewMatrix;
		this.numInstances = numInstances;
	}

	public void create() {
		meshCommonData.create();

		// create buffer
		modelViewVBO = GL15.glGenBuffers();
		instanceDataBuffer = MemoryUtil.memAllocFloat(numInstances * MATRIX_SIZE_FLOATS);
		GL15.glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 3;
		for (int i = 0; i < 4; i++) {
			GL20.glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

	}

	public void initRender() {
		meshCommonData.initRender();
		int start = 3;
		int numElements = 4;// * 2;
		for (int i = 0; i < numElements; i++) {
			GL20.glEnableVertexAttribArray(start + i);
		}
	}

	public void endRender() {
		meshCommonData.endRender();
	}

	public int getVertexCount() {
		return 0;
	}

	public void destroy() {
		meshCommonData.destroy();
	}

	public Vertex[] getVertices() {
		return meshCommonData.getVertices();
	}

	public int[] getIndices() {
		return meshCommonData.getIndices();
	}

	public int getVao() {
		return meshCommonData.getVao();
	}

	public int getPbo() {
		return meshCommonData.getPbo();
	}

	public int getIbo() {
		return meshCommonData.getIbo();
	}

	public int getTbo() {
		return meshCommonData.getTbo();
	}

	public Material getMaterial() {
		return meshCommonData.getMaterial();
	}

	public int getNbo() {
		return meshCommonData.getNbo();
	}


}
