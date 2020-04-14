package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.game_objects.*;
import com.nick.wood.graphics_library.input.Inputs;
import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;
import com.nick.wood.graphics_library.objects.mesh_objects.SingleMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Matrix4f;
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
import java.util.*;
import java.util.function.Consumer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

	private final Inputs input;
	private Camera camera;
	// The window handle
	private long window;
	private int WIDTH;
	private int HEIGHT;
	private String title;

	private Shader shader;
	private Renderer renderer;
	private Matrix4f projectionMatrix;
	private double newMouseX, newMouseY;
	private double oldMouseX = 0;
	private double oldMouseY = 0;

	private boolean windowSizeChanged = false;

	HashMap<UUID, RootGameObject> gameObjects;
	UUID playerObjectUUID;

	// im hoping the entries that no longer exist will be removed when expungeStaleEntries() method is called within
	// weakhashmap. it will do this when it needs to call resize as the map has got too big.
	WeakHashMap<UUID, RenderObject<Light>> lights = new WeakHashMap<>();
	WeakHashMap<UUID, RenderObject<MeshObject>> meshes = new WeakHashMap<>();
	WeakHashMap<UUID, RenderObject<Camera>> cameras = new WeakHashMap<>();

	public Window(int WIDTH, int HEIGHT, String title, HashMap<UUID, RootGameObject> gameRootObjects, Inputs input) {

		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.title = title;

		for (Map.Entry<UUID, RootGameObject> uuidRootGameObjectEntry : gameRootObjects.entrySet()) {
			this.camera = getPrimaryCamera(uuidRootGameObjectEntry.getValue(), null);
			if (this.camera != null) {
				break;
			}
		}

		this.input = input;

		this.projectionMatrix = Matrix4f.Projection(WIDTH / HEIGHT, (float) Math.toRadians(70.0), 0.001f, 1000f);

		this.gameObjects = gameRootObjects;

		for (Map.Entry<UUID, RootGameObject> uuidRootGameObjectEntry : gameObjects.entrySet()) {
			createInitialRenderLists(lights, meshes, cameras, uuidRootGameObjectEntry.getValue(), Matrix4d.Identity);
		}

	}

	private Camera getPrimaryCamera(GameObjectNode gameObjectNode, Camera camera) {
		for (GameObjectNode child : gameObjectNode.getGameObjectNodeData().getChildren()) {
			if (child instanceof CameraGameObject) {
				CameraGameObject cameraGameObject = (CameraGameObject) child;
				if (cameraGameObject.getCameraType() == CameraType.PRIMARY) {
					return cameraGameObject.getCamera();
				}
			} else {
				Camera newCamera = getPrimaryCamera(child, camera);
				if (newCamera != null) {
					return newCamera;
				}
			}
		}
		return camera;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}

	public void destroy() {

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		shader.destroy();

		for (GameObjectNode gameObjectNode : gameObjects.values()) {
			actOnMeshes(gameObjectNode, Mesh::destroy);
		}

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void actOnMeshes(GameObjectNode gameObjectNode, Consumer<Mesh> meshFunction) {
		if (gameObjectNode instanceof MeshGameObject) {
			MeshGameObject meshGameObject = (MeshGameObject) gameObjectNode;
			meshFunction.accept(meshGameObject.getMeshObject().getMesh());
		}
		if (gameObjectNode.getGameObjectNodeData().containsMeshes()) {
			for (GameObjectNode child : gameObjectNode.getGameObjectNodeData().getChildren()) {
				actOnMeshes(child, meshFunction);
			}
		}
	}

	public void init() {

		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		renderer = new Renderer(this);


		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, title, NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		createCallbacks();

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
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

		for (GameObjectNode gameObjectNode : gameObjects.values()) {
			actOnMeshes(gameObjectNode, Mesh::create);
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

		// todo could only load thing that will be viewable by the camera
		// get all lights, meshes and cameras in the scene in arrays to pass to renderer.

		lights.clear();
		meshes.clear();
		cameras.clear();

		for (Map.Entry<UUID, RootGameObject> uuidRootGameObjectEntry : gameObjects.entrySet()) {
			createInitialRenderLists(lights, meshes, cameras, uuidRootGameObjectEntry.getValue(), Matrix4d.Identity);
		}

		renderer.renderMesh(meshes, cameras, lights);

		glfwSwapBuffers(window); // swap the color buffers

	}

	private void createInitialRenderLists(WeakHashMap<UUID, RenderObject<Light>> lights, WeakHashMap<UUID, RenderObject<MeshObject>> meshes, WeakHashMap<UUID, RenderObject<Camera>> cameras, GameObjectNode gameObjectNode, Matrix4d transformationSoFar) {


		if (isAvailableRenderData(gameObjectNode.getGameObjectNodeData())) {

			for (GameObjectNode child : gameObjectNode.getGameObjectNodeData().getChildren()) {

				switch (child.getGameObjectNodeData().getType()) {

					case TRANSFORM:
						TransformGameObject transformGameObject = (TransformGameObject) child;
						Matrix4d newTransformationSoFar = transformGameObject.getTransformForRender().multiply(transformationSoFar);
						createInitialRenderLists(lights, meshes, cameras, transformGameObject, newTransformationSoFar);
						break;
					case LIGHT:
						LightGameObject lightGameObject = (LightGameObject) child;
						RenderObject<Light> lightRenderObject = new RenderObject<>(lightGameObject.getLight(), transformationSoFar, child.getGameObjectNodeData().getUuid());
						lights.put(child.getGameObjectNodeData().getUuid(), lightRenderObject);
						createInitialRenderLists(lights, meshes, cameras, lightGameObject, transformationSoFar);
						break;
					case MESH:
						MeshGameObject meshGameObject = (MeshGameObject) child;
						RenderObject<MeshObject> meshGroupRenderObject = new RenderObject<>(meshGameObject.getMeshObject(), transformationSoFar, child.getGameObjectNodeData().getUuid());
						meshes.put(child.getGameObjectNodeData().getUuid(), meshGroupRenderObject);
						createInitialRenderLists(lights, meshes, cameras, meshGameObject, transformationSoFar);
						break;
					case CAMERA:
						CameraGameObject cameraGameObject = (CameraGameObject) child;
						RenderObject<Camera> cameraRenderObject = new RenderObject<>(cameraGameObject.getCamera(), transformationSoFar, child.getGameObjectNodeData().getUuid());
						cameras.put(child.getGameObjectNodeData().getUuid(), cameraRenderObject);
						createInitialRenderLists(lights, meshes, cameras, cameraGameObject, transformationSoFar);
						break;
					default:
						createInitialRenderLists(lights, meshes, cameras, child, transformationSoFar);
						break;

				}

			}

		}

	}

	private boolean isAvailableRenderData(GameObjectNodeData gameObjectNodeData) {
		return gameObjectNodeData.containsMeshes() || gameObjectNodeData.containsCameras() || gameObjectNodeData.containsLights();
	}

	public void setTitle(String title) {
		glfwSetWindowTitle(window, title);
	}

	public long getWindow() {
		return window;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Shader getShader() {
		return shader;
	}
}