package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;

public class Mesh {

	private final boolean invertedNormals;
	private boolean created = false;
	private Vertex[] vertices;
	private int[] indices;
	private Material material;
	private int vao, pbo, ibo, tbo, nbo;

	Mesh(Vertex[] vertices, int[] indices, Material material, boolean invertedNormals) {
		this.vertices = vertices;
		this.indices = indices;
		this.material = material;
		this.invertedNormals = invertedNormals;
	}


	public void createWithoutMaterial() {
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);




		// PBO
		float[] posData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			posData[i * 3] =     vertices[i].getPos().get(0);
			posData[i * 3 + 1] = vertices[i].getPos().get(1);
			posData[i * 3 + 2] = vertices[i].getPos().get(2);
		}
		FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		positionBuffer.put(posData).flip();
		pbo = GL15.glGenBuffers();
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pbo);
		// put data in
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(positionBuffer);




		// TBO
		float[] texData = new float[vertices.length * 2];
		for (int i = 0; i < vertices.length; i++) {
			texData[i * 2] =     vertices[i].getTextureCoord().getX();
			texData[i * 2 + 1] = vertices[i].getTextureCoord().getY();
		}
		FloatBuffer textBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
		textBuffer.put(texData).flip();
		tbo = GL15.glGenBuffers();
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tbo);
		// put data in
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textBuffer, GL15.GL_STATIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(textBuffer);





		// NBO
		float[] normData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			normData[i * 3] =     vertices[i].getNormal().get(0);
			normData[i * 3 + 1] = vertices[i].getNormal().get(1);
			normData[i * 3 + 2] = vertices[i].getNormal().get(2);
		}
		FloatBuffer normBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		normBuffer.put(normData).flip();
		nbo = GL15.glGenBuffers();
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		// put data in
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuffer, GL15.GL_STATIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(normBuffer);




		// IBO
		IntBuffer buffer = MemoryUtil.memAllocInt(indices.length);
		buffer.put(indices).flip();
		ibo = GL15.glGenBuffers();
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		// put data in
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(3, 3, GL11.GL_INT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(buffer);


		created = true;
	}

	public void create() {
		material.create();
		createWithoutMaterial();
	}

	public void initRender() {
		GL30.glBindVertexArray(vao);
		// enable position attribute
		GL30.glEnableVertexAttribArray(0);
		// enable texture attribute
		GL30.glEnableVertexAttribArray(1);
		// enable normal
		GL30.glEnableVertexAttribArray(2);
		GL30.glEnableVertexAttribArray(3);
	}

	public void endRender() {
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	public void destroy() {
		material.destroy();
		GL15.glDeleteBuffers(nbo);
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(ibo);
		GL30.glDeleteTextures(tbo);
		GL30.glDeleteVertexArrays(vao);
		created = false;
	}

	public void destroyWithoutDestroyingMaterial() {
		GL15.glDeleteBuffers(nbo);
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(ibo);
		GL30.glDeleteTextures(tbo);
		GL30.glDeleteVertexArrays(vao);
		created = false;
	}

	private FloatBuffer createFloatBufferAndPutData(int amount, float[] data) {
		FloatBuffer buffer = MemoryUtil.memAllocFloat(amount);
		buffer.put(data).flip();
		return buffer;
	}

	private IntBuffer createIntBufferAndPutData(int amount, int[] data) {
		IntBuffer buffer = MemoryUtil.memAllocInt(amount);
		buffer.put(data).flip();
		return buffer;
	}

	private float[] createDataForBuffer(int amount, BiFunction<Vertex, Integer, Float> getDataFunctionArray) {
		float[] data = new float[amount];

		for (int i = 0; i < vertices.length; i++) {
			data[i * 3] =     getDataFunctionArray.apply(vertices[i], 0);
			data[i * 3 + 1] = getDataFunctionArray.apply(vertices[i], 1);
			data[i * 3 + 2] = getDataFunctionArray.apply(vertices[i], 2);
		}

		return data;
	}

	private int writeDataToBuffer(int bufferType, Consumer<Integer> bufferDataConsumer) {

		int bufferId = GL15.glGenBuffers();
		// bind to buffer
		GL15.glBindBuffer(bufferType, bufferId);
		// put data in
		bufferDataConsumer.accept(bufferType);
		// unbind from buffer
		GL15.glBindBuffer(bufferType, 0);

		return bufferId;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public Material getMaterial() {
		return material;
	}

	public int getVao() {
		return vao;
	}

	public int getPbo() {
		return pbo;
	}

	public int getIbo() {
		return ibo;
	}

	public int getTbo() {
		return tbo;
	}

	public int getNbo() {
		return nbo;
	}

	public int getVertexCount() {
		return indices.length;
	}

	public boolean isCreated() {
		return created;
	}

}
