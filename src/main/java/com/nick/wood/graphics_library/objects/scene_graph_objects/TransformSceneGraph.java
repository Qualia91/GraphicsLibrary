package com.nick.wood.graphics_library.objects.scene_graph_objects;

import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class TransformSceneGraph implements SceneGraphNode {

	private final SceneGraphNodeData sceneGraphNodeData;
	private final Transform transform;
	private boolean changedSinceRender;

	public TransformSceneGraph(SceneGraphNode parent, Transform transform) {
		this.sceneGraphNodeData = new SceneGraphNodeData(parent, RenderObjectType.TRANSFORM, this);
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

	public Transform getTransform() {
		return transform;
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return sceneGraphNodeData;
	}
}
