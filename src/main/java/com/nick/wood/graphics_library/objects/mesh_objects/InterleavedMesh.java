package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.DrawVisitor;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class InterleavedMesh implements Mesh {

	private static final int FLOAT_BYTE_SIZE = 4;

	private Vertex[] vertices;
	private int[] indices;
	private int vao;
	private int vbo;
	private int ibo;

	public InterleavedMesh(Vertex[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;
	}

	public void create() {

		FloatBuffer interleavedBuffer = BufferUtils.createFloatBuffer(vertices.length * Vertex.NUM_OF_FLOATS);

		for (Vertex vertex : vertices) {

			interleavedBuffer
					.put(vertex.getPos().getValues())
					.put(vertex.getTextureCoord().getValues())
					.put(vertex.getNormal().getValues())
					.put(vertex.getTangent().getValues())
					.put(vertex.getBitangent().getValues());

		}

		interleavedBuffer.flip();

		// create vertex array object and bind
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		// Create a new VBO for our interleaved data
		this.vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, interleavedBuffer, GL15.GL_STATIC_DRAW);

		// total amount oif bytes per vertex (stride)
		int vertexFloatSizeInBytes = FLOAT_BYTE_SIZE * Vertex.NUM_OF_FLOATS;

		// now split interleaved data into 5 attribute lists needed for vertex
		glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, Vertex.POSITION_FLOAT_COUNT, GL11.GL_FLOAT,
				false, vertexFloatSizeInBytes, FLOAT_BYTE_SIZE * Vertex.POSITION_OFFSET);

		glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, Vertex.TEXTURE_COORD_FLOAT_COUNT, GL11.GL_FLOAT,
				false, vertexFloatSizeInBytes, FLOAT_BYTE_SIZE * Vertex.TEXTURE_COORD_OFFSET);

		glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, Vertex.NORMAL_FLOAT_COUNT, GL11.GL_FLOAT,
				false, vertexFloatSizeInBytes, FLOAT_BYTE_SIZE * Vertex.NORMAL_OFFSET);

		glEnableVertexAttribArray(3);
		GL20.glVertexAttribPointer(3, Vertex.TANGENT_FLOAT_COUNT, GL11.GL_FLOAT,
				false, vertexFloatSizeInBytes, FLOAT_BYTE_SIZE * Vertex.TANGENT_OFFSET);

		glEnableVertexAttribArray(4);
		GL20.glVertexAttribPointer(4, Vertex.BITANGENT_FLOAT_COUNT, GL11.GL_FLOAT,
				false, vertexFloatSizeInBytes, FLOAT_BYTE_SIZE * Vertex.BITANGENT_OFFSET);
		// unbind
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		// now do index buffer
		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();
		this.ibo = GL15.glGenBuffers();

		// bind to buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		// put data in
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		// shader stuff
		//GL20.glVertexAttribPointer(5, 3, GL11.GL_INT, false, 0, 0);
		// unbind from buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(indicesBuffer);

		// unbind
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);


	}

	public void initRender() {
		GL30.glBindVertexArray(vao);
		// enable position attribute
		glEnableVertexAttribArray(0);
		// enable texture attribute
		glEnableVertexAttribArray(1);
		// enable normal
		glEnableVertexAttribArray(2);
		// enable tangent
		glEnableVertexAttribArray(3);
		// enable bitangent
		glEnableVertexAttribArray(4);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);


	}

	public void endRender() {
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(2);
		GL30.glDisableVertexAttribArray(3);
		GL30.glDisableVertexAttribArray(4);
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
		GL30.glDeleteVertexArrays(vao);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vbo);
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public int getVertexCount() {
		return indices.length;
	}


}
