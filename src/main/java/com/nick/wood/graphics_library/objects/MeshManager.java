package com.nick.wood.graphics_library.objects;

import com.nick.wood.graphics_library.objects.mesh_objects.InstanceMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;
import com.nick.wood.graphics_library.objects.mesh_objects.SingleMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.ModelLoader;
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
		SingleMesh singleMesh = meshLoader.loadModel(defaultMesh);
		singleMesh.create();
		InstanceMesh instanceMesh = new InstanceMesh(singleMesh);
		meshStringDescriptorToMeshMap.put("DEFAULT", instanceMesh);
	}

	public void destroy() {
		for (Mesh mesh : this.meshStringDescriptorToMeshMap.values()) {
			mesh.destroy();
		}
	}

	public void createMesh(String filePath) throws IOException {
		// if mesh already made, just continue
		if (meshStringDescriptorToMeshMap.containsKey(filePath)) return;
		SingleMesh singleMesh = meshLoader.loadModel(filePath);
		singleMesh.create();
		InstanceMesh instanceMesh = new InstanceMesh(singleMesh);
		meshStringDescriptorToMeshMap.put(filePath, instanceMesh);
	}
}
