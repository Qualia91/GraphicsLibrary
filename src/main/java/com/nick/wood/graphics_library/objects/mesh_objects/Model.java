package com.nick.wood.graphics_library.objects.mesh_objects;

import java.util.UUID;

public class Model {

	private final String meshString;
	private final UUID materialID;
	private final String modelID;

	public Model(String meshString, UUID materialID) {
		this.meshString = meshString;
		this.materialID = materialID;
		String materialIdString;
		if (materialID == null) {
			materialIdString = "DEFAULT";
		} else {
			materialIdString = materialID.toString();
		}
		this.modelID = meshString + materialIdString;
	}

	public String getMeshString() {
		return meshString;
	}

	public UUID getMaterialID() {
		return materialID;
	}

	public String getStringID() {
		return modelID;
	}
}
