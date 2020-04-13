package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;

public class TransformGameObject implements GameObjectNode {

	private final GameObjectNodeData gameObjectNodeData;
	private final Transform transform;
	private boolean changedSinceRender;

	public TransformGameObject(GameObjectNode parent, Transform transform) {
		this.gameObjectNodeData = new GameObjectNodeData(parent, GameObjectType.TRANSFORM, this);
		this.transform = transform;
	}

	public Matrix4d getTransformForRender() {
		changedSinceRender = false;
		return transform.getTransform();
	}

	public void setPosition(Vec3d pos) {
		changedSinceRender = true;
		transform.setPosition(pos);
	}

	public void setRotation(Matrix4d rot) {
		changedSinceRender = true;
		transform.setRotation(rot);
	}

	public void setScale(Vec3d scale) {
		changedSinceRender = true;
		transform.setScale(scale);
	}

	@Override
	public GameObjectNodeData getGameObjectNodeData() {
		return gameObjectNodeData;
	}
}
