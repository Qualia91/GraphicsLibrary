package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.input.GraphicsLibraryInput;
import com.nick.wood.graphics_library.materials.TextureManager;
import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;
import com.nick.wood.graphics_library.objects.render_scene.Scene;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.system.Callback;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements AutoCloseable {

	// this map will contain all of the meshes created and be accessible via the stringtocompair variable
	// ion mesh. This will enable objects that have been unloaded then reloaded to use the old created ones
	// and it wont have to create new ones.
	private final HashMap<String, Mesh> createdMeshMap = new HashMap<>();

	private final GraphicsLibraryInput graphicsLibraryInput;
	private final ArrayList<Scene> sceneLayers;

	// The window handle
	private long windowHandler;

	private int width;
	private int height;
	private String title;

	private Renderer renderer;

	private boolean windowSizeChanged = false;
	private boolean titleChanged = false;

	private final TextureManager textureManager;

	public Window(ArrayList<Scene> sceneLayers) {
		this.sceneLayers = sceneLayers;
		this.graphicsLibraryInput = new GraphicsLibraryInput();
		this.textureManager = new TextureManager();
	}

	public GraphicsLibraryInput getGraphicsLibraryInput() {
		return graphicsLibraryInput;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowHandler);
	}

	public void init(WindowInitialisationParameters windowInitialisationParameters) throws IOException {

		renderer = new Renderer(this.textureManager);

		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default


		// window settings //
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation


		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

		// create window with init params
		windowHandler = windowInitialisationParameters.accept(this);

		if (windowHandler == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		createCallbacks();

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(windowHandler, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					windowHandler,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(windowHandler);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(windowHandler);


		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// debug
		if (windowInitialisationParameters.isDebug()) {
			Callback callback = GLUtil.setupDebugMessageCallback();
			glEnable(GL43.GL_DEBUG_OUTPUT);
			Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		}


		textureManager.create();

		// cull back faces
		GL11.glEnable(GLES20.GL_CULL_FACE);
		GL11.glCullFace(GLES20.GL_BACK);
		GL11.glEnable(GL_DEPTH_TEST);

		// support for transparencies
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		for (Scene sceneLayer : sceneLayers) {
			sceneLayer.init(width, height);
		}

		this.renderer.init();

	}

	private void createCallbacks() {
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(windowHandler, graphicsLibraryInput.getKeyboard());
		glfwSetCursorPosCallback(windowHandler, graphicsLibraryInput.getMouseMove());
		glfwSetMouseButtonCallback(windowHandler, graphicsLibraryInput.getMouseButton());
		glfwSetScrollCallback(windowHandler, graphicsLibraryInput.getGlfwScrollCallback());
		//glfwSetJoystickCallback(graphicsLibraryInput.getGlfwJoystickCallback());

		glfwSetWindowSizeCallback(windowHandler, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				Window.this.width = width;
				Window.this.height = height;
				windowSizeChanged = true;
			}
		});

	}

	public void loop(HashMap<String, RenderGraph> renderGraphs) {

		// user inputs
		if (graphicsLibraryInput.isKeyPressed(GLFW_KEY_ESCAPE)) {
			glfwSetWindowShouldClose(windowHandler, true);
		}

		if (windowSizeChanged) {
			glViewport(0, 0, width, height);
			windowSizeChanged = false;
			for (Scene sceneLayer : sceneLayers) {
				sceneLayer.updateScreen(width, height);
			}
		}

		if (titleChanged) {
			glfwSetWindowTitle(windowHandler, title);
			titleChanged = false;
		}

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();

		for (Scene sceneLayer : sceneLayers) {

			RenderGraph renderGraph = renderGraphs.get(sceneLayer.getName());

			if (renderGraph != null) {

				// destroy all the meshes that need to be destroyed
				for (Mesh mesh : renderGraph.getMeshesToDestroy()) {
					mesh.destroy();
				}

				// build all the meshes that are yet to be build
				for (Mesh mesh : renderGraph.getMeshesToBuild()) {
					mesh.create();
				}

				// clear the list
				renderGraph.getMeshesToBuild().clear();
				renderGraph.getMeshesToDestroy().clear();

				sceneLayer.render(renderer, renderGraph, textureManager);
				// this makes sure next scene is on top of last scene
				glClear(GL_DEPTH_BUFFER_BIT);
			}

		}

		glfwSwapBuffers(windowHandler); // swap the color buffers

	}

	public void setTitle(String title) {
		this.title = title;
		this.titleChanged = true;
	}

	public void setScreenDimensions(int width, int height) {
		this.width = width;
		this.height = height;
		for (Scene sceneLayer : sceneLayers) {
			sceneLayer.updateScreen(width, height);
		}
	}

	@Override
	public void close() throws Exception {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(windowHandler);
		glfwDestroyWindow(windowHandler);

		textureManager.destroy();


		for (Scene sceneLayer : sceneLayers) {
			sceneLayer.destroy();
		}
		renderer.destroy();

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		GL.setCapabilities(null);
	}

	public TextureManager getTextureManager() {
		return textureManager;
	}
}
