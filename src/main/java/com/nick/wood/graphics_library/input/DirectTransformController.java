package com.nick.wood.graphics_library.input;

import com.nick.wood.game_control.input.ActionEnum;
import com.nick.wood.game_control.input.Control;
import com.nick.wood.graphics_library.objects.game_objects.*;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DirectTransformController implements Control {

	private TransformSceneGraph transformSceneGraph;
	private final HashMap<ActionEnum, Boolean> actions = new HashMap<>();
	private final boolean enableLook;
	private final boolean enableMove;
	private float sensitivity = 0.01f;
	private float speed = 1f;

	public DirectTransformController(TransformSceneGraph transformSceneGraph, boolean enableLook, boolean enableMove) {
		this.transformSceneGraph = transformSceneGraph;
		this.enableLook = enableLook;
		this.enableMove = enableMove;
	}

	public void reset() {
	}

	public void mouseMove(double dx, double dy, boolean shiftPressed) {
		if (enableLook) {

			QuaternionF rotationX = QuaternionF.RotationZ((float) -dx * sensitivity);
			QuaternionF rotationZ = QuaternionF.RotationX((float) -dy * sensitivity);

			// x axis rotation in local frame
			QuaternionF multiplyGlobalAxisX = rotationX.multiply(transformSceneGraph.getTransform().getRotation());
			// y axis rotation in globals frame
			QuaternionF multiplyGlobalAxisZ = transformSceneGraph.getTransform().getRotation().multiply(rotationZ);

			transformSceneGraph.getTransform().setRotation(multiplyGlobalAxisX.add(multiplyGlobalAxisZ).normalise());

		}
	}

	public void leftLinear() {
			transformSceneGraph.getTransform().setPosition(
					transformSceneGraph.getTransform().getPosition()
							.add(transformSceneGraph.getTransform().getRotation().rotateVector(Vec3f.X.scale(-speed)).toVec3f()));
	}

	public void rightLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().rotateVector(Vec3f.X.scale(speed)).toVec3f()));
	}

	public void forwardLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().rotateVector(Vec3f.Z.scale(-speed)).toVec3f()));
	}

	public void backLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().rotateVector(Vec3f.Z.scale(speed)).toVec3f()));
	}

	public void upLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().rotateVector(Vec3f.Y.scale(speed)).toVec3f()));
	}

	public void downLinear() {
		transformSceneGraph.getTransform().setPosition(
				transformSceneGraph.getTransform().getPosition()
						.add(transformSceneGraph.getTransform().getRotation().rotateVector(Vec3f.Y.scale(-speed)).toVec3f()));
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
