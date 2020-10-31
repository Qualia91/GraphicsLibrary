package com.nick.wood.graphics_library;

import com.nick.wood.maths.objects.vector.Vec3f;

public class WindowInitialisationParametersBuilder {

	// defaults
	private boolean resizable = true;
	private boolean decorated = true;
	private boolean lockCursor = true;
	private boolean fullScreen = false;
	private int windowWidth = 800;
	private int windowHeight = 600;
	private String title = "Window";
	private boolean debug = false;
	private long cullStepSize = 100;

	public WindowInitialisationParameters build() {
		return new WindowInitialisationParameters(
				resizable,
				decorated,
				lockCursor,
				fullScreen,
				windowWidth,
				windowHeight,
				title,
				cullStepSize,
				debug
		);
	}

	public WindowInitialisationParametersBuilder setCullStepSize(long cullStepSize) {
		this.cullStepSize = cullStepSize;
		return this;
	}

	public WindowInitialisationParametersBuilder setDebug(boolean debug) {
		this.debug = debug;
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
}
