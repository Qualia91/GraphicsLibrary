package com.boc_dev.graphics_library;

import com.boc_dev.graphics_library.objects.Camera;
import com.boc_dev.graphics_library.objects.DrawVisitor;
import com.boc_dev.graphics_library.objects.lighting.*;
import com.boc_dev.graphics_library.objects.managers.MaterialManager;
import com.boc_dev.graphics_library.objects.managers.MeshManager;
import com.boc_dev.graphics_library.objects.managers.ModelManager;
import com.boc_dev.graphics_library.objects.managers.TextureManager;
import com.boc_dev.graphics_library.objects.mesh_objects.InstanceMesh;
import com.boc_dev.graphics_library.objects.mesh_objects.SingleMesh;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.objects.render_scene.Pair;
import com.boc_dev.graphics_library.objects.mesh_objects.Mesh;
import com.boc_dev.graphics_library.objects.mesh_objects.Model;
import com.boc_dev.maths.objects.matrix.Matrix4f;
import com.boc_dev.maths.objects.vector.Vec3f;
import com.boc_dev.maths.objects.vector.Vec4f;
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

	public final int instanceArraySizeLimit;

	private final MaterialManager materialManager;
	private final TextureManager textureManager;
	private final MeshManager meshManager;
	private final ModelManager modelManager;

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int VECTOR4F_SIZE_BYTES = 4 * Renderer.FLOAT_SIZE_BYTES;
	private static final int MATRIX_SIZE_BYTES = Renderer.MATRIX_SIZE_FLOATS * Renderer.FLOAT_SIZE_BYTES;

	private Matrix4f lightViewMatrix = Matrix4f.Identity;

	private DrawVisitor drawVisitor;

	public Renderer(int instanceArraySizeLimit, TextureManager textureManager, MaterialManager materialManager, MeshManager meshManager, ModelManager modelManager) {
		this.instanceArraySizeLimit = instanceArraySizeLimit;
		this.textureManager = textureManager;
		this.materialManager = materialManager;
		this.meshManager = meshManager;
		this.modelManager = modelManager;
	}

	public void init() {
		this.drawVisitor = new DrawVisitor();
	}

	public void destroy() {

	}

	public void renderSkybox(Pair<String, InstanceObject> skyboxModel, Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry, Shader shader) {

		shader.bind();

		Model model = modelManager.getModel(skyboxModel.getKey());
		Mesh mesh = meshManager.getMesh(model.getMeshString());

		materialManager.getMaterial(model.getMaterialID()).initRender(textureManager, shader);

		mesh.initRender();
		shader.setUniform("projection", cameraInstanceObjectEntry.getKey().getProjectionMatrix());
		shader.setUniform("view", cameraInstanceObjectEntry.getValue().getTransformationInverse());

		Vec3f cameraPosition = cameraInstanceObjectEntry.getValue().getTransformation().getTranslation();

		Matrix4f matrix4f = skyboxModel.getValue().getTransformation().multiply(Matrix4f.Translation(cameraPosition)).transpose();

		int start = 5;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		FloatBuffer modelViewBuffer = MemoryUtil.memAllocFloat(MATRIX_SIZE_FLOATS);

		for (int i = 0; i < matrix4f.getValues().length; i++) {
			modelViewBuffer.put(i, matrix4f.getValues()[i]);
		}

		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		glDrawElements(GL11.GL_TRIANGLES, mesh.size(), GL11.GL_UNSIGNED_INT, 0);

		// clean up
		materialManager.getMaterial(model.getMaterialID()).endRender();

		mesh.endRender();

		shader.unbind();

	}

	public void renderPickingScene(HashMap<String, ArrayList<InstanceObject>> meshes, Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry, Shader shader, HashMap<Integer, HashMap<Integer, UUID>> indexToUUIDMap) {
		shader.bind();

		shader.setUniform("projection", cameraInstanceObjectEntry.getKey().getProjectionMatrix());
		shader.setUniform("cameraPos", cameraInstanceObjectEntry.getValue().getTransformation().getTranslation());
		shader.setUniform("view", cameraInstanceObjectEntry.getValue().getTransformationInverse());

		int modelTypeId = 1;
		for (Map.Entry<String, ArrayList<InstanceObject>> meshArrayListEntry : meshes.entrySet()) {

			if (!indexToUUIDMap.containsKey(modelTypeId)) {
				indexToUUIDMap.put(modelTypeId, new HashMap<>());
			}

			shader.setUniform("inInstanceColourID", modelTypeId);
			renderPickingInstance(meshArrayListEntry, indexToUUIDMap.get(modelTypeId));

			modelTypeId++;
		}

		shader.unbind();
	}

	private void renderPickingInstance(Map.Entry<String, ArrayList<InstanceObject>> meshArrayListEntry, HashMap<Integer, UUID> integerUUIDHashMap) {

		if (meshManager.getMesh(meshArrayListEntry.getKey()) instanceof InstanceMesh) {

			InstanceMesh instanceMesh = (InstanceMesh) meshManager.getMesh(meshArrayListEntry.getKey());

			instanceMesh.getSingleMesh().initRender();

//		int index = 0;
//		for (InstanceObject instanceObject : meshArrayListEntry.getValue()) {
//			integerUUIDHashMap.put(index, instanceObject.getUuid());
//			index++;
//		}

			// todo this doesnt work for some reason
			//drawVisitor.draw(singleMesh, meshArrayListEntry.getValue());

			int start = 5;
			for (int i = 0; i < 4; i++) {
				glEnableVertexAttribArray(start);
				glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
				glVertexAttribDivisor(start, 1);
				start++;
			}

			FloatBuffer modelViewBuffer = MemoryUtil.memAllocFloat(meshArrayListEntry.getValue().size() * MATRIX_SIZE_FLOATS);

			float[] transformArray = new float[meshArrayListEntry.getValue().size() * MATRIX_SIZE_FLOATS];

			int index = 0;
			for (InstanceObject instanceObject : meshArrayListEntry.getValue()) {
				integerUUIDHashMap.put(index, instanceObject.getUuid());

				for (int transformIndex = 0; transformIndex < instanceObject.getTransformation().getValues().length; transformIndex++) {
					transformArray[(index * MATRIX_SIZE_FLOATS) + transformIndex] =
							instanceObject.getTransformation().getValues()[transformIndex];
				}
				index++;
			}
			for (int i = 0; i < transformArray.length; i++) {
				modelViewBuffer.put(i, transformArray[i]);
			}
			glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

			GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, instanceMesh.size(), GL11.GL_UNSIGNED_INT, 0, meshArrayListEntry.getValue().size());

			MemoryUtil.memFree(modelViewBuffer);

			instanceMesh.endRender();
		} else {
			SingleMesh singleMesh = (SingleMesh) meshManager.getMesh(meshArrayListEntry.getKey());

			singleMesh.initRender();

//		int index = 0;
//		for (InstanceObject instanceObject : meshArrayListEntry.getValue()) {
//			integerUUIDHashMap.put(index, instanceObject.getUuid());
//			index++;
//		}

			// todo this doesnt work for some reason
			//drawVisitor.draw(singleMesh, meshArrayListEntry.getValue());

			int start = 5;
			for (int i = 0; i < 4; i++) {
				glEnableVertexAttribArray(start);
				glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
				glVertexAttribDivisor(start, 1);
				start++;
			}

			FloatBuffer modelViewBuffer = MemoryUtil.memAllocFloat(meshArrayListEntry.getValue().size() * MATRIX_SIZE_FLOATS);

			float[] transformArray = new float[meshArrayListEntry.getValue().size() * MATRIX_SIZE_FLOATS];

			int index = 0;
			for (InstanceObject instanceObject : meshArrayListEntry.getValue()) {
				integerUUIDHashMap.put(index, instanceObject.getUuid());

				for (int transformIndex = 0; transformIndex < instanceObject.getTransformation().getValues().length; transformIndex++) {
					transformArray[(index * MATRIX_SIZE_FLOATS) + transformIndex] =
							instanceObject.getTransformation().getValues()[transformIndex];
				}
				index++;
			}
			for (int i = 0; i < transformArray.length; i++) {
				modelViewBuffer.put(i, transformArray[i]);
			}
			glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

			GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, singleMesh.size(), GL11.GL_UNSIGNED_INT, 0, meshArrayListEntry.getValue().size());

			MemoryUtil.memFree(modelViewBuffer);

			singleMesh.endRender();
		}

	}

	public void renderWater(HashMap<String, InstanceObject> models,
	                        Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry,
	                        HashMap<Light, InstanceObject> lights, Shader shader,
	                        Fog fog,
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

		createBasic(shader, ambientLight, 0.5f, cameraInstanceObjectEntry.getKey(), cameraInstanceObjectEntry.getValue());

		shader.setUniform("moveFactor", moveFactor);

		createFog(fog, shader);

		for (Map.Entry<String, InstanceObject> modelArrayListEntry : models.entrySet()) {
			renderWaterMesh(modelArrayListEntry, shader);
		}


		shader.unbind();

	}

	public void renderWaterMesh(Map.Entry<String, InstanceObject> modelArrayListEntry,
	                            Shader shader) {

		Model model = modelManager.getModel(modelArrayListEntry.getKey());
		Mesh singleMesh = meshManager.getMesh(model.getMeshString());

		singleMesh.initRender();

		materialManager.getMaterial(model.getMaterialID()).initRender(textureManager, shader);

		int start = 5;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		FloatBuffer modelViewBuffer = MemoryUtil.memAllocFloat(MATRIX_SIZE_FLOATS);
		for (int i = 0; i < modelArrayListEntry.getValue().getTransformation().getValues().length; i++) {
			modelViewBuffer.put(i, modelArrayListEntry.getValue().getTransformation().getValues()[i]);
		}

		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		GL31.glDrawElements(GL11.GL_TRIANGLES, singleMesh.size(), GL11.GL_UNSIGNED_INT, 0);

		// clean up

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		singleMesh.endRender();
	}

	public void renderScene(HashMap<String, ArrayList<InstanceObject>> meshes,
	                        HashMap<String, ArrayList<InstanceObject>> textMeshes,
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

		createBasic(shader, ambientLight, 0.5f, cameraInstanceObjectEntry.getKey(), cameraInstanceObjectEntry.getValue());

		shader.setUniform("clippingPlane", Objects.requireNonNullElse(clippingPlane, Vec4f.ZERO));

		createFog(fog, shader);

		for (Map.Entry<String, ArrayList<InstanceObject>> meshArrayListEntry : meshes.entrySet()) {
			renderInstance(meshArrayListEntry, shader);
		}

		// do text after so alpha works properly
		for (Map.Entry<String, ArrayList<InstanceObject>> meshArrayListEntry : textMeshes.entrySet()) {
			renderInstance(meshArrayListEntry, shader);
		}


		shader.unbind();

	}

	private void renderInstance(Map.Entry<String, ArrayList<InstanceObject>> modelArrayListEntry, Shader shader) {

		Model model = modelManager.getModel(modelArrayListEntry.getKey());
		Mesh mesh = meshManager.getMesh(model.getMeshString());

		mesh.initRender();

		materialManager.getMaterial(model.getMaterialID()).initRender(textureManager, shader);

		mesh.draw(drawVisitor, modelArrayListEntry.getValue());

		materialManager.getMaterial(model.getMaterialID()).endRender();

		mesh.endRender();

	}

