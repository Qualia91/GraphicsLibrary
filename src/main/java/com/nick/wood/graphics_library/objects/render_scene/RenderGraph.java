package com.nick.wood.graphics_library.objects.render_scene;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RenderGraph {

	private final HashMap<Light, InstanceObject> lights;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> meshes;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> waterMeshes;
	private final HashMap<Camera, InstanceObject> cameras;
	private MeshObject skybox;

	public RenderGraph() {
		this.lights = new HashMap<>();
		this.meshes = new HashMap<>();
		this.waterMeshes = new HashMap<>();
		this.cameras = new HashMap<>();
		this.skybox = null;
	}

	public void empty() {
		for (ArrayList<InstanceObject> value : meshes.values()) {
		value.clear();
	}
		for (ArrayList<InstanceObject> value : waterMeshes.values()) {
			value.clear();
		}
	}

	public HashMap<Light, InstanceObject> getLights() {
		return lights;
	}

	public HashMap<MeshObject, ArrayList<InstanceObject>> getMeshes() {
		return meshes;
	}

	public HashMap<MeshObject, ArrayList<InstanceObject>> getWaterMeshes() {
		return waterMeshes;
	}

	public HashMap<Camera, InstanceObject> getCameras() {
		return cameras;
	}

	public MeshObject getSkybox() {
		return skybox;
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

	public void removeWater(UUID uuid) {
		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> next : waterMeshes.entrySet()) {
			next.getValue().removeIf(instance -> instance.getUuid().equals(uuid));
		}
	}

	public void setSkybox(MeshObject skybox) {
		this.skybox = skybox;
	}

	public void removeSkybox() {
		this.skybox = null;
	}

}
