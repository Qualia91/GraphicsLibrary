package com.nick.wood.graphics_library.objects.managers;

import com.nick.wood.graphics_library.objects.mesh_objects.Model;

import java.util.HashMap;
import java.util.UUID;

public class ModelManager {

	private final HashMap<String, Model> modelStringDescriptorToMeshMap;

	public ModelManager() {
		this.modelStringDescriptorToMeshMap = new HashMap<>();
	}

	public Model getModel(String stringDescriptor) {
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
