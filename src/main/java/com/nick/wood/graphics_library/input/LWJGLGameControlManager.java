package com.nick.wood.graphics_library.input;

import com.nick.wood.game_control.input.Control;
import com.nick.wood.game_control.input.ControlManager;
import org.lwjgl.glfw.GLFW;

public class LWJGLGameControlManager implements ControlManager<GraphicsLibraryInput> {

	private GraphicsLibraryInput input;
	private Control control;
	private double oldMouseX = 0.0;
	private double oldMouseY = 0.0;

	public LWJGLGameControlManager(GraphicsLibraryInput input, Control control) {
		this.input = input;
		this.control = control;
	}

	public GraphicsLibraryInput getInput() {
		return input;
	}

	public Control getControl() {
		return control;
	}

	public void checkInputs() {

		double newMouseX = input.getMouseX();
		double newMouseY = input.getMouseY();
		if (Math.abs(oldMouseX) <= 0.000001) {
			oldMouseX = newMouseX;
		}
		if (Math.abs(oldMouseY) <= 0.000001) {
			oldMouseY = newMouseY;
		}
		double dx = newMouseX - oldMouseX;
		double dy = newMouseY - oldMouseY;
		oldMouseX = newMouseX;
		oldMouseY = newMouseY;

		control.mouseMove(Math.copySign(Math.min(Math.abs(dx), 100.0), dx) , Math.copySign(Math.min(Math.abs(dy), 100.0), dy), input.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT));

		if (input.isKeyPressed(GLFW.GLFW_KEY_A)) {
			control.leftLinear();
		}
		if (input.isKeyPressed(GLFW.GLFW_KEY_W)) {
			control.forwardLinear();
		}
		if (input.isKeyPressed(GLFW.GLFW_KEY_D)) {
			control.rightLinear();
		}
		if (input.isKeyPressed(GLFW.GLFW_KEY_S)) {
			control.backLinear();
		}
		if (input.isKeyPressed(GLFW.GLFW_KEY_Q)) {
			control.upLinear();
		}
		if (input.isKeyPressed(GLFW.GLFW_KEY_E)) {
			control.downLinear();
		}
		if (input.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
			if (input.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
				control.leftYaw();
			} else {
				control.leftRoll();
			}
		}
		if (input.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
			if (input.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
				control.rightYaw();
			} else {
				control.rightRoll();
			}
		}
		if (input.isKeyPressed(GLFW.GLFW_KEY_UP)) {
			control.upPitch();
		}
		if (input.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
			control.downPitch();
		}

		if (input.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			control.action();
		}

	}

	public void setInputs(GraphicsLibraryInput input) {
		this.input = input;
	}

	public void setControl(Control control) {
		this.control = control;
	}
}
