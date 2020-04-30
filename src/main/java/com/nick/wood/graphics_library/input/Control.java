package com.nick.wood.graphics_library.input;

import com.nick.wood.maths.objects.matrix.Matrix4d;
import com.nick.wood.maths.objects.vector.Vec;
import com.nick.wood.maths.objects.vector.Vec3d;

import java.util.UUID;

public interface Control {

	UUID getUuid();
	void reset();
	void mouseMove(double dx, double dy, boolean shiftPressed);
	void leftLinear();
	void rightLinear();
	void forwardLinear();
	void backLinear();
	void upLinear();
	void downLinear();
	void leftRoll();
	void rightRoll();
	void upPitch();
	void downPitch();
	void leftYaw();
	void rightYaw();
	void action();
	Vec getForce();
	Vec getTorque();

}
