package com.nick.wood.graphics_library.objects.managers;

import com.nick.wood.graphics_library.objects.mesh_objects.*;
import com.nick.wood.graphics_library.objects.mesh_objects.renderer_objects.OpenGlMesh;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;

import java.io.IOException;
import java.util.HashMap;

public class MeshManager {

	private final BasicMeshCreator basicMeshCreator = new BasicMeshCreator();

	private final HashMap<String, Mesh> meshStringDescriptorToMeshMap;

	private final ModelLoader meshLoader = new ModelLoader();

	public MeshManager() {
		this.meshStringDescriptorToMeshMap = new HashMap<>();
	}

	public Mesh getMesh(String stringDescriptor) {
		return meshStringDescriptorToMeshMap.getOrDefault(stringDescriptor, meshStringDescriptorToMeshMap.get("DEFAULT"));
	}

	public void create() throws IOException {
		// create basic meshes as default
		Mesh circle = basicMeshCreator.createCircle(50, new OpenGlMesh());
		Mesh cube = basicMeshCreator.createCube(false, new OpenGlMesh());
		Mesh cubeSkybox = basicMeshCreator.createCube(true, new OpenGlMesh());
		Mesh square = basicMeshCreator.createSquare(new OpenGlMesh());
		Mesh invertedSphere = meshLoader.loadModel("models/invertedSphere.obj", new OpenGlMesh());
		Mesh sphere = meshLoader.loadModel("models/sphere.obj", new OpenGlMesh());

		circle.create();
		cubeSkybox.create();
		cube.create();
		square.create();
		invertedSphere.create();
		sphere.create();

		meshStringDescriptorToMeshMap.put("DEFAULT", cube);
		meshStringDescriptorToMeshMap.put("DEFAULT_CUBE_SKYBOX", cubeSkybox);
		meshStringDescriptorToMeshMap.put("DEFAULT_CIRCLE", circle);
		meshStringDescriptorToMeshMap.put("DEFAULT_CUBE", cube);
		meshStringDescriptorToMeshMap.put("DEFAULT_SQUARE", square);
		meshStringDescriptorToMeshMap.put("DEFAULT_SPHERE", sphere);
		meshStringDescriptorToMeshMap.put("DEFAULT_SPHERE_SKYBOX", invertedSphere);
	}

	public void destroy() {
		for (Mesh mesh : this.meshStringDescriptorToMeshMap.values()) {
			mesh.destroy();
		}
	}

	public void createMesh(String filePath) throws IOException {
		// if mesh already made, just continue
		if (meshStringDescriptorToMeshMap.containsKey(filePath)) return;
		Mesh mesh = meshLoader.loadModel(filePath, new OpenGlMesh());
		mesh.create();
		meshStringDescriptorToMeshMap.put(filePath, mesh);
	}
}
