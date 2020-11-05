package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.maths.objects.matrix.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class BatchMesh {

	private final int MAX_VERTEX_NUMBER = 1000;
	private int vao, pbo, ibo, tbo, nbo, tabo, btabo;
	private int currentVertexCount = 0;
	private int currentIndexCount = 0;

	public void create() {

		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);


		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pbo);
		// create empty buffer
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_VERTEX_NUMBER * 3, GL15.GL_DYNAMIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


		// TBO
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tbo);
		// create empty buffer
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_VERTEX_NUMBER * 2, GL15.GL_DYNAMIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


		// NBO
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		// put data in
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_VERTEX_NUMBER * 3, GL15.GL_DYNAMIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


		// IBO
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		// put data in
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, MAX_VERTEX_NUMBER * 9, GL15.GL_DYNAMIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(3, 3, GL11.GL_INT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);


		// TABO
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tabo);
		// put data in
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_VERTEX_NUMBER * 3, GL15.GL_DYNAMIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(8, 3, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


		// BTABO
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, btabo);
		// put data in
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_VERTEX_NUMBER * 3, GL15.GL_DYNAMIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(9, 3, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


		// enable position attribute
		GL30.glEnableVertexAttribArray(0);
		// enable texture attribute
		GL30.glEnableVertexAttribArray(1);
		// enable normal
		GL30.glEnableVertexAttribArray(2);
		// enable index
		GL30.glEnableVertexAttribArray(3);
		// enable tangent
		GL30.glEnableVertexAttribArray(8);
		// enable bitangent
		GL30.glEnableVertexAttribArray(9);
	}

	public int getCurrentVertexCount() {
		return currentVertexCount;
	}

	public void destroy() {

		GL15.glDeleteBuffers(tabo);
		GL15.glDeleteBuffers(btabo);
		GL15.glDeleteBuffers(nbo);
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(ibo);
		GL15.glDeleteBuffers(tbo);
		GL30.glDeleteVertexArrays(vao);

	}

	public int getMAX_VERTEX_NUMBER() {
		return MAX_VERTEX_NUMBER;
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

	public int getTabo() {
		return tabo;
	}

	public int getBtabo() {
		return btabo;
	}

	public void addVertex(Vertex vertex, Matrix4f transformation) {

	}

	public void addIndex(int index) {

	}

	public void init() {

	}
}
