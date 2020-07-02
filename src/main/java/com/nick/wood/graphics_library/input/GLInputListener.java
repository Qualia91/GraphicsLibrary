package com.nick.wood.graphics_library.input;

import com.nick.wood.event_bus.interfaces.Bus;
import com.nick.wood.event_bus.event_data.MoveEventData;
import com.nick.wood.event_bus.event_data.PressEventData;
import com.nick.wood.event_bus.event_types.ControlEventType;
import com.nick.wood.event_bus.events.ControlEvent;
import org.lwjgl.glfw.*;

public class GLInputListener {

	private final GLFWKeyCallback keyboard;
	private final GLFWCursorPosCallback mouseMove;
	private final GLFWMouseButtonCallback mouseButton;
	private final GLFWScrollCallback glfwScrollCallback;

	public GLInputListener(Bus bus) {

		keyboard = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int keyPressed, int scanCode, int action, int mods) {
				// this if is so that it ignore the repeated press function
				if (action == 0 || action == 1) {
					bus.dispatch(new ControlEvent(ControlEventType.KEY, new PressEventData(keyPressed, action)));
				}
			}
		};

		mouseMove = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xPos, double yPos) {
				bus.dispatch(new ControlEvent(ControlEventType.MOUSE, new MoveEventData(xPos, yPos)));
			}
		};

		mouseButton = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int keyPressed, int action, int mods) {
				bus.dispatch(new ControlEvent(ControlEventType.MOUSE_BUTTON, new PressEventData(keyPressed, action)));
			}
		};

		glfwScrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				bus.dispatch(new ControlEvent(ControlEventType.SCROLL, new MoveEventData(xoffset, yoffset)));
			}
		};

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

	public GLFWScrollCallback getGlfwScrollCallback() {
		return glfwScrollCallback;
	}
}
