package com.nick.wood.graphics_library.objects.render_scene;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;

import java.util.*;

public class RenderGraph {

	private final HashMap<Light, InstanceObject> lights;
	private final HashMap<String, ArrayList<InstanceObject>> meshes;
	private final HashMap<String, ArrayList<InstanceObject>> textMeshes;
	private final HashMap<String, InstanceObject> waterMeshes;
	private final HashMap<String, InstanceObject> terrainMeshes;
	private final HashMap<Camera, InstanceObject> cameras;
	private Pair<String, InstanceObject> skybox;

	public RenderGraph() {
		this.lights = new HashMap<>();
		this.meshes = new HashMap<>();
		this.textMeshes = new HashMap<>();
		this.waterMeshes = new HashMap<>();
		this.terrainMeshes = new HashMap<>();
		this.cameras = new HashMap<>();
		this.skybox = null;
	}

	public HashMap<Light, InstanceObject> getLights() {
		return lights;
	}

	public HashMap<String, ArrayList<InstanceObject>> getMeshes() {
		return meshes;
	}

	public HashMap<String, ArrayList<InstanceObject>> getTextMeshes() {
		return textMeshes;
	}

	public HashMap<String, InstanceObject> getWaterMeshes() {
		return waterMeshes;
	}

	public HashMap<String, InstanceObject> getTerrainMeshes() {
		return terrainMeshes;
	}

	public HashMap<Camera, InstanceObject> getCameras() {
		return cameras;
	}

	public Pair<String, InstanceObject> getSkybox() {
		return skybox;
	}
}
