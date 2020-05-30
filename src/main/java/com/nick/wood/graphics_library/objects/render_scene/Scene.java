package com.nick.wood.graphics_library.objects.render_scene;

import com.nick.wood.graphics_library.Renderer;
import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;

import java.util.*;

public class Scene {

	private final HashMap<Light, InstanceObject> lights;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> meshes;
	private final HashMap<Camera, InstanceObject> cameras;
	private UUID primaryCamera;

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
					renderer.renderScene(meshes, cameraInstanceObjectEntry, lights);
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
}
