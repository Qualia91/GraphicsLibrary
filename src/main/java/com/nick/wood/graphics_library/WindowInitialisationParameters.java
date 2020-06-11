package com.nick.wood.graphics_library;

import com.nick.wood.maths.objects.matrix.Matrix4f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowInitialisationParameters {

	private final boolean resizable;
	private final boolean decorated;
	private final boolean lockCursor;
	private final boolean fullScreen;
	private final int windowWidth;
	private final int windowHeight;
	private final String title;
	private final float near;
	private final float far;
	private final float fov;

	public WindowInitialisationParameters(boolean resizable,
	                                      boolean decorated,
	                                      boolean lockCursor,
	                                      boolean fullScreen,
	                                      int windowWidth,
	                                      int windowHeight,
	                                      String title,
	                                      float near,
	                                      float far,
	                                      float fov) {
		this.resizable = resizable;
		this.decorated = decorated;
		this.lockCursor = lockCursor;
		this.fullScreen = fullScreen;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.title = title;
		this.near = near;
		this.far = far;
		this.fov = fov;
	}

	public long accept(Window window) {
		// the window will be resizable
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);

		// remove window bar
		glfwWindowHint(GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE);

		// Create the window
		long windowHandler = glfwCreateWindow(windowWidth, windowHeight, title, NULL, NULL);

		// this locks cursor to center so can always look about
		org.lwjgl.glfw.GLFW.glfwSetInputMode(windowHandler, GLFW_CURSOR, lockCursor ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);

		window.setFov(fov);
		window.setWIDTH(windowWidth);
		window.setHEIGHT(windowHeight);
		window.setTitle(title);

		window.setProjectionMatrix(Matrix4f.Projection((float) windowWidth / (float) windowHeight, fov, near, far));

		return windowHandler;
	}
}
