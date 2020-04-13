package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.lighting.DirectionalLight;
import com.nick.wood.graphics_library.lighting.PointLight;
import com.nick.wood.graphics_library.lighting.SpotLight;
import com.nick.wood.graphics_library.objects.game_objects.RenderObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;

public class Renderer {

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int VECTOR4F_SIZE_BYTES = 4 * Renderer.FLOAT_SIZE_BYTES;
	private static final int MATRIX_SIZE_BYTES = Renderer.MATRIX_SIZE_FLOATS * Renderer.FLOAT_SIZE_BYTES;

	private final Shader shader;
	private final Matrix4d projectionMatrix;


	public Renderer(Window window) {
		this.shader = window.getShader();
		this.projectionMatrix = window.getProjectionMatrix();
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


		shader.setUniform("ambientLight", new Vec3d(0.1, 0.1, 0.1));
		shader.setUniform("specularPower", 0.5f);
		shader.setUniform("projection", projectionMatrix);

		// for now just use camera one
		for (Map.Entry<UUID, RenderObject<Camera>> uuidRenderObjectCameraEntry : cameras.entrySet()) {

			shader.setUniform("view", uuidRenderObjectCameraEntry.getValue().getObject().getView());
			shader.setUniform("cameraPos", uuidRenderObjectCameraEntry.getValue().getObject().getPos());

			// i will keep looping through the mesh objects hash map until a counter has reached length of map
			// each iteration i will get the next mesh and if i haven't already rendered it, i will loop
			// through rendering all same meshes together using instanced rendering
			int meshRenderedCounter = 0;
			ArrayList<String> meshedMeshFiles = new ArrayList<>();

			for (Map.Entry<UUID, RenderObject<MeshObject>> meshObjectEntry : meshObjects.entrySet()) {

				RenderObject<MeshObject> meshObjectRenderObject = meshObjectEntry.getValue();

				meshObjectRenderObject.getObject().getMesh().initRender();

				meshObjectRenderObject.getObject().getMesh().getVertexCount();

				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshObjectRenderObject.getObject().getMesh().getIbo());

				// bind texture
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL13.glBindTexture(GL11.GL_TEXTURE_2D, meshObjectRenderObject.getObject().getMesh().getMaterial().getTextureId());

				shader.setUniform("model", meshObjectRenderObject.getObject().getRotationOfModel().multiply(meshObjectRenderObject.getTransform()));

				shader.setUniform("material.diffuse", meshObjectRenderObject.getObject().getMesh().getMaterial().getDiffuseColour());
				shader.setUniform("material.specular", meshObjectRenderObject.getObject().getMesh().getMaterial().getSpecularColour());
				shader.setUniform("material.shininess", meshObjectRenderObject.getObject().getMesh().getMaterial().getShininess());
				shader.setUniform("material.reflectance", meshObjectRenderObject.getObject().getMesh().getMaterial().getReflectance());

				GL31.glDrawElements(GL11.GL_TRIANGLES, meshObjectRenderObject.getObject().getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

				meshObjectRenderObject.getObject().getMesh().endRender();
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
