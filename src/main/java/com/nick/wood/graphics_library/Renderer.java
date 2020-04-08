package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.game_objects.GameObject;
import com.nick.wood.graphics_library.lighting.DirectionalLight;
import com.nick.wood.graphics_library.lighting.PointLight;
import com.nick.wood.graphics_library.lighting.SpotLight;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshGroup;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Renderer {

	private final Shader shader;
	private final Matrix4d projectionMatrix;
	private final Matrix4d worldRotation;

	public Renderer(Window window) {
		this.shader = window.getShader();
		this.projectionMatrix = window.getProjectionMatrix();
		this.worldRotation = Matrix4d.Rotation(0.0, Vec3d.Y);
	}



	public void renderMesh(HashMap<MeshGroup, ArrayList<Matrix4d>> meshGroupArrayListEntry, HashMap<Camera, ArrayList<Matrix4d>> cameras, HashMap<Light, ArrayList<Matrix4d>> lights) {

		// for now just use camera one
		for (Map.Entry<Camera, ArrayList<Matrix4d>> cameraArrayListEntry : cameras.entrySet()) {

			Camera camera = cameraArrayListEntry.getKey();
			Matrix4d cameraTransform = cameraArrayListEntry.getValue().get(0);

			for (Map.Entry<MeshGroup, ArrayList<Matrix4d>> groupArrayListEntry : meshGroupArrayListEntry.entrySet()) {

				ArrayList<Matrix4d> matrix4ds = groupArrayListEntry.getValue();
				MeshGroup meshGroup = groupArrayListEntry.getKey();

				for (Matrix4d meshGroupTransform : matrix4ds) {

					for (MeshObject meshObject : meshGroup.getMeshObjectArray()) {

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

						shader.setUniform("model", meshGroupTransform);
						shader.setUniform("projection", projectionMatrix);
						shader.setUniform("view", camera.getView());
						shader.setUniform("ambientLight", new Vec3d(0.1, 0.1, 0.1));
						shader.setUniform("specularPower", 0.5f);
						shader.setUniform("cameraPos", camera.getPos());

						shader.setUniform("material.diffuse", meshObject.getMesh().getMaterial().getDiffuseColour());
						shader.setUniform("material.specular", meshObject.getMesh().getMaterial().getSpecularColour());
						shader.setUniform("material.shininess", meshObject.getMesh().getMaterial().getShininess());
						shader.setUniform("material.reflectance", meshObject.getMesh().getMaterial().getReflectance());

						int pointLightIndex = 0;
						int spotLightIndex = 0;
						int directionalLightIndex = 0;

						for (Map.Entry<Light, ArrayList<Matrix4d>> lightArrayListEntry : lights.entrySet()) {

							Light light = lightArrayListEntry.getKey();

							for (Matrix4d lightTransformation : lightArrayListEntry.getValue()) {

								//lightTransformation = lightTransformation.multiply(worldRotation);

								switch (light.getType()) {
									case POINT:
										createPointLight("", (PointLight) light, pointLightIndex++, lightTransformation);
										break;
									case SPOT:
										createSpotLight((SpotLight) light, spotLightIndex++, lightTransformation);
										break;
									case DIRECTIONAL:
										createDirectionalLight((DirectionalLight) light, directionalLightIndex++, lightTransformation);
										break;
									default:
										break;
								}
							}

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
			}

			break;
		}
	}

	private void createSpotLight(SpotLight spotLight, int index, Matrix4d transformation) {
		createPointLight("spotLights[" + index + "].", spotLight.getPointLight(), -1, transformation);
		shader.setUniform("spotLights[" + index + "].coneDirection", transformation.rotate(spotLight.getConeDirection().toVec3d()));
		shader.setUniform("spotLights[" + index + "].coneAngleCosine", (float) Math.cos(spotLight.getConeAngle()));
	}

	private void createDirectionalLight(DirectionalLight directionalLight, int index, Matrix4d transformation) {
		shader.setUniform("directionalLights[" + index + "].colour", directionalLight.getColour());
		shader.setUniform("directionalLights[" + index + "].direction", transformation.rotate(directionalLight.getDirection()));
		shader.setUniform("directionalLights[" + index + "].intensity", directionalLight.getIntensity());
	}

	private void createPointLight(String namePrefix, PointLight pointLight, int index, Matrix4d transformation) {
		String indexAddition = "";
		if (index != -1) {
			indexAddition = "s[" + index + "]";
		}

		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".colour", pointLight.getColour());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".position", transformation.multiply(Vec3d.ZERO));
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".intensity", (float) pointLight.getIntensity());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".att.constant", pointLight.getAttenuation().getConstant());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".att.linear", pointLight.getAttenuation().getLinear());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".att.exponent", pointLight.getAttenuation().getExponent());
	}
}
