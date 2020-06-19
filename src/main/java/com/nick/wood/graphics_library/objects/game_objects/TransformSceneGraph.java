package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.UUID;

public class TransformSceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;
	private final Transform transform;

	public TransformSceneGraph(SceneGraphNode parent, Transform transform) {
		this.sceneGraphNodeData = new SceneGraphNodeData(parent, RenderObjectType.TRANSFORM, this);
		this.transform = transform;
	}

	public TransformSceneGraph(UUID uuid, SceneGraphNode parent, Transform transform) {
		this.sceneGraphNodeData = new SceneGraphNodeData(uuid, parent, RenderObjectType.TRANSFORM, this);
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
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
	}
}
