package com.nick.wood.graphics_library.objects;

import com.nick.wood.graphics_library.objects.mesh_objects.*;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;

import java.io.IOException;
import java.util.HashMap;

public class MeshManager {

	private final HashMap<String, Mesh> meshStringDescriptorToMeshMap;

	private final ModelLoader meshLoader = new ModelLoader();

	public MeshManager() {
		this.meshStringDescriptorToMeshMap = new HashMap<>();
	}

	public Mesh getMesh(String stringDescriptor) {
		return meshStringDescriptorToMeshMap.getOrDefault(stringDescriptor, meshStringDescriptorToMeshMap.get("DEFAULT"));
	}

	public void create(String defaultMesh) throws IOException {
		Mesh mesh = meshLoader.loadModel(defaultMesh);
		mesh.create();
		meshStringDescriptorToMeshMap.put("DEFAULT", mesh);
	}

	public void destroy() {
		for (Mesh mesh : this.meshStringDescriptorToMeshMap.values()) {
			mesh.destroy();
		}
	}

	public void createMesh(String filePath) throws IOException {
		// if mesh already made, just continue
		if (meshStringDescriptorToMeshMap.containsKey(filePath)) return;
		Mesh mesh = meshLoader.loadModel(filePath);
		mesh.create();
		meshStringDescriptorToMeshMap.put(filePath, mesh);
	}
}
