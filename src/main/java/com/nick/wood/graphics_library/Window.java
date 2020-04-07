package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.game_objects.GameObject;
import com.nick.wood.graphics_library.input.Inputs;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.UUID;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

	private final Inputs input;
	private final Camera camera;
	// The window handle
	private long window;
	private int WIDTH;
	private int HEIGHT;
	private String title;

	private Shader shader;
	private Renderer renderer;
	private Matrix4d projectionMatrix;
	private double newMouseX, newMouseY;
	private double oldMouseX = 0;
	private double oldMouseY = 0;

	private boolean windowSizeChanged = false;

	HashMap<UUID, GameObject> gameObjects;
	UUID playerObjectUUID;

	public Window(int WIDTH, int HEIGHT, String title, HashMap<UUID, GameObject> gameObjects, Inputs input) {

		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.title = title;
		this.camera = new Camera(new Vec3d(-5, 0.0, 0.0),  new Vec3d(-90.0, 180.0, 90.0), 0.5, 0.1);

		gameObjects.forEach(
				(uuid, gameObject) -> {
					if (gameObject.isPlayer()) {
						this.camera.attachGameObject(gameObject);
						playerObjectUUID = uuid;
					}
				}
		);

		this.input = input;

		this.projectionMatrix = Matrix4d.Projection((double)WIDTH/(double)HEIGHT, Math.toRadians(70.0), 0.001, 1000);

		this.gameObjects = gameObjects;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}

	public void destroy() {

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		shader.destroy();

		for (GameObject gameObject : gameObjects.values()) {
			gameObject.getMeshGroup().getMeshObjectArray().forEach(meshObject -> meshObject.getMesh().destroy());
		}

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public void init() {

		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		renderer = new Renderer(this);


		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, title, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		createCallbacks();

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);


		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// cull back faces
		GL11.glEnable(GLES20.GL_CULL_FACE);
		GL11.glCullFace(GLES20.GL_BACK);

		GL11.glEnable(GL_DEPTH_TEST);

		// this locks cursor to center so can always look about
		GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		for (GameObject gameObject : gameObjects.values()) {
			gameObject.getMeshGroup().getMeshObjectArray().forEach(meshObject -> meshObject.getMesh().create());
		}

		shader.create();
	}

	private void createCallbacks() {
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, input.getKeyboard());
		glfwSetCursorPosCallback(window, input.getMouseMove());
		glfwSetMouseButtonCallback(window, input.getMouseButton());
		glfwSetScrollCallback(window, input.getGlfwScrollCallback());
		glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				WIDTH = width;
				HEIGHT = height;
				windowSizeChanged = true;
			}
		});

	}

	public void loop() {

		// user inputs
		if (input.isKeyPressed(GLFW_KEY_ESCAPE)) {
			glfwSetWindowShouldClose(window, true);
		}

		if (playerObjectUUID == null) {
			newMouseX = input.getMouseX();
			newMouseY = input.getMouseY();
			double dx = newMouseX - oldMouseX;
			double dy = newMouseY - oldMouseY;
			if (oldMouseX == 0 && oldMouseY == 0) {
				dx = 0.0;
				dy = 0.0;
			}
			oldMouseX = newMouseX;
			oldMouseY = newMouseY;

			camera.rotate(dx, dy);
			if (input.isKeyPressed(GLFW_KEY_A)) {
				camera.left();
			}
			if (input.isKeyPressed(GLFW_KEY_W)) {
				camera.forward();
			}
			if (input.isKeyPressed(GLFW_KEY_D)) {
				camera.right();
			}
			if (input.isKeyPressed(GLFW_KEY_S)) {
				camera.back();
			}
			if (input.isKeyPressed(GLFW_KEY_SPACE)) {
				camera.up();
			}
			if (input.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
				camera.down();
			}
		}

		if (windowSizeChanged) {
			glViewport(0, 0, WIDTH, HEIGHT);
			windowSizeChanged = false;
		}

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();

		for (GameObject gameObject : gameObjects.values()) {
			renderer.renderMesh(gameObject, camera);
		}

		glfwSwapBuffers(window); // swap the color buffers

	}

	public void setTitle(String title) {
		glfwSetWindowTitle(window, title);
	}

	public long getWindow() {
		return window;
	}

	public Matrix4d getProjectionMatrix() {
		return projectionMatrix;
	}

	public Shader getShader() {
		return shader;
	}

	public void updateDrawables(HashMap<UUID, GameObject> inputGameObject) {

		gameObjects = inputGameObject;

	}
}
