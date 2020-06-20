package com.nick.wood.graphics_library.objects.game_objects;

import java.util.UUID;

public class MeshObject implements GameObject {

	private final GameObjectData gameObjectData;
	private com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshObject;

	public MeshObject(GameObject parent, com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshObject) {
		this.gameObjectData = new GameObjectData(parent, ObjectType.MESH, this);
		this.meshObject = meshObject;
	}

	public MeshObject(UUID uuid, GameObject parent, com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshObject) {
		this.gameObjectData = new GameObjectData(uuid, parent, ObjectType.MESH, this);
		this.meshObject = meshObject;
	}

	public com.nick.wood.graphics_library.objects.mesh_objects.MeshObject getMeshObject() {
		return meshObject;
	}

	@Override
	public GameObjectData getGameObjectData() {
		return gameObjectData;
	}

	public void removeMeshObject() {
		meshObject = null;
	}

	public void setMeshObject(com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshObject) {
		this.meshObject = meshObject;
	}
}
