package com.nick.wood.graphics_library_3d.input;

import com.nick.wood.game_control.input.Input;
import org.lwjgl.glfw.*;

public class GraphicsLibraryInput implements Input {

	private boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
	private boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
	private float mouseX;
	private float mouseY;
	private float offsetX;
	private float offsetY;

	private final GLFWKeyCallback keyboard;
	private final GLFWCursorPosCallback mouseMove;
	private final GLFWMouseButtonCallback mouseButton;
	private final GLFWScrollCallback glfwScrollCallback;

	public GraphicsLibraryInput() {
		keyboard = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int keyPressed, int scanCode, int action, int mods) {
				keys[keyPressed] = (action != GLFW.GLFW_RELEASE);
			}
		};

		mouseMove = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xPos, double yPos) {
				mouseX = (float) xPos;
				mouseY = (float) yPos;
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

	public float getMouseX() {
		return mouseX;
	}

	public float getMouseY() {
		return mouseY;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
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
