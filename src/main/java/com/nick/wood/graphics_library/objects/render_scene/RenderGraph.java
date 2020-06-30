package com.nick.wood.graphics_library.objects.render_scene;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;

import java.util.*;

public class RenderGraph {

	private final HashMap<Light, InstanceObject> lights;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> meshes;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> waterMeshes;
	private final HashMap<Camera, InstanceObject> cameras;
	private MeshObject skybox;
	private final ArrayList<Mesh> meshesToBuild = new ArrayList<>();
	private final ArrayList<Mesh> meshesToDestroy = new ArrayList<>();

	public RenderGraph() {
		this.lights = new HashMap<>();
		this.meshes = new HashMap<>();
		this.waterMeshes = new HashMap<>();
		this.cameras = new HashMap<>();
		this.skybox = null;
	}

	public void empty() {
		stripMeshArrays(meshes);
		stripMeshArrays(waterMeshes);
	}

	private void stripMeshArrays(HashMap<MeshObject, ArrayList<InstanceObject>> meshes) {
		Iterator<ArrayList<InstanceObject>> iterator = meshes.values().iterator();
		while (iterator.hasNext()) {
			ArrayList<InstanceObject> value = iterator.next();
			if (value.isEmpty()) {
				iterator.remove();
			} else {
				value.clear();
			}
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
		Iterator<Map.Entry<MeshObject, ArrayList<InstanceObject>>> iterator = meshes.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<MeshObject, ArrayList<InstanceObject>> next = iterator.next();
			next.getValue().removeIf(instance -> instance.getUuid().equals(uuid));
			if (next.getValue().isEmpty()) {
				meshesToDestroy.add(next.getKey().getMesh());
				iterator.remove();
			}
		}
	}

	public void removeWater(UUID uuid) {
		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> next : waterMeshes.entrySet()) {
			next.getValue().removeIf(instance -> instance.getUuid().equals(uuid));
			if (next.getValue().isEmpty()) {
				meshesToDestroy.add(next.getKey().getMesh());
			}
		}
	}

	public void setSkybox(MeshObject skybox) {
		if (!skybox.equals(this.skybox)) {
			// if we are replacing the skybox, destroy the last one
			if (this.skybox != null) {
				meshesToDestroy.add(skybox.getMesh());
			}
			meshesToBuild.add(skybox.getMesh());
			this.skybox = skybox;
		}
	}

	public void removeSkybox() {
		meshesToDestroy.add(skybox.getMesh());
		this.skybox = null;
	}

	public ArrayList<Mesh> getMeshesToBuild() {
		return meshesToBuild;
	}

	public ArrayList<Mesh> getMeshesToDestroy() {
		return meshesToDestroy;
	}
}
