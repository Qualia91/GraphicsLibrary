package com.nick.wood.graphics_library.objects.render_scene;

import com.nick.wood.graphics_library.Renderer;
import com.nick.wood.graphics_library.Shader;
import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.*;

public class Scene {


	private Vec3f ambientLight = new Vec3f(0.1f, 0.1f, 0.1f);
	private Vec3f skyboxAmbientLight = new Vec3f(0.9f, 0.9f, 0.9f);
	private Shader shader;
	private final HashMap<Light, InstanceObject> lights;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> meshes;
	private final HashMap<Camera, InstanceObject> cameras;
	private UUID primaryCamera;
	private Shader skyboxShader;
	private MeshObject skybox;

	public Scene() {
		this.lights = new HashMap<>();
		this.meshes = new HashMap<>();
		this.cameras = new HashMap<>();
	}

	public HashMap<MeshObject, ArrayList<InstanceObject>> getMeshes() {
		return meshes;
	}

	public HashMap<Light, InstanceObject> getLights() {
		return lights;
	}

	public HashMap<Camera, InstanceObject> getCameras() {
		return cameras;
	}

	public void render(Renderer renderer) {


		if (primaryCamera != null) {
			for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : cameras.entrySet()) {
				if (cameraInstanceObjectEntry.getValue().getUuid().equals(primaryCamera)) {
					if (skyboxShader != null && skybox != null) {
						renderer.renderSkybox(skybox, cameraInstanceObjectEntry, skyboxShader, skyboxAmbientLight);
					}

					if (shader != null) {
						renderer.renderScene(meshes, cameraInstanceObjectEntry, lights, shader, ambientLight);
					}
					break;
				}
			}
		}

		for (ArrayList<InstanceObject> value : meshes.values()) {
			value.clear();
		}

	}

	public void setPrimaryCamera(UUID primaryCamera) {
		this.primaryCamera = primaryCamera;
	}

	public void removeLight(UUID uuid) {
		lights.entrySet().removeIf(next -> next.getValue().getUuid().equals(uuid));
	}

	public void removeCamera(UUID uuid) {
		cameras.entrySet().removeIf(next -> next.getValue().getUuid().equals(uuid));
	}

	public void removeMesh(UUID uuid) {

		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> next : meshes.entrySet()) {
			next.getValue().removeIf(instance -> instance.getUuid().equals(uuid));

		}
	}

	public void attachShader(Shader shader) {
		this.shader = shader;
	}

	public void attachSkyboxShader(Shader skyboxShader) {
		this.skyboxShader = skyboxShader;
	}

	public void setSkybox(MeshObject skybox) {
		this.skybox = skybox;
	}

	public void removeSkybox() {
		this.skybox = null;
	}

	public Vec3f getAmbientLight() {
		return ambientLight;
	}

	public void setAmbientLight(Vec3f ambientLight) {
		this.ambientLight = ambientLight;
	}

	public Vec3f getSkyboxAmbientLight() {
		return skyboxAmbientLight;
	}

	public void setSkyboxAmbientLight(Vec3f skyboxAmbientLight) {
		this.skyboxAmbientLight = skyboxAmbientLight;
	}
}
