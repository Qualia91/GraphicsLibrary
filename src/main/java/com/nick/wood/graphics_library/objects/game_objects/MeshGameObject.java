package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;

import java.util.UUID;

public class MeshGameObject implements GameObject {

	private final GameObjectData gameObjectData;
	private MeshObject meshObject;

	public MeshGameObject(GameObject parent, MeshObject meshObject) {
		this.gameObjectData = new GameObjectData(parent, ObjectType.MESH, this);
		this.meshObject = meshObject;
	}

	public MeshGameObject(UUID uuid, GameObject parent, MeshObject meshObject) {
		this.gameObjectData = new GameObjectData(uuid, parent, ObjectType.MESH, this);
		this.meshObject = meshObject;
	}

	public MeshObject getMeshObject() {
		return meshObject;
	}

	@Override
	public GameObjectData getGameObjectData() {
		return gameObjectData;
	}

	public void removeMeshObject() {
		meshObject = null;
	}

	public void setMeshObject(MeshObject meshObject) {
		this.meshObject = meshObject;
	}
}
