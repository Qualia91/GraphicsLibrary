package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.UUID;

public class TransformObject implements GameObject {

	private final GameObjectData gameObjectData;
	private final Transform transform;

	public TransformObject(GameObject parent, Transform transform) {
		this.gameObjectData = new GameObjectData(parent, ObjectType.TRANSFORM, this);
		this.transform = transform;
	}

	public TransformObject(UUID uuid, GameObject parent, Transform transform) {
		this.gameObjectData = new GameObjectData(uuid, parent, ObjectType.TRANSFORM, this);
		this.transform = transform;
	}

	public Matrix4f getTransformForRender() {
		return transform.getSRT();
	}

	public void setPosition(Vec3f pos) {
		transform.setPosition(pos);
	}

	public void setRotation(QuaternionF rot) {
		transform.setRotation(rot);
	}

	public void setScale(Vec3f scale) {
		transform.setScale(scale);
	}

	public Transform getTransform() {
		return transform;
	}

	@Override
	public GameObjectData getGameObjectData() {
		return gameObjectData;
	}
}
