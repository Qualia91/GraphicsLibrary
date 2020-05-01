package com.nick.wood.graphics_library_3d.objects.game_objects;

import com.nick.wood.graphics_library_3d.objects.Transform;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class TransformGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;
	private final Transform transform;
	private boolean changedSinceRender;

	public TransformGameObject(GameObjectNode parent, Transform transform) {
		this.gameObjectNodeData = new GameObjectNodeData(parent, GameObjectType.TRANSFORM, this);
		this.transform = transform;
	}

	public Matrix4f getTransformForRender() {
		changedSinceRender = false;
		return transform.getTransform();
	}

	public void setPosition(Vec3f pos) {
		changedSinceRender = true;
		transform.setPosition(pos);
	}

	public void setRotation(Matrix4f rot) {
		changedSinceRender = true;
		transform.setRotation(rot);
	}

	public void setScale(Vec3f scale) {
		changedSinceRender = true;
		transform.setScale(scale);
	}

	@Override
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}
}
