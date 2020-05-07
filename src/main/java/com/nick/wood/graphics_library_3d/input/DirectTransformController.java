package com.nick.wood.graphics_library_3d.input;

import com.nick.wood.game_control.input.ActionEnum;
import com.nick.wood.game_control.input.Control;
import com.nick.wood.graphics_library_3d.objects.Camera;
import com.nick.wood.graphics_library_3d.objects.scene_graph_objects.TransformSceneGraph;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.HashMap;
import java.util.UUID;

public class DirectTransformController implements Control {

	private TransformSceneGraph transformSceneGraph;
	private final HashMap<ActionEnum, Boolean> actions = new HashMap<>();
	private final boolean enableLook;
	private final boolean enableMove;

	public DirectTransformController(TransformSceneGraph transformSceneGraph, boolean enableLook, boolean enableMove) {
		this.transformSceneGraph = transformSceneGraph;
		this.enableLook = enableLook;
		this.enableMove = enableMove;
	}

	public void reset() {
	}

	public void mouseMove(double dx, double dy, boolean shiftPressed) {
	}

	public void leftLinear() {
			transformSceneGraph.getTransform().setPosition(
					transformSceneGraph.getTransform().getPosition()
							.add(transformSceneGraph.getTransform().getRotation().multiply(Vec3f.Z.neg())));
	}

	public void rightLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().multiply(Vec3f.Z)));
	}

	public void forwardLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().multiply(Vec3f.X)));
	}

	public void backLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().multiply(Vec3f.X.neg())));
	}

	public void upLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().multiply(Vec3f.Y)));
	}

	public void downLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().multiply(Vec3f.Y.neg())));
	}

	public void leftRoll() {
	}

	public void rightRoll() {
	}

	public void upPitch() {
	}

	public void downPitch() {
	}

	public void leftYaw() {
	}

	public void rightYaw() {
	}

	public void action() {
	}

	@Override
	public void setObjectBeingControlled(Object objectBeingControlled) {
	}

	public UUID getUuid() {
		return UUID.randomUUID();
	}

	public HashMap<ActionEnum, Boolean> getActions() {
		return actions;
	}
}
