package com.nick.wood.graphics_library.objects.render_scene;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;

import java.util.*;

public class RenderGraph {

	private final long step;
	private final HashMap<Light, InstanceObject> lights;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> meshes;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> waterMeshes;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> terrainMeshes;
	private final HashMap<Camera, InstanceObject> cameras;
	private MeshObject skybox;

	public RenderGraph(long step) {
		this.step = step;
		this.lights = new HashMap<>();
		this.meshes = new HashMap<>();
		this.waterMeshes = new HashMap<>();
		this.terrainMeshes = new HashMap<>();
		this.cameras = new HashMap<>();
		this.skybox = null;
	}

	public long getStep() {
		return step;
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

	public void setSkybox(MeshObject skybox) {
		if (!skybox.equals(this.skybox)) {
			this.skybox = skybox;
		}
	}

}
