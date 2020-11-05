package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.objects.mesh_objects.InstanceMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.InterleavedMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.SingleMesh;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class DrawVisitor {

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int VECTOR4F_SIZE_BYTES = 4 * FLOAT_SIZE_BYTES;
	private static final int MATRIX_SIZE_BYTES = MATRIX_SIZE_FLOATS * FLOAT_SIZE_BYTES;

	private final int modelViewVBO;
	private FloatBuffer modelViewBuffer;

	public DrawVisitor(int modelViewVBO) {
		this.modelViewVBO = modelViewVBO;
	}

	public void draw(InstanceMesh instanceMesh, ArrayList<InstanceObject> instanceArray) {
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, instanceMesh.size(), GL11.GL_UNSIGNED_INT, 0, instanceArray.size());
	}

	public void draw(SingleMesh singleMesh, ArrayList<InstanceObject> instanceArray) {
		for (InstanceObject instanceObject : instanceArray) {

			glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
			int start = 5;
			for (int i = 0; i < 4; i++) {
				glEnableVertexAttribArray(start);
				glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
				glVertexAttribDivisor(start, 1);
				start++;
			}

			modelViewBuffer = MemoryUtil.memAllocFloat(MATRIX_SIZE_FLOATS);

			for (int i = 0; i < instanceObject.getTransformation().getValues().length; i++) {
				modelViewBuffer.put(i, instanceObject.getTransformation().getValues()[i]);
			}

			glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

			MemoryUtil.memFree(modelViewBuffer);

			glDrawElements(GL11.GL_TRIANGLES, singleMesh.size(), GL11.GL_UNSIGNED_INT, 0);

		}
	}

	public void draw(InterleavedMesh interleavedMesh, ArrayList<InstanceObject> instanceArray) {

		for (InstanceObject instanceObject : instanceArray) {

			glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
			int start = 5;
			for (int i = 0; i < 4; i++) {
				glEnableVertexAttribArray(start);
				glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
				glVertexAttribDivisor(start, 1);
				start++;
			}

			modelViewBuffer = MemoryUtil.memAllocFloat(MATRIX_SIZE_FLOATS);

			for (int i = 0; i < instanceObject.getTransformation().getValues().length; i++) {
				modelViewBuffer.put(i, instanceObject.getTransformation().getValues()[i]);
			}

			glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

			MemoryUtil.memFree(modelViewBuffer);

			glDrawElements(GL11.GL_TRIANGLES, interleavedMesh.size(), GL11.GL_UNSIGNED_INT, 0);

		}
	}
}
