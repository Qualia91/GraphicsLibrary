package com.nick.wood.graphics_library;

import com.nick.wood.maths.objects.vector.Vec3f;

public class WindowInitialisationParametersBuilder {

	// defaults
	private boolean resizable = true;
	private boolean decorated = true;
	private boolean lockCursor = false;
	private boolean fullScreen = false;
	private int windowWidth = 1200;
	private int windowHeight = 800;
	private String title = "My First Window";
	private Vec3f sceneAmbientLight = new Vec3f(.1f, .1f, .1f);
	private Vec3f hudAmbientLight = new Vec3f(.9f, .9f, .9f);

	public WindowInitialisationParameters build() {
		return new WindowInitialisationParameters(
				resizable,
				decorated,
				lockCursor,
				fullScreen,
				windowWidth,
				windowHeight,
				title,
				sceneAmbientLight,
				hudAmbientLight
		);
	}

	public WindowInitialisationParametersBuilder setSceneAmbientLight(Vec3f sceneAmbientLight) {
		this.sceneAmbientLight = sceneAmbientLight;
		return this;
	}

	public WindowInitialisationParametersBuilder setHudAmbientLight(Vec3f hudAmbientLight) {
		this.hudAmbientLight = hudAmbientLight;
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
