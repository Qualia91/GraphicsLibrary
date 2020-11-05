package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.DrawVisitor;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class SingleMesh implements Mesh {

	private Vertex[] vertices;
	private int[] indices;
	private int vao = -1, pbo, ibo, tbo, nbo, tabo, btabo;

	public SingleMesh(Vertex[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;
	}

	public void updateMesh(Vertex[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;
		create();
	}

	public void create() {

		vao = GL30.glGenVertexArrays();
		pbo = GL15.glGenBuffers();
		tbo = GL15.glGenBuffers();
		nbo = GL15.glGenBuffers();
		ibo = GL15.glGenBuffers();
		tabo = GL15.glGenBuffers();
		btabo = GL15.glGenBuffers();
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
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		// put data in
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuffer, GL15.GL_STATIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(normBuffer);



		// TABO
		float[] tangentData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			tangentData[i * 3] = vertices[i].getTangent().get(0);
			tangentData[i * 3 + 1] = vertices[i].getTangent().get(1);
			tangentData[i * 3 + 2] = vertices[i].getTangent().get(2);
		}
		FloatBuffer tangentBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		tangentBuffer.put(tangentData).flip();
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tabo);
		// put data in
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tangentBuffer, GL15.GL_STATIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(tangentBuffer);

		// BTABO
		float[] bitangentData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			bitangentData[i * 3] = vertices[i].getBitangent().get(0);
			bitangentData[i * 3 + 1] = vertices[i].getBitangent().get(1);
			bitangentData[i * 3 + 2] = vertices[i].getBitangent().get(2);
		}
		FloatBuffer bitangentBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		bitangentBuffer.put(bitangentData).flip();
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, btabo);
		// put data in
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bitangentBuffer, GL15.GL_STATIC_DRAW);
		// shader stuff
		GL20.glVertexAttribPointer(4, 3, GL11.GL_FLOAT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(bitangentBuffer);


		// IBO
		IntBuffer buffer = MemoryUtil.memAllocInt(indices.length);
		buffer.put(indices).flip();
		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		// put data in
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// shader stuff
		//GL20.glVertexAttribPointer(5, 3, GL11.GL_INT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(buffer);


	}

	public void initRender() {
		GL30.glBindVertexArray(vao);
		// enable position attribute
		GL30.glEnableVertexAttribArray(0);
		// enable texture attribute
		GL30.glEnableVertexAttribArray(1);
		// enable normal
		GL30.glEnableVertexAttribArray(2);
		// enable index
		GL30.glEnableVertexAttribArray(3);
		// enable tangent
		GL30.glEnableVertexAttribArray(4);


		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);

	}

	public void endRender() {
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glEnableVertexAttribArray(4);
		GL30.glDisableVertexAttribArray(3);
		GL30.glDisableVertexAttribArray(2);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	@Override
	public FloatBuffer getModelViewBuffer() {
		return null;
	}

	@Override
	public void draw(DrawVisitor drawVisitor, ArrayList<InstanceObject> value) {
		drawVisitor.draw(this, value);
	}

	@Override
	public int size() {
		return indices.length;
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

	public Vertex[] getVertices() {
		return vertices;
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

	public int getTabo() {
		return tabo;
	}

	public int getBtabo() {
		return btabo;
	}
}