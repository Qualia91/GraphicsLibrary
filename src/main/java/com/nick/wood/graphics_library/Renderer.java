package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.lighting.DirectionalLight;
import com.nick.wood.graphics_library.lighting.PointLight;
import com.nick.wood.graphics_library.lighting.SpotLight;
import com.nick.wood.graphics_library.objects.game_objects.RenderObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Matrix4f;
import com.nick.wood.maths.objects.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class Renderer {

	private final static int MAX_INSTANCE = 1000;

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int VECTOR4F_SIZE_BYTES = 4 * Renderer.FLOAT_SIZE_BYTES;
	private static final int MATRIX_SIZE_BYTES = Renderer.MATRIX_SIZE_FLOATS * Renderer.FLOAT_SIZE_BYTES;

	private final Shader shader;
	private final Matrix4f projectionMatrix;


	public Renderer(Window window) {
		this.shader = window.getShader();
		this.projectionMatrix = window.getProjectionMatrix();
	}

	private void addToInstance(HashMap<String, HashCodeCounter> meshedMeshFiles, Map.Entry<UUID, RenderObject<MeshObject>> meshObjectEntry, String appendString) {
		if (!meshedMeshFiles.containsKey(meshObjectEntry.getValue().getObject().getStringToCompare() + appendString)) {
			meshedMeshFiles.put(meshObjectEntry.getValue().getObject().getStringToCompare() + appendString, new HashCodeCounter(meshObjectEntry.getValue().getObject().getStringToCompare() + appendString, meshObjectEntry.getValue().getObject(), meshObjectEntry.getValue().getObject().getRotationOfModel(), meshObjectEntry.getValue().getTransform()));
		} else {
			if (meshedMeshFiles.get(meshObjectEntry.getValue().getObject().getStringToCompare() + appendString).getAmount() <= MAX_INSTANCE) {
				meshedMeshFiles.get(meshObjectEntry.getValue().getObject().getStringToCompare() + appendString).addInstance(meshObjectEntry.getValue().getTransform());
			} else {
				addToInstance(meshedMeshFiles, meshObjectEntry, appendString + "1");
			}
		}
	}

	public void renderMesh(WeakHashMap<UUID, RenderObject<MeshObject>> meshObjects, WeakHashMap<UUID, RenderObject<Camera>> cameras, WeakHashMap<UUID, RenderObject<Light>> lights) {

		shader.bind();

		int pointLightIndex = 0;
		int spotLightIndex = 0;
		int directionalLightIndex = 0;

		for (Map.Entry<UUID, RenderObject<Light>> lightRenderObj : lights.entrySet()) {

			switch (lightRenderObj.getValue().getObject().getType()) {
				case POINT:
					createPointLight("", (PointLight) lightRenderObj.getValue().getObject(), pointLightIndex++, lightRenderObj.getValue().getTransform());
					break;
				case SPOT:
					createSpotLight((SpotLight) lightRenderObj.getValue().getObject(), spotLightIndex++, lightRenderObj.getValue().getTransform());
					break;
				case DIRECTIONAL:
					createDirectionalLight((DirectionalLight) lightRenderObj.getValue().getObject(), directionalLightIndex++, lightRenderObj.getValue().getTransform());
					break;
				default:
					break;
			}

		}

		// get a lit of meshes via hash code of each type which depends on input mesh file and material
		HashMap<String, HashCodeCounter> meshedMeshFiles = new HashMap<>();

		for (Map.Entry<UUID, RenderObject<MeshObject>> meshObjectEntry : meshObjects.entrySet()) {
			addToInstance(meshedMeshFiles, meshObjectEntry, "");
		}

		shader.setUniform("ambientLight", new Vec3d(0.1, 0.1, 0.1));
		shader.setUniform("specularPower", 0.5f);
		shader.setUniform("projection", projectionMatrix);

		// for now just use camera one
		for (Map.Entry<UUID, RenderObject<Camera>> uuidRenderObjectCameraEntry : cameras.entrySet()) {

			shader.setUniform("view", uuidRenderObjectCameraEntry.getValue().getObject().getView());
			shader.setUniform("cameraPos", uuidRenderObjectCameraEntry.getValue().getObject().getPos());

			for (Map.Entry<String, HashCodeCounter> stringHashCodeCounterEntry : meshedMeshFiles.entrySet()) {

				HashCodeCounter meshHashCode = stringHashCodeCounterEntry.getValue();


					meshHashCode.getMeshObject().getMesh().initRender();
					GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshHashCode.getMeshObject().getMesh().getIbo());

					// bind texture
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL13.glBindTexture(GL11.GL_TEXTURE_2D, meshHashCode.getMeshObject().getMesh().getMaterial().getTextureId());

					shader.setUniform("material.diffuse", meshHashCode.getMeshObject().getMesh().getMaterial().getDiffuseColour());
					shader.setUniform("material.specular", meshHashCode.getMeshObject().getMesh().getMaterial().getSpecularColour());
					shader.setUniform("material.shininess", meshHashCode.getMeshObject().getMesh().getMaterial().getShininess());
					shader.setUniform("material.reflectance", meshHashCode.getMeshObject().getMesh().getMaterial().getReflectance());

					int modelViewVBO = glGenBuffers();
					glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
					int start = 3;
					for (int i = 0; i < 4; i++) {
						glEnableVertexAttribArray(start);
						glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
						glVertexAttribDivisor(start, 1);
						start++;
					}

					FloatBuffer modelViewBuffer = MemoryUtil.memAllocFloat(meshHashCode.getAmount() * MATRIX_SIZE_FLOATS);
					int index = 0;
					for (Matrix4d transform : meshHashCode.getTransforms()) {
						modelViewBuffer.put(index * 16, meshHashCode.getRotationOfModel().multiply(transform).transpose().getValuesF());
						index++;
					}
					glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
					glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);
					GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, meshHashCode.getMeshObject().getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0, meshHashCode.getAmount());

				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

				meshHashCode.getMeshObject().getMesh().endRender();

			}

			break;
		}

		shader.unbind();
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
