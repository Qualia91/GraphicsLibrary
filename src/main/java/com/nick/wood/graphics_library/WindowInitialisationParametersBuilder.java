package com.nick.wood.graphics_library;

public class WindowInitialisationParametersBuilder {

	// defaults
	private boolean resizable = true;
	private boolean decorated = true;
	private boolean lockCursor = false;
	private boolean fullScreen = false;
	private int windowWidth = 500;
	private int windowHeight = 500;
	private String title = "My First Window";
	private float near = 1f;
	private float far = 100f;
	private float fov = 1.22173f; // 70°

	public WindowInitialisationParameters build() {
		return new WindowInitialisationParameters(
				resizable,
				decorated,
				lockCursor,
				fullScreen,
				windowWidth,
				windowHeight,
				title,
				near,
				far,
				fov
		);
	}

	public WindowInitialisationParametersBuilder setFov(float fov) {
		this.fov = fov;
		return this;
	}

	public WindowInitialisationParametersBuilder setResizable(boolean resizable) {
		this.resizable = resizable;
		return this;
	}

	public WindowInitialisationParametersBuilder setDecorated(boolean decorated) {
		this.decorated = decorated;
		return this;
	}

	public WindowInitialisationParametersBuilder setLockCursor(boolean lockCursor) {
		this.lockCursor = lockCursor;
		return this;
	}

	public WindowInitialisationParametersBuilder setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
		return this;
	}

	public WindowInitialisationParametersBuilder setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
		return this;
	}

	public WindowInitialisationParametersBuilder setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
		return this;
	}

	public WindowInitialisationParametersBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public WindowInitialisationParametersBuilder setNear(float near) {
		this.near = near;
		return this;
	}

	public WindowInitialisationParametersBuilder setFar(float far) {
		this.far = far;
		return this;
	}
}
