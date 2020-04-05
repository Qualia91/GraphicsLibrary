package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.mesh_objects.Camera;
import com.nick.wood.graphics_library.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.Matrix4d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class Renderer {

	private final Shader shader;
	private final Matrix4d projectionMatrix;

	public Renderer(Window window) {
		this.shader = window.getShader();
		this.projectionMatrix = window.getProjectionMatrix();
	}

	public void renderMesh(GameObject gameObject, Camera camera) {

		for (MeshObject meshObject : gameObject.getMeshGroup().getMeshObjectArray()) {

			GL30.glBindVertexArray(meshObject.getMesh().getVao());
			// enable position attribute
			GL30.glEnableVertexAttribArray(0);
			// enable colour attribute
			GL30.glEnableVertexAttribArray(1);
			// enable texture
			GL30.glEnableVertexAttribArray(2);

			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshObject.getMesh().getIbo());

			// bind texture
			GL30.glEnableVertexAttribArray(2);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, meshObject.getMesh().getMaterial().getTextureId());
			shader.bind();

			// uniform is transformation matrix
			Matrix4d transform = meshObject.getTransformation().multiply(Matrix4d.Transform(gameObject.getPosition(), gameObject.getRotation(), gameObject.getScale()));
			shader.setUniform("model", transform);
			shader.setUniform("projection", projectionMatrix);
			shader.setUniform("view", camera.getView());

			GL11.glDrawElements(GL11.GL_TRIANGLES, meshObject.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

			shader.unbind();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

			GL30.glDisableVertexAttribArray(2);
			GL30.glDisableVertexAttribArray(1);
			GL30.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
		}
	}
}