//	public void renderTerrain(HashMap<String, InstanceObject> terrainModels,
//	                          Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry,
//	                          HashMap<Light, InstanceObject> lights,
//	                          Shader shader,
//	                          Vec3f ambientLight,
//	                          Fog fog,
//	                          Vec4f clippingPlane) {
//
//		shader.bind();
//
//		int pointLightIndex = 0;
//		int spotLightIndex = 0;
//		int directionalLightIndex = 0;
//
//		for (Map.Entry<Light, InstanceObject> lightInstanceObjectEntry : lights.entrySet()) {
//
//			Light light = lightInstanceObjectEntry.getKey();
//			Matrix4f transform = lightInstanceObjectEntry.getValue().getTransformation();
//
//			switch (light.getType()) {
//				case POINT:
//					createPointLight("", (PointLight) light, pointLightIndex++, transform, shader);
//					break;
//				case SPOT:
//					createSpotLight((SpotLight) light, spotLightIndex++, transform, shader);
//					break;
//				case DIRECTIONAL:
//					createDirectionalLight((DirectionalLight) light, directionalLightIndex++, transform, shader);
//					break;
//				default:
//					break;
//			}
//
//		}
//
//		createBasic(shader, ambientLight, 0.5f, cameraInstanceObjectEntry.getKey(), cameraInstanceObjectEntry.getValue());
//
//		if (clippingPlane != null) {
//			shader.setUniform("clippingPlane", clippingPlane);
//		} else {
//			shader.setUniform("clippingPlane", Vec4f.ZERO);
//		}
//
//		createFog(fog, shader);
//
//		for (Map.Entry<String, InstanceObject> modelInstanceObjectEntry : terrainModels.entrySet()) {
//
//			renderTerrainInstance(modelInstanceObjectEntry, shader);
//
//		}
//
//		shader.unbind();
//
//	}
//
//	private void renderTerrainInstance(Map.Entry<String, InstanceObject> terrainModel, Shader shader) {
//
//		Model model = modelManager.getModel(terrainModel.getKey());
//		Mesh singleMesh = meshManager.getMesh(model.getMeshString());
//
//		singleMesh.initRender();
//
//		materialManager.getMaterial(model.getMaterialID()).initRender(textureManager, shader);
//
//		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
//		int start = 3;
//		for (int i = 0; i < 4; i++) {
//			glEnableVertexAttribArray(start);
//			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
//			glVertexAttribDivisor(start, 1);
//			start++;
//		}
//
//		FloatBuffer modelViewBuffer = MemoryUtil.memAllocFloat(MATRIX_SIZE_FLOATS);
//		for (int i = 0; i < Matrix4f.Identity.getValues().length; i++) {
//			modelViewBuffer.put(i, terrainModel.getValue().getTransformation().getValues()[i]);
//		}
//
//		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);
//
//		MemoryUtil.memFree(modelViewBuffer);
//
//		glDrawElements(GL11.GL_TRIANGLES, singleMesh.size(), GL11.GL_UNSIGNED_INT, 0);
//
//		// clean up
//
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
//		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
//		GL13.glDisable(GL11.GL_TEXTURE_2D);
//		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
//
//		singleMesh.endRender();
//
//	}

	private void createBasic(Shader shader, Vec3f ambientLight, float specularPower, Camera camera, InstanceObject cameraInstance) {
		shader.setUniform("ambientLight", ambientLight);
		shader.setUniform("specularPower", specularPower); // 0.5f
		shader.setUniform("projection", camera.getProjectionMatrix());

		shader.setUniform("cameraPos", cameraInstance.getTransformation().getTranslation());
		shader.setUniform("view", cameraInstance.getTransformationInverse());
		shader.setUniform("modelLightViewMatrix", lightViewMatrix);

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
		shader.setUniform(namePrefix + "pointLight" + indexAddition + ".intensity", pointLight.getIntensity());
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
