package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.game_objects.GameObject;
import com.nick.wood.graphics_library.lighting.DirectionalLight;
import com.nick.wood.graphics_library.lighting.PointLight;
import com.nick.wood.graphics_library.lighting.SpotLight;
import com.nick.wood.graphics_library.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;
import com.nick.wood.maths.objects.Vec3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

public class Renderer {

	private final Shader shader;
	private final Matrix4d projectionMatrix;
	private final Matrix4d worldRotation;
	private final Matrix4d worldRotationLight;
	private final ArrayList<PointLight> pointLights = new ArrayList<>();
	private final ArrayList<DirectionalLight> directionalLights = new ArrayList<>();
	private final ArrayList<SpotLight> spotLights = new ArrayList<>();

	public Renderer(Window window) {
		this.shader = window.getShader();
		this.projectionMatrix = window.getProjectionMatrix();

		// todo this obs needs to go somewhere else
		this.pointLights.add(new PointLight(
				new Vec3d(0.0, 1.0, 0.0),
				new Vec3d(0.0, 0.0, -10),
				1f));

		this.directionalLights.add(new DirectionalLight(
				new Vec3d(0.0, 0.0, 1.0),
				new Vec3d(0.0, 0.0, 10.0),
				1f));

		this.spotLights.add(new SpotLight(
				new PointLight(
						new Vec3d(1.0, 0.0, 0.0),
						new Vec3d(0.0, -10.0, 0.0),
						10f),
				Vec3f.Y,
				0.05f
		));

		this.worldRotation = Matrix4d.Rotation(-90.0, Vec3d.X);
		// todo need matrix inverse function
		this.worldRotationLight = Matrix4d.Rotation(90.0, Vec3d.X);
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
			// enable normals
			GL30.glEnableVertexAttribArray(3);

			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshObject.getMesh().getIbo());

			// bind texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, meshObject.getMesh().getMaterial().getTextureId());

			shader.bind();

			// uniform is transformation matrix
			Matrix4d transform = worldRotation.multiply(meshObject.getModelTransform().getTransform()).multiply(Matrix4d.Transform(gameObject.getPosition(), gameObject.getRotation(), gameObject.getScale()));
			shader.setUniform("model", transform);
			shader.setUniform("projection", projectionMatrix);
			shader.setUniform("view", camera.getView());
			shader.setUniform("ambientLight", new Vec3d(0.1, 0.1, 0.1));
			shader.setUniform("specularPower", 0.5f);
			shader.setUniform("cameraPos", worldRotationLight.multiply(camera.getPos()));

			shader.setUniform("material.diffuse", meshObject.getMesh().getMaterial().getDiffuseColour());
			shader.setUniform("material.specular", meshObject.getMesh().getMaterial().getSpecularColour());
			shader.setUniform("material.shininess", meshObject.getMesh().getMaterial().getShininess());
			shader.setUniform("material.reflectance", meshObject.getMesh().getMaterial().getReflectance());

			for (int i = 0; i < pointLights.size(); i++) {
				createPointLight("", pointLights.get(i), i);
			}
			for (int i = 0; i < directionalLights.size(); i++) {
				createDirectionalLight(directionalLights.get(i), i);
			}
			for (int i = 0; i < spotLights.size(); i++) {
				createSpotLight(spotLights.get(i), i);
			}

			GL11.glDrawElements(GL11.GL_TRIANGLES, meshObject.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

			shader.unbind();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

			GL30.glDisableVertexAttribArray(3);
			GL30.glDisableVertexAttribArray(2);
			GL30.glDisableVertexAttribArray(1);
			GL30.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
		}
	}

	private void createSpotLight(SpotLight spotLight, int index) {
		createPointLight("spotLights[" + index + "].", spotLight.getPointLight(), -1);
		shader.setUniform("spotLights[" + index + "].coneDirection", this.worldRotationLight.multiply(spotLight.getConeDirection().toVec3d()).toVec3f());
		shader.setUniform("spotLights[" + index + "].coneAngleCosine", (float) Math.cos(spotLight.getConeAngle()));
	}

	private void createDirectionalLight(DirectionalLight directionalLight, int index) {
		shader.setUniform("directionalLights[" + index + "].colour", directionalLight.getColour());
		shader.setUniform("directionalLights[" + index + "].direction", worldRotationLight.multiply(directionalLight.getDirection()));
		shader.setUniform("directionalLights[" + index + "].intensity",directionalLight.getIntensity());
	}

	private void createPointLight(String namePrefix, PointLight pointLight, int index) {
		String indexAddition = "";
		if (index != -1) {
			indexAddition = "s[" + index + "]";
		}
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".colour", pointLight.getColour());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".position", worldRotationLight.multiply(pointLight.getPosition()));
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".intensity", (float) pointLight.getIntensity());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".att.constant", pointLight.getAttenuation().getConstant());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".att.linear", pointLight.getAttenuation().getLinear());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".att.exponent", pointLight.getAttenuation().getExponent());
	}
}
