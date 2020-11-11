package com.boc_dev.graphics_library.objects.mesh_objects;

import com.boc_dev.graphics_library.objects.DrawVisitor;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.objects.mesh_objects.renderer_objects.OpenGlMesh;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class InstanceMesh implements Mesh {

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int VECTOR4F_SIZE_BYTES = 4 * FLOAT_SIZE_BYTES;
	private static final int MATRIX_SIZE_BYTES = MATRIX_SIZE_FLOATS * FLOAT_SIZE_BYTES;

	private final SingleMesh mesh;
	private FloatBuffer modelViewBuffer;
	private int modelViewVBO;
	private ArrayList<InstanceObject> instanceObjects;

	public InstanceMesh(Mesh mesh) {
		this.mesh = (SingleMesh) mesh;
	}

	public void createTransformArray() {

		glBindVertexArray(((OpenGlMesh) mesh.getRendererObject()).getVao());

		modelViewVBO = glGenBuffers();

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);

		int start = 5;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

	}

	public FloatBuffer getModelViewBuffer() {
		return modelViewBuffer;
	}

	public void draw(DrawVisitor drawVisitor, ArrayList<InstanceObject> instanceObjects) {

		this.instanceObjects = instanceObjects;
		float[] transformArray = new float[instanceObjects.size() * MATRIX_SIZE_FLOATS];

		for (int instanceIndex = 0; instanceIndex < instanceObjects.size(); instanceIndex++) {

			for (int transformIndex = 0; transformIndex < instanceObjects.get(instanceIndex).getTransformation().getValues().length; transformIndex++) {
				transformArray[(instanceIndex * MATRIX_SIZE_FLOATS) + transformIndex] =
						instanceObjects.get(instanceIndex).getTransformation().getValues()[transformIndex];
			}
		}

		MemoryUtil.memFree(modelViewBuffer);
		this.modelViewBuffer = MemoryUtil.memAllocFloat(transformArray.length);

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);

		for (int i = 0; i < transformArray.length; i++) {
			modelViewBuffer.put(i, transformArray[i]);
		}

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		drawVisitor.draw(this, instanceObjects);


	}

	@Override
	public int size() {
		return mesh.size();
	}

	@Override
	public MeshType getType() {
		return MeshType.INSTANCED;
	}

	@Override
	public void create() {

	}

	public void destroyInstancing() {
		glDeleteBuffers(modelViewVBO);
		MemoryUtil.memFree(modelViewBuffer);
	}

	@Override
	public void destroy() {
		mesh.destroy();

		glDeleteBuffers(modelViewVBO);

		MemoryUtil.memFree(modelViewBuffer);
	}

	@Override
	public void initRender() {
		mesh.initRender();

		int start = 6;
		int numElements = 4 * 2;
		for (int i = 0; i < numElements; i++) {
			glEnableVertexAttribArray(start + i);
		}

	}

	@Override
	public void endRender() {

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		mesh.endRender();
	}

	public Mesh getSingleMesh() {
		return mesh;
	}
}
