package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.objects.DrawVisitor;
import com.nick.wood.graphics_library.objects.lighting.*;
import com.nick.wood.graphics_library.objects.managers.MaterialManager;
import com.nick.wood.graphics_library.objects.managers.TextureManager;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.managers.MeshManager;
import com.nick.wood.graphics_library.objects.managers.ModelManager;
import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.graphics_library.objects.mesh_objects.Model;
import com.nick.wood.graphics_library.objects.mesh_objects.Vertex;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.Pair;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import com.nick.wood.maths.objects.vector.Vec4f;
import org.lwjgl.BufferUtils;
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

	public static final int INSTANCE_ARRAY_SIZE_LIMIT = 10000;
	private final MaterialManager materialManager;
	private final TextureManager textureManager;
	private final MeshManager meshManager;
	private final ModelManager modelManager;
	private int modelViewVBO;

	private FloatBuffer modelViewBuffer;

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int VECTOR4F_SIZE_BYTES = 4 * Renderer.FLOAT_SIZE_BYTES;
	private static final int MATRIX_SIZE_BYTES = Renderer.MATRIX_SIZE_FLOATS * Renderer.FLOAT_SIZE_BYTES;

	private Matrix4f lightViewMatrix = Matrix4f.Identity;

	private DrawVisitor drawVisitor;
	private int colourVBO;

	public Renderer(TextureManager textureManager, MaterialManager materialManager, MeshManager meshManager, ModelManager modelManager) {
		this.textureManager = textureManager;
		this.materialManager = materialManager;
		this.meshManager = meshManager;
		this.modelManager = modelManager;
	}

	public void init() {
		this.modelViewVBO = glGenBuffers();
		this.drawVisitor = new DrawVisitor(modelViewVBO);
	}

	public void destroy() {
		glDeleteBuffers(modelViewVBO);
	}

	public void renderSkybox(Pair<String, InstanceObject> skyboxModel, Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry, Shader shader, Vec3f ambientLight) {

		shader.bind();

		Model model = modelManager.getModel(skyboxModel.getKey());
		Mesh mesh = meshManager.getMesh(model.getMeshString());

		mesh.initRender();
		shader.setUniform("projection", cameraInstanceObjectEntry.getKey().getProjectionMatrix());
		shader.setUniform("view", cameraInstanceObjectEntry.getValue().getTransformationInverse());

		materialManager.getMaterial(model.getMaterialID()).initRender(textureManager, shader);

		Vec3f cameraPosition = cameraInstanceObjectEntry.getValue().getTransformation().getTranslation();

		Matrix4f matrix4f = skyboxModel.getValue().getTransformation().multiply(Matrix4f.Translation(cameraPosition)).transpose();

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 6;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		modelViewBuffer = MemoryUtil.memAllocFloat(MATRIX_SIZE_FLOATS);

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

	public void renderPickingScene(HashMap<String, ArrayList<InstanceObject>> models, Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry, Shader shader, HashMap<Integer, HashMap<Integer, UUID>> indexToUUIDMap) {
		shader.bind();

		shader.setUniform("projection", cameraInstanceObjectEntry.getKey().getProjectionMatrix());
		shader.setUniform("cameraPos", cameraInstanceObjectEntry.getValue().getTransformation().getTranslation());
		shader.setUniform("view", cameraInstanceObjectEntry.getValue().getTransformation().invert());

		int modelTypeId = 1;
		for (Map.Entry<String, ArrayList<InstanceObject>> modelArrayListEntry : models.entrySet()) {

			if (!indexToUUIDMap.containsKey(modelTypeId)) {
				indexToUUIDMap.put(modelTypeId, new HashMap<>());
			}

			shader.setUniform("inInstanceColourID", modelTypeId);
			renderPickingInstance(modelArrayListEntry, indexToUUIDMap.get(modelTypeId));
			modelTypeId++;
		}

		shader.unbind();
	}

	private void renderPickingInstance(Map.Entry<String, ArrayList<InstanceObject>> modelArrayListEntry, HashMap<Integer, UUID> integerUUIDHashMap) {

		Model model = modelManager.getModel(modelArrayListEntry.getKey());
		Mesh singleMesh = meshManager.getMesh(model.getMeshString());

		singleMesh.initRender();

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 3;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		int index = 0;

		modelViewBuffer = MemoryUtil.memAllocFloat(modelArrayListEntry.getValue().size() * MATRIX_SIZE_FLOATS);
		for (InstanceObject instanceObject : modelArrayListEntry.getValue()) {
			integerUUIDHashMap.put(index, instanceObject.getUuid());

			for (int i = 0; i < instanceObject.getTransformation().getValues().length; i++) {
				modelViewBuffer.put(index * 16 + i, instanceObject.getTransformation().getValues()[i]);
			}

			index++;
		}
		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, singleMesh.size(), GL11.GL_UNSIGNED_INT, 0, modelArrayListEntry.getValue().size());

		// clean up

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		singleMesh.endRender();

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

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 3;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		modelViewBuffer = MemoryUtil.memAllocFloat(MATRIX_SIZE_FLOATS);
		for (int i = 0; i < modelArrayListEntry.getValue().getTransformation().getValues().length; i++) {
			modelViewBuffer.put(i, modelArrayListEntry.getValue().getTransformation().getValues()[i]);
		}
		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
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


		shader.unbind();

	}

	private void renderInstance(Map.Entry<String, ArrayList<InstanceObject>> modelArrayListEntry, Shader shader) {

		Model model = modelManager.getModel(modelArrayListEntry.getKey());
		Mesh mesh = meshManager.getMesh(model.getMeshString());

		mesh.initRender();

		materialManager.getMaterial(model.getMaterialID()).initRender(textureManager, shader);

		mesh.draw(drawVisitor, modelArrayListEntry.getValue());

		// clean up
		materialManager.getMaterial(model.getMaterialID()).endRender();

		mesh.endRender();

	}

	public void renderTerrain(HashMap<String, InstanceObject> terrainModels,
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

		if (clippingPlane != null) {
			shader.setUniform("clippingPlane", clippingPlane);
		} else {
			shader.setUniform("clippingPlane", Vec4f.ZERO);
		}

		createFog(fog, shader);

		for (Map.Entry<String, InstanceObject> modelInstanceObjectEntry : terrainModels.entrySet()) {

			renderTerrainInstance(modelInstanceObjectEntry, shader);

		}

		shader.unbind();

	}

	private void renderTerrainInstance(Map.Entry<String, InstanceObject> terrainModel, Shader shader) {

		Model model = modelManager.getModel(terrainModel.getKey());
		Mesh singleMesh = meshManager.getMesh(model.getMeshString());

		singleMesh.initRender();

		materialManager.getMaterial(model.getMaterialID()).initRender(textureManager, shader);

		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		int start = 3;
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(start);
			glVertexAttribPointer(start, 4, GL_FLOAT, false, MATRIX_SIZE_BYTES, i * VECTOR4F_SIZE_BYTES);
			glVertexAttribDivisor(start, 1);
			start++;
		}

		modelViewBuffer = MemoryUtil.memAllocFloat(MATRIX_SIZE_FLOATS);
		for (int i = 0; i < Matrix4f.Identity.getValues().length; i++) {
			modelViewBuffer.put(i, terrainModel.getValue().getTransformation().getValues()[i]);
		}

		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		glDrawElements(GL11.GL_TRIANGLES, singleMesh.size(), GL11.GL_UNSIGNED_INT, 0);

		// clean up

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glDisable(GL11.GL_TEXTURE_2D);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		singleMesh.endRender();

	}

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
