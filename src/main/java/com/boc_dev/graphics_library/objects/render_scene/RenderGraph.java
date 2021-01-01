package com.boc_dev.graphics_library.objects.render_scene;

import com.boc_dev.graphics_library.objects.Camera;
import com.boc_dev.graphics_library.objects.lighting.Light;
import com.boc_dev.graphics_library.objects.text.TextInstance;

import java.util.*;

public class RenderGraph {

	private final HashMap<Light, InstanceObject> lights;
	private final HashMap<String, ArrayList<InstanceObject>> meshes;
	private final HashMap<String, ArrayList<InstanceObject>> pickingMeshes;
	// font name then array of text using font
	private final HashMap<String, ArrayList<TextInstance>> textMeshes;
	private final HashMap<String, InstanceObject> waterMeshes;
	private final HashMap<String, InstanceObject> terrainMeshes;
	private final HashMap<Camera, InstanceObject> cameras;
	private Pair<String, InstanceObject> skybox;

	public RenderGraph() {
		this.lights = new HashMap<>();
		this.meshes = new HashMap<>();
		this.pickingMeshes = new HashMap<>();
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

	public HashMap<String, ArrayList<InstanceObject>> getPickingMeshes() {
		return pickingMeshes;
	}

	public HashMap<String, ArrayList<TextInstance>> getTextMeshes() {
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

	public void setSkybox(Pair<String, InstanceObject> skybox) {
		this.skybox = skybox;
	}
}
