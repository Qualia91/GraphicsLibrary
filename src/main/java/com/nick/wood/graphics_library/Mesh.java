package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Mesh {

	private Vertex[] vertices;
	private int[] indices;
	private Material material;
	private int vao, pbo, ibo, cbo, tbo, nbo;

	public Mesh(Vertex[] vertices, int[] indices, Material material) {
		this.vertices = vertices;
		this.indices = indices;
		this.material = material;
	}

	public void create() {
		material.create();
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		BiFunction<Vertex, Integer, Double> positionDataGettersBiFunc = (vertex, index) -> vertex.getPos().getValues()[index];
		double[] posData = createDataForBuffer(vertices.length * 3, positionDataGettersBiFunc);
		DoubleBuffer positionBuffer = createDoubleBufferAndPutData(vertices.length * 3, posData);
		pbo = writeDataToBuffer(GL15.GL_ARRAY_BUFFER, bufferType -> {
			GL15.glBufferData(bufferType, positionBuffer, GL15.GL_STATIC_DRAW);
			// shader stuff
			GL20.glVertexAttribPointer(0, 3, GL11.GL_DOUBLE, false, 0, 0);
		});

		FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
		float[] textData = new float[vertices.length * 2];
		for (int i = 0; i < vertices.length; i++) {
			textData[i * 2] = vertices[i].getTextureCoord().getX();
			textData[i * 2 + 1] = vertices[i].getTextureCoord().getY();
		}
		textureBuffer.put(textData).flip();
		tbo = writeDataToBuffer(GL15.GL_ARRAY_BUFFER, bufferType -> {
			GL15.glBufferData(bufferType, textureBuffer, GL15.GL_STATIC_DRAW);
			// shader stuff
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		});

		BiFunction<Vertex, Integer, Double> normalDataGettersBiFunc = (vertex, index) -> vertex.getNormal().getValues()[index];
		double[] norData = createDataForBuffer(vertices.length * 3, normalDataGettersBiFunc);
		DoubleBuffer normBuffer = createDoubleBufferAndPutData(vertices.length * 3, norData);
		nbo = writeDataToBuffer(GL15.GL_ARRAY_BUFFER, bufferType -> {
			GL15.glBufferData(bufferType, normBuffer, GL15.GL_STATIC_DRAW);
			// shader stuff
			GL20.glVertexAttribPointer(2, 3, GL11.GL_DOUBLE, false, 0, 0);
		});

		IntBuffer indicesBuffer = createIntBufferAndPutData(indices.length, indices);
		ibo = writeDataToBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, bufferType -> {
			GL15.glBufferData(bufferType, indicesBuffer, GL15.GL_STATIC_DRAW);
		});

	}

	public void destroy() {
		material.destroy();
		GL15.glDeleteBuffers(nbo);
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(cbo);
		GL15.glDeleteBuffers(ibo);
		GL30.glDeleteTextures(tbo);
		GL30.glDeleteVertexArrays(vao);
	}

	private DoubleBuffer createDoubleBufferAndPutData(int amount, double[] data) {
		DoubleBuffer buffer = MemoryUtil.memAllocDouble(amount);
		buffer.put(data).flip();
		return buffer;
	}

	private IntBuffer createIntBufferAndPutData(int amount, int[] data) {
		IntBuffer buffer = MemoryUtil.memAllocInt(amount);
		buffer.put(data).flip();
		return buffer;
	}

	private double[] createDataForBuffer(int amount, BiFunction<Vertex, Integer, Double> getDataFunctionArray) {
		double[] data = new double[amount];

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

	public int getVao() {
		return vao;
	}

	public int getPbo() {
		return pbo;
	}

	public int getIbo() {
		return ibo;
	}

	public int getCbo() {
		return cbo;
	}

	public int getTbo() {
		return tbo;
	}

	public Material getMaterial() {
		return material;
	}

	public int getNbo() {
		return nbo;
	}
}
