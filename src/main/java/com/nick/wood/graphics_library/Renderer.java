package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.lighting.DirectionalLight;
import com.nick.wood.graphics_library.lighting.PointLight;
import com.nick.wood.graphics_library.lighting.SpotLight;
import com.nick.wood.graphics_library.objects.scene_graph_objects.RenderObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.TextItem;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
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

	private final static int MAX_INSTANCE = 1500;

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int VECTOR4F_SIZE_BYTES = 4 * Renderer.FLOAT_SIZE_BYTES;
	private static final int MATRIX_SIZE_BYTES = Renderer.MATRIX_SIZE_FLOATS * Renderer.FLOAT_SIZE_BYTES;

	private final Shader shader;
	private final Shader hudShader;
	private final Matrix4f projectionMatrix;

	private Matrix4f lightViewMatrix = Matrix4f.Identity;

	private Vec3f ambientLight = new Vec3f(0.1f, 0.1f, 0.1f);
	private Vec3f hudAmbientLight = new Vec3f(0.2f, 0.1f, 0.1f);
	private HashMap<String, HashCodeCounter> meshedMeshFiles = new HashMap<>();

	private Matrix4f createOrthoProjMatrix() {

		float right = 10;
		float left = -10;
		float top = 10;
		float bottom = -10;
		float far = 10;
		float near = -10;

		return new Matrix4f(
				2.0f / (right - left), 0f, 0f, -(right + left) / (right - left),
				0f, 2.0f / (top - bottom), 0f, -(top + bottom) / (top - bottom),
				0f, 0f, -2.0f / (far - near), -(far + near) / (far - near),
				0f, 0f, 0f, 1.0f
		);
	}

	public Renderer(Window window) {
		this.shader = window.getShader();
		this.hudShader = window.getHudShader();
		this.projectionMatrix = window.getProjectionMatrix();
	}

	public void destroy() {

	}

	private void addToInstance(HashMap<String, HashCodeCounter> meshedMeshFiles, RenderObject<MeshObject> meshObject, String appendString) {
		if (!meshedMeshFiles.containsKey(meshObject.getObject().getStringToCompare() + appendString)) {
			meshedMeshFiles.put(meshObject.getObject().getStringToCompare() + appendString, new HashCodeCounter(meshObject.getObject().getStringToCompare() + appendString, meshObject.getObject(), meshObject.getObject().getMeshTransformation(), meshObject.getTransform()));
		} else {
			if (meshedMeshFiles.get(meshObject.getObject().getStringToCompare() + appendString).getAmount() <= MAX_INSTANCE) {
				meshedMeshFiles.get(meshObject.getObject().getStringToCompare() + appendString).addInstance(meshObject.getTransform());
			} else {
				addToInstance(meshedMeshFiles, meshObject, appendString + "1");
			}
		}
	}

	public void renderMesh(ArrayList<RenderObject<MeshObject>> meshObjects, RenderObject<Camera> primaryCamera, ArrayList<RenderObject<Light>> lights) {

		// set up meshes
		// get a lit of meshes via hash code of each type which depends on input mesh file and material
		this.meshedMeshFiles.clear();

		for (RenderObject<MeshObject> meshObject : meshObjects) {
			addToInstance(meshedMeshFiles, meshObject, "");
		}

		shader.bind();

		int pointLightIndex = 0;
		int spotLightIndex = 0;
		int directionalLightIndex = 0;

		for (RenderObject<Light> light : lights) {

			switch (light.getObject().getType()) {
				case POINT:
					createPointLight("", (PointLight) light.getObject(), pointLightIndex++, light.getTransform(), shader);
					break;
				case SPOT:
					createSpotLight((SpotLight) light.getObject(), spotLightIndex++, light.getTransform(), shader);
					break;
				case DIRECTIONAL:
					createDirectionalLight((DirectionalLight) light.getObject(), directionalLightIndex++, light.getTransform(), shader);
					break;
				default:
					break;
			}

		}

		shader.setUniform("ambientLight", ambientLight);
		shader.setUniform("specularPower", 0.5f);
		shader.setUniform("projection", projectionMatrix);

		shader.setUniform("cameraPos", primaryCamera.getTransform().multiply(primaryCamera.getObject().getPos()));
		shader.setUniform("view", primaryCamera.getObject().getView(primaryCamera.getTransform()));
		shader.setUniform("modelLightViewMatrix", lightViewMatrix);
		//shader.setUniform("orthoProjectionMatrix", orthoProjectionMatrix);

		// do all but text
		for (Map.Entry<String, HashCodeCounter> stringHashCodeCounterEntry : meshedMeshFiles.entrySet()) {
			if (!(stringHashCodeCounterEntry.getValue().getMeshObject() instanceof TextItem)) {
				renderInstance(stringHashCodeCounterEntry, shader);
			}
		}
		// do all text ones last as the background wont be see through properly if they dont
		for (Map.Entry<String, HashCodeCounter> stringHashCodeCounterEntry : meshedMeshFiles.entrySet()) {
			if (stringHashCodeCounterEntry.getValue().getMeshObject() instanceof TextItem) {
				renderInstance(stringHashCodeCounterEntry, shader);
			}
		}


		shader.unbind();
	}

	public void renderMiniMap(ArrayList<RenderObject<MeshObject>> meshObjects, RenderObject<Camera> camera, ArrayList<RenderObject<Light>> lights) {

		// set up meshes
		// get a lit of meshes via hash code of each type which depends on input mesh file and material
		HashMap<String, HashCodeCounter> meshedMeshFiles = new HashMap<>();

		for (RenderObject<MeshObject> meshObject : meshObjects) {
			addToInstance(meshedMeshFiles, meshObject, "");
		}

		hudShader.bind();

		int pointLightIndex = 0;
		int spotLightIndex = 0;
		int directionalLightIndex = 0;

		for (RenderObject<Light> light : lights) {

			switch (light.getObject().getType()) {
				case POINT:
					createPointLight("", (PointLight) light.getObject(), pointLightIndex++, light.getTransform(), hudShader);
					break;
				case SPOT:
					createSpotLight((SpotLight) light.getObject(), spotLightIndex++, light.getTransform(), hudShader);
					break;
				case DIRECTIONAL:
					createDirectionalLight((DirectionalLight) light.getObject(), directionalLightIndex++, light.getTransform(), hudShader);
					break;
				default:
					break;
			}

		}

		hudShader.setUniform("ambientLight", hudAmbientLight);
		hudShader.setUniform("specularPower", 0.5f);
		hudShader.setUniform("projection", projectionMatrix);

		hudShader.setUniform("cameraPos", camera.getTransform().multiply(camera.getObject().getPos()));
		hudShader.setUniform("view", camera.getObject().getView(camera.getTransform()));
		hudShader.setUniform("modelLightViewMatrix", lightViewMatrix);

		// do all but text
		for (Map.Entry<String, HashCodeCounter> stringHashCodeCounterEntry : meshedMeshFiles.entrySet()) {
			if (!(stringHashCodeCounterEntry.getValue().getMeshObject() instanceof TextItem)) {
				renderInstance(stringHashCodeCounterEntry, hudShader);
			}
		}
		// do all text ones last as the background wont be see through properly if they dont
		for (Map.Entry<String, HashCodeCounter> stringHashCodeCounterEntry : meshedMeshFiles.entrySet()) {
			if (stringHashCodeCounterEntry.getValue().getMeshObject() instanceof TextItem) {
				renderInstance(stringHashCodeCounterEntry, hudShader);
			}
		}


		hudShader.unbind();
	}

	private void renderInstance(Map.Entry<String, HashCodeCounter> stringHashCodeCounterEntry, Shader shader) {

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

		int index = 0;

		FloatBuffer modelViewBuffer = MemoryUtil.memAllocFloat(meshHashCode.getTransforms().size() * MATRIX_SIZE_FLOATS);
		for (Matrix4f transform : meshHashCode.getTransforms()) {
			modelViewBuffer.put(index * 16, meshHashCode.getRotationOfModel().multiply(transform).transpose().getValues());
			index++;
		}
		glBindBuffer(GL_ARRAY_BUFFER, modelViewVBO);
		glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

		MemoryUtil.memFree(modelViewBuffer);

		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, meshHashCode.getMeshObject().getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0, meshHashCode.getAmount());

		// clean up
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		meshHashCode.getMeshObject().getMesh().endRender();

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

	public void setAmbientLight(Vec3f ambientLight) {
		this.ambientLight = ambientLight;
	}
}
