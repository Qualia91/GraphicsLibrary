package com.nick.wood.graphics_library.objects;

import com.nick.wood.graphics_library.Texture;
import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;

import java.util.HashMap;

public class ResourceManager {

	HashMap<String, Mesh> meshMap = new HashMap<>();
	HashMap<String, Texture> textureMap = new HashMap<>();

	public ResourceManager() {
	}
}
