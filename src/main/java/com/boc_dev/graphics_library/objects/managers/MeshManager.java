package com.boc_dev.graphics_library.objects.managers;

import com.boc_dev.graphics_library.objects.mesh_objects.BasicMeshCreator;
import com.boc_dev.graphics_library.objects.mesh_objects.InstanceMesh;
import com.boc_dev.graphics_library.objects.mesh_objects.Mesh;
import com.boc_dev.graphics_library.objects.mesh_objects.ModelLoader;
import com.boc_dev.graphics_library.objects.mesh_objects.renderer_objects.OpenGlMesh;
import com.boc_dev.maths.objects.vector.Vec3f;

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
//		System.out.println("Mesh: " + meshStringDescriptorToMeshMap.size());
//		meshStringDescriptorToMeshMap.forEach((s, mesh) -> System.out.println(s + " " + mesh));
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

	public void createMesh(float[][] grid, double cellSpace, String name) {
		Mesh mesh = basicMeshCreator.createMarchingMeshMap(new OpenGlMesh(), grid, cellSpace);
		mesh.create();
		meshStringDescriptorToMeshMap.put(name, mesh);
	}

	public void destroyMesh(String name) {
		this.meshStringDescriptorToMeshMap.get(name).destroy();
		this.meshStringDescriptorToMeshMap.remove(name);
	}

	public void convertToInstancedMesh(String meshString) {
		InstanceMesh instanceMesh = new InstanceMesh(meshStringDescriptorToMeshMap.get(meshString));
		instanceMesh.createTransformArray();
		System.out.println("Converting " + meshString + " to instanced mesh");
		meshStringDescriptorToMeshMap.put(meshString, instanceMesh);
	}

	public void convertToSingleMesh(String meshString) {
		InstanceMesh instanceMesh = (InstanceMesh) meshStringDescriptorToMeshMap.get(meshString);
		instanceMesh.destroyInstancing();
		System.out.println("Converting " + meshString + " to single mesh");
		meshStringDescriptorToMeshMap.put(meshString, instanceMesh.getSingleMesh());
	}

	public void createMesh(Vec3f[] vertex, String name) {
		Mesh mesh = basicMeshCreator.createMarchingMeshMap(new OpenGlMesh(), vertex);
		mesh.create();
		meshStringDescriptorToMeshMap.put(name, mesh);
	}
}
