package com.boc_dev.graphics_library.objects.managers;

import com.boc_dev.graphics_library.objects.mesh_objects.Model;

import java.util.HashMap;
import java.util.UUID;

public class ModelManager {

	private final HashMap<String, Model> modelStringDescriptorToMeshMap;

	public ModelManager() {
		this.modelStringDescriptorToMeshMap = new HashMap<>();
	}

	public Model getModel(String stringDescriptor) {
//		System.out.println("Model: " + modelStringDescriptorToMeshMap.size());
//		modelStringDescriptorToMeshMap.forEach((s, model) -> System.out.println(s + " " + model.getStringID()));
		return modelStringDescriptorToMeshMap.getOrDefault(stringDescriptor, modelStringDescriptorToMeshMap.get("DEFAULT"));
	}

	public void create(UUID defaultMaterialID)  {
		modelStringDescriptorToMeshMap.put("DEFAULT", new Model("DEFAULT", defaultMaterialID));
	}

	public void addModel(Model model) {
		modelStringDescriptorToMeshMap.putIfAbsent(model.getStringID(), model);
	}

	public void removeModel(Model model) {
		modelStringDescriptorToMeshMap.put(model.getStringID(), model);
	}
}
