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
	private final HashMap<MeshObject, ArrayList<InstanceObject>> terrainMeshes;
	private final HashMap<Camera, InstanceObject> cameras;
	private MeshObject skybox;
	private final ArrayList<Mesh> meshesToBuild = new ArrayList<>();
	private final ArrayList<Mesh> meshesToDestroy = new ArrayList<>();

	public RenderGraph() {
		this.lights = new HashMap<>();
		this.meshes = new HashMap<>();
		this.waterMeshes = new HashMap<>();
		this.terrainMeshes = new HashMap<>();
		this.cameras = new HashMap<>();
		this.skybox = null;
	}

	public void empty() {
		stripMeshArrays(meshes);
		stripMeshArrays(waterMeshes);
		stripMeshArrays(terrainMeshes);
	}

	private void stripMeshArrays(HashMap<MeshObject, ArrayList<InstanceObject>> meshes) {
		Iterator<Map.Entry<MeshObject, ArrayList<InstanceObject>>> iterator = meshes.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<MeshObject, ArrayList<InstanceObject>> next = iterator.next();
			if (next.getValue().isEmpty()) {
				meshesToDestroy.add(next.getKey().getMesh());
				iterator.remove();
			} else {
				next.getValue().clear();
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

	public HashMap<MeshObject, ArrayList<InstanceObject>> getTerrainMeshes() {
		return terrainMeshes;
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
		removeMesh(uuid, meshes);
	}

	public void removeWater(UUID uuid) {
		removeMesh(uuid, waterMeshes);
	}

	public void removeTerrain(UUID uuid) {
		removeMesh(uuid, terrainMeshes);
	}

	private void removeMesh(UUID uuid, HashMap<MeshObject, ArrayList<InstanceObject>> mesheMap) {
		Iterator<Map.Entry<MeshObject, ArrayList<InstanceObject>>> iterator = mesheMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<MeshObject, ArrayList<InstanceObject>> next = iterator.next();
			next.getValue().removeIf(instance -> instance.getUuid().equals(uuid));
			if (next.getValue().isEmpty()) {
				meshesToDestroy.add(next.getKey().getMesh());
				iterator.remove();
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
