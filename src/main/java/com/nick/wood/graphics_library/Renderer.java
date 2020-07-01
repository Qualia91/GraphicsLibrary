package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.lighting.*;
import com.nick.wood.graphics_library.materials.TextureManager;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.mesh_objects.Terrain;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.TextItem;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import com.nick.wood.maths.objects.vector.Vec4f;
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

	private final TextureManager textureManager;
	private int modelViewVBO;

	private FloatBuffer modelViewBuffer;

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int VECTOR4F_SIZE_BYTES = 4 * Renderer.FLOAT_SIZE_BYTES;
	private static final int MATRIX_SIZE_BYTES = Renderer.MATRIX_SIZE_FLOATS * Renderer.FLOAT_SIZE_BYTES;

	private Matrix4f lightViewMatrix = Matrix4f.Identity;

	public Renderer(TextureManager textureManager) {
		this.textureManager = textureManager;
	}

	public void init() {
		this.modelViewVBO = glGenBuffers();
	}

	public void destroy() {
		glDeleteBuffers(modelViewVBO);
	}

	public void renderSkybox(MeshObject meshObject, Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry, Shader shader, Vec3f ambientLight) {

		shader.bind();

		meshObject.getMesh().initRender();

		shader.setUniform("ambientLight", ambientLight);
		shader.setUniform("projection", cameraInstanceObjectEntry.getKey().getProjectionMatrix());
		shader.setUniform("view", cameraInstanceObjectEntry.getValue().getTransformation().invert());

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshObject.getMesh().getIbo());

		// bind texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(meshObject.getMesh().getMaterial().getTexturePath()));

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 3;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		modelViewBuffer = MemoryUtil.memAllocFloat(MATRIX_SIZE_FLOATS);

		Matrix4f transform = Matrix4f.Transform(cameraInstanceObjectEntry.getValue().getTransformation().getTranslation(), meshObject.getMeshTransformation().getRotation().toMatrix(), meshObject.getMeshTransformation().getScale());

		for (int i = 0; i < transform.transpose().getValues().length; i++) {
			modelViewBuffer.put(i, transform.transpose().getValues()[i]);
		}

		/** for java 14
		 * modelViewBuffer.put(0, transform.transpose().getValues());
		 */


		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		glDrawElements(GL11.GL_TRIANGLES, meshObject.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

		// clean up
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		meshObject.getMesh().endRender();

		shader.unbind();

	}

	public void renderPickingScene(HashMap<MeshObject, ArrayList<InstanceObject>> meshes, Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry, Shader shader, HashMap<Integer, HashMap<Integer, UUID>> indexToUUIDMap) {
		shader.bind();

		shader.setUniform("projection", cameraInstanceObjectEntry.getKey().getProjectionMatrix());
		shader.setUniform("cameraPos", cameraInstanceObjectEntry.getValue().getTransformation().getTranslation());
		shader.setUniform("view", cameraInstanceObjectEntry.getValue().getTransformation().invert());

		int modelTypeId = 0;
		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry : meshes.entrySet()) {

			if (!indexToUUIDMap.containsKey(modelTypeId)) {
				indexToUUIDMap.put(modelTypeId, new HashMap<>());
			}

			shader.setUniform("inInstanceColourID", modelTypeId);
			renderPickingInstance(meshObjectArrayListEntry, indexToUUIDMap.get(modelTypeId));
			modelTypeId++;
		}

		shader.unbind();
	}

	private void renderPickingInstance(Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry, HashMap<Integer, UUID> integerUUIDHashMap) {

		meshObjectArrayListEntry.getKey().getMesh().initRender();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshObjectArrayListEntry.getKey().getMesh().getIbo());

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 3;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		int index = 0;

		modelViewBuffer = MemoryUtil.memAllocFloat(meshObjectArrayListEntry.getValue().size() * MATRIX_SIZE_FLOATS);
		for (InstanceObject instanceObject : meshObjectArrayListEntry.getValue()) {
			integerUUIDHashMap.put(index, instanceObject.getUuid());

			for (int i = 0; i < meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues().length; i++) {
				modelViewBuffer.put(index * 16 + i, meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues()[i]);
			}

			/** for java 14
			 * modelViewBuffer.put(index * 16, meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues());
			 */

			index++;
		}
		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, meshObjectArrayListEntry.getKey().getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0, meshObjectArrayListEntry.getValue().size());

		// clean up

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		meshObjectArrayListEntry.getKey().getMesh().endRender();

	}

	public void renderWater(HashMap<MeshObject, ArrayList<InstanceObject>> meshes,
	                        Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry,
	                        HashMap<Light, InstanceObject> lights, Shader shader,
	                        Fog fog,
	                        int reflectionTexture,
	                        int refractionTexture,
	                        float moveFactor,
	                        Vec3f ambientLight) {

		shader.bind();

		int pointLightIndex = 0;
		int spotLightIndex = 0;
		int directionalLightIndex = 0;

		for (Map.Entry<Light, InstanceObject> lightInstanceObjectEntry : lights.entrySet()) {

			Light light = lightInstanceObjectEntry.getKey();
			Matrix4f transform = lightInstanceObjectEntry.getValue().getTransformation();

			switch (light.getType()) {
				case POINT:
					createPointLight("", (PointLight) light, pointLightIndex++, transform, shader);
					break;
				case SPOT:
					createSpotLight((SpotLight) light, spotLightIndex++, transform, shader);
					break;
				case DIRECTIONAL:
					createDirectionalLight((DirectionalLight) light, directionalLightIndex++, transform, shader);
					break;
				default:
					break;
			}

		}


		shader.setUniform("ambientLight", ambientLight);
		shader.setUniform("specularPower", 0.5f);
		shader.setUniform("projection", cameraInstanceObjectEntry.getKey().getProjectionMatrix());

		shader.setUniform("cameraPos", cameraInstanceObjectEntry.getValue().getTransformation().getTranslation());
		shader.setUniform("view", cameraInstanceObjectEntry.getValue().getTransformation().invert());
		shader.setUniform("modelLightViewMatrix", lightViewMatrix);

		shader.setUniform("moveFactor", moveFactor);

		createFog(fog, shader);

		// do all text ones last as the background wont be see through properly if they dont
		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry : meshes.entrySet()) {
			renderWaterMesh(meshObjectArrayListEntry, shader, reflectionTexture, refractionTexture);
		}


		shader.unbind();

	}

	public void renderWaterMesh(Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry,
	                            Shader shader, int reflectionTexture, int refractionTexture) {
		meshObjectArrayListEntry.getKey().getMesh().initRender();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshObjectArrayListEntry.getKey().getMesh().getIbo());

		// bind texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, reflectionTexture);
		shader.setUniform("reflectionTexture", 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, refractionTexture);
		shader.setUniform("refractionTexture", 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(meshObjectArrayListEntry.getKey().getMesh().getMaterial().getTexturePath()));
		shader.setUniform("dudvmap", 2);

		// bind normal map if available
		if (meshObjectArrayListEntry.getKey().getMesh().getMaterial().hasNormalMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE3);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(meshObjectArrayListEntry.getKey().getMesh().getMaterial().getNormalMapPath()));
			shader.setUniform("normal_text_sampler", 3);
		}

		shader.setUniform("material.diffuse", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getDiffuseColour());
		shader.setUniform("material.specular", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getSpecularColour());
		shader.setUniform("material.shininess", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getShininess());
		shader.setUniform("material.reflectance", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getReflectance());
		shader.setUniform("material.hasNormalMap", meshObjectArrayListEntry.getKey().getMesh().getMaterial().hasNormalMap() ? 1 : 0);

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 3;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		int index = 0;

		modelViewBuffer = MemoryUtil.memAllocFloat(meshObjectArrayListEntry.getValue().size() * MATRIX_SIZE_FLOATS);
		for (InstanceObject instanceObject : meshObjectArrayListEntry.getValue()) {
			for (int i = 0; i < meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues().length; i++) {
				modelViewBuffer.put(index * 16 + i, meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues()[i]);
			}

			/** for java 14
			 * modelViewBuffer.put(index * 16, meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues());
			 */
			index++;
		}
		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, meshObjectArrayListEntry.getKey().getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0, meshObjectArrayListEntry.getValue().size());

		// clean up

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		meshObjectArrayListEntry.getKey().getMesh().endRender();
	}

	public void renderScene(HashMap<MeshObject, ArrayList<InstanceObject>> meshes,
	                        Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry,
	                        HashMap<Light, InstanceObject> lights,
	                        Shader shader,
	                        Vec3f ambientLight,
	                        Fog fog,
	                        Vec4f clippingPlane) {

		shader.bind();

		int pointLightIndex = 0;
		int spotLightIndex = 0;
		int directionalLightIndex = 0;

		for (Map.Entry<Light, InstanceObject> lightInstanceObjectEntry : lights.entrySet()) {

			Light light = lightInstanceObjectEntry.getKey();
			Matrix4f transform = lightInstanceObjectEntry.getValue().getTransformation();

			switch (light.getType()) {
				case POINT:
					createPointLight("", (PointLight) light, pointLightIndex++, transform, shader);
					break;
				case SPOT:
					createSpotLight((SpotLight) light, spotLightIndex++, transform, shader);
					break;
				case DIRECTIONAL:
					createDirectionalLight((DirectionalLight) light, directionalLightIndex++, transform, shader);
					break;
				default:
					break;
			}

		}

		shader.setUniform("ambientLight", ambientLight);
		shader.setUniform("specularPower", 0.5f);
		shader.setUniform("projection", cameraInstanceObjectEntry.getKey().getProjectionMatrix());

		shader.setUniform("cameraPos", cameraInstanceObjectEntry.getValue().getTransformation().getTranslation());
		shader.setUniform("view", cameraInstanceObjectEntry.getValue().getTransformation().invert());
		shader.setUniform("modelLightViewMatrix", lightViewMatrix);

		if (clippingPlane != null) {
			shader.setUniform("clippingPlane", clippingPlane);
		} else {
			shader.setUniform("clippingPlane", Vec4f.ZERO);
		}

		createFog(fog, shader);

		// do all but text
		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry : meshes.entrySet()) {
			if (!(meshObjectArrayListEntry.getKey() instanceof TextItem)) {
				renderInstance(meshObjectArrayListEntry, shader);
			}
		}
		// do all text ones last as the background wont be see through properly if they dont
		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry : meshes.entrySet()) {
			if ((meshObjectArrayListEntry.getKey() instanceof TextItem)) {
				renderInstance(meshObjectArrayListEntry, shader);
			}
		}


		shader.unbind();

	}

	private void renderInstance(Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry, Shader shader) {

		meshObjectArrayListEntry.getKey().getMesh().initRender();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshObjectArrayListEntry.getKey().getMesh().getIbo());

		// bind texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(meshObjectArrayListEntry.getKey().getMesh().getMaterial().getTexturePath()));
		shader.setUniform("tex", 0);

		// bind normal map if available
		if (meshObjectArrayListEntry.getKey().getMesh().getMaterial().hasNormalMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(meshObjectArrayListEntry.getKey().getMesh().getMaterial().getNormalMapPath()));
			shader.setUniform("normal_text_sampler", 1);
		}

		shader.setUniform("material.diffuse", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getDiffuseColour());
		shader.setUniform("material.specular", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getSpecularColour());
		shader.setUniform("material.shininess", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getShininess());
		shader.setUniform("material.reflectance", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getReflectance());
		shader.setUniform("material.hasNormalMap", meshObjectArrayListEntry.getKey().getMesh().getMaterial().hasNormalMap() ? 1 : 0);

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 3;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		int index = 0;

		modelViewBuffer = MemoryUtil.memAllocFloat(meshObjectArrayListEntry.getValue().size() * MATRIX_SIZE_FLOATS);
		for (InstanceObject instanceObject : meshObjectArrayListEntry.getValue()) {

			for (int i = 0; i < meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues().length; i++) {
				modelViewBuffer.put(index * 16 + i, meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues()[i]);
			}

			/** for java 14
			 * modelViewBuffer.put(index * 16, meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues());
			 */

			index++;
		}
		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, meshObjectArrayListEntry.getKey().getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0, meshObjectArrayListEntry.getValue().size());

		// clean up

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glDisable(GL11.GL_TEXTURE_2D);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		meshObjectArrayListEntry.getKey().getMesh().endRender();

	}

	public void renderTerrain(HashMap<MeshObject, ArrayList<InstanceObject>> meshes,
	                        Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry,
	                        HashMap<Light, InstanceObject> lights,
	                        Shader shader,
	                        Vec3f ambientLight,
	                        Fog fog,
	                        Vec4f clippingPlane) {

		shader.bind();

		int pointLightIndex = 0;
		int spotLightIndex = 0;
		int directionalLightIndex = 0;

		for (Map.Entry<Light, InstanceObject> lightInstanceObjectEntry : lights.entrySet()) {

			Light light = lightInstanceObjectEntry.getKey();
			Matrix4f transform = lightInstanceObjectEntry.getValue().getTransformation();

			switch (light.getType()) {
				case POINT:
					createPointLight("", (PointLight) light, pointLightIndex++, transform, shader);
					break;
				case SPOT:
					createSpotLight((SpotLight) light, spotLightIndex++, transform, shader);
					break;
				case DIRECTIONAL:
					createDirectionalLight((DirectionalLight) light, directionalLightIndex++, transform, shader);
					break;
				default:
					break;
			}

		}

		shader.setUniform("ambientLight", ambientLight);
		shader.setUniform("specularPower", 0.5f);
		shader.setUniform("projection", cameraInstanceObjectEntry.getKey().getProjectionMatrix());

		shader.setUniform("cameraPos", cameraInstanceObjectEntry.getValue().getTransformation().getTranslation());
		shader.setUniform("view", cameraInstanceObjectEntry.getValue().getTransformation().invert());
		shader.setUniform("modelLightViewMatrix", lightViewMatrix);

		if (clippingPlane != null) {
			shader.setUniform("clippingPlane", clippingPlane);
		} else {
			shader.setUniform("clippingPlane", Vec4f.ZERO);
		}

		createFog(fog, shader);

		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry : meshes.entrySet()) {
			renderTerrainInstance(meshObjectArrayListEntry, shader);
		}


		shader.unbind();

	}

	private void renderTerrainInstance(Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry, Shader shader) {

		meshObjectArrayListEntry.getKey().getMesh().initRender();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshObjectArrayListEntry.getKey().getMesh().getIbo());

		Terrain terrain = (Terrain) meshObjectArrayListEntry.getKey();

		for (int i = 0; i < terrain.getTerrainTextureObjects().size(); i++) {
			// bind texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(terrain.getTerrainTextureObjects().get(i).getTexturePath()));
			shader.setUniform("texture_array[" + i + "]", i);

			// bind normal
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + terrain.getTerrainTextureObjects().size() + i);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(terrain.getTerrainTextureObjects().get(i).getNormalPath()));
			shader.setUniform("normal_array[" + i + "]", terrain.getTerrainTextureObjects().size() + i);

			// bind heights
			shader.setUniform("heights[" + i + "]", terrain.getTerrainTextureObjects().get(i).getHeight());

			// bind transition width
			shader.setUniform("transitionWidths[" + i + "]", terrain.getTerrainTextureObjects().get(i).getTransitionWidth());
		}

		shader.setUniform("material.diffuse", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getDiffuseColour());
		shader.setUniform("material.specular", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getSpecularColour());
		shader.setUniform("material.shininess", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getShininess());
		shader.setUniform("material.reflectance", meshObjectArrayListEntry.getKey().getMesh().getMaterial().getReflectance());
		shader.setUniform("material.hasNormalMap", meshObjectArrayListEntry.getKey().getMesh().getMaterial().hasNormalMap() ? 1 : 0);

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 3;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		int index = 0;

		modelViewBuffer = MemoryUtil.memAllocFloat(meshObjectArrayListEntry.getValue().size() * MATRIX_SIZE_FLOATS);
		for (InstanceObject instanceObject : meshObjectArrayListEntry.getValue()) {

			for (int i = 0; i < meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues().length; i++) {
				modelViewBuffer.put(index * 16 + i, meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues()[i]);
			}

			/** for java 14
			 * modelViewBuffer.put(index * 16, meshObjectArrayListEntry.getKey().getMeshTransformation().getSRT().multiply(instanceObject.getTransformation()).transpose().getValues());
			 */

			index++;
		}
		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, meshObjectArrayListEntry.getKey().getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0, meshObjectArrayListEntry.getValue().size());

		// clean up

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glDisable(GL11.GL_TEXTURE_2D);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		meshObjectArrayListEntry.getKey().getMesh().endRender();

	}

	private void createSpotLight(SpotLight spotLight, int index, Matrix4f transformation, Shader shader) {
		createPointLight("spotLights[" + index + "].", spotLight.getPointLight(), -1, transformation, shader);
		shader.setUniform("spotLights[" + index + "].coneDirection", transformation.rotate(spotLight.getConeDirection()));
		shader.setUniform("spotLights[" + index + "].coneAngleCosine", (float) Math.cos(spotLight.getConeAngle()));
	}

	private void createDirectionalLight(DirectionalLight directionalLight, int index, Matrix4f transformation, Shader shader) {
		shader.setUniform("directionalLights[" + index + "].colour", directionalLight.getColour());
		shader.setUniform("directionalLights[" + index + "].direction", transformation.rotate(directionalLight.getDirection()));
		shader.setUniform("directionalLights[" + index + "].intensity", directionalLight.getIntensity());
	}

	private void createPointLight(String namePrefix, PointLight pointLight, int index, Matrix4f transformation, Shader shader) {
		String indexAddition = "";
		if (index != -1) {
			indexAddition = "s[" + index + "]";
		}

		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".colour", pointLight.getColour());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".position", transformation.getTranslation());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".intensity", (float) pointLight.getIntensity());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".att.constant", pointLight.getAttenuation().getConstant());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".att.linear", pointLight.getAttenuation().getLinear());
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".att.exponent", pointLight.getAttenuation().getExponent());
	}

	private void createFog(Fog fog, Shader shader) {
		shader.setUniform("fog.isactive", fog.isActive() ? 1 : 0);
		shader.setUniform("fog.colour", fog.getColour());
		shader.setUniform("fog.density", fog.getDensity());
	}

}
