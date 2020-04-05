package com.nick.wood.graphics_library.input;

import org.lwjgl.glfw.*;

public class Inputs {

	private boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
	private boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
	private double mouseX;
	private double mouseY;
	private double offsetX;
	private double offsetY;

	private GLFWKeyCallback keyboard;
	private GLFWCursorPosCallback mouseMove;
	private GLFWMouseButtonCallback mouseButton;
	private GLFWScrollCallback glfwScrollCallback;

	public Inputs() {
		keyboard = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int keyPressed, int scanCode, int action, int mods) {
				keys[keyPressed] = (action != GLFW.GLFW_RELEASE);
			}
		};


		mouseMove = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xPos, double yPos) {
				mouseX = xPos;
				mouseY = yPos;
			}
		};

		mouseButton = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int keyPressed, int action, int mods) {
				buttons[keyPressed] = (action != GLFW.GLFW_RELEASE);
			}
		};

		glfwScrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				offsetX += xoffset;
				offsetY += yoffset;
			}
		};
	}

	public boolean[] getKeys() {
		return keys;
	}

	public boolean[] getButtons() {
		return buttons;
	}

	public double getMouseX() {
		return mouseX;
	}

	public double getMouseY() {
		return mouseY;
	}

	public double getOffsetX() {
		return offsetX;
	}

	public double getOffsetY() {
		return offsetY;
	}

	public GLFWScrollCallback getGlfwScrollCallback() {
		return glfwScrollCallback;
	}

	public GLFWKeyCallback getKeyboard() {
		return keyboard;
	}

	public GLFWCursorPosCallback getMouseMove() {
		return mouseMove;
	}

	public GLFWMouseButtonCallback getMouseButton() {
		return mouseButton;
	}

	public boolean isKeyPressed(int key) {
		return keys[key];
	}
}
