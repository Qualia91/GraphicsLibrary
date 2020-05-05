package com.nick.wood.graphics_library_3d.input;

import com.nick.wood.graphics_library_3d.objects.Camera;
import com.nick.wood.maths.objects.vector.Vec3d;

import java.util.HashMap;
import java.util.UUID;

public class DirectCameraController implements Control {

	private Camera camera;
	private final HashMap<ActionEnum, Boolean> actions = new HashMap<>();
	private final boolean enableLook;
	private final boolean enableMove;

	public DirectCameraController(Camera camera, boolean enableLook, boolean enableMove) {
		this.camera = camera;
		this.enableLook = enableLook;
		this.enableMove = enableMove;
	}

	public void reset() {
	}

	public void mouseMove(double dx, double dy, boolean shiftPressed) {
		if (enableLook) {
			camera.rotate((float) dx, (float) dy);
		}
	}

	public void leftLinear() {
		if (enableMove) {
			camera.left();
		}
	}

	public void rightLinear() {
		if (enableMove) {
			camera.right();
		}
	}

	public void forwardLinear() {
		if (enableMove) {
			camera.forward();
		}
	}

	public void backLinear() {
		if (enableMove) {
			camera.back();
		}
	}

	public void upLinear() {
		if (enableMove) {
			camera.up();
		}
	}

	public void downLinear() {
		if (enableMove) {
			camera.down();
		}
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
		if (objectBeingControlled instanceof Camera) {
			this.camera = (Camera) objectBeingControlled;
		}
	}

	public UUID getUuid() {
		return UUID.randomUUID();
	}

	public HashMap<ActionEnum, Boolean> getActions() {
		return actions;
	}
}
