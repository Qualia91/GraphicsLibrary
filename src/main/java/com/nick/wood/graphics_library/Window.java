package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.game_objects.*;
import com.nick.wood.graphics_library.input.Inputs;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshGroup;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Quaternion;
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

import java.lang.reflect.Array;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
	private Matrix4d projectionMatrix;
	private double newMouseX, newMouseY;
	private double oldMouseX = 0;
	private double oldMouseY = 0;

	private boolean windowSizeChanged = false;

	HashMap<UUID, RootGameObject> gameObjects;
	UUID playerObjectUUID;

	public Window(int WIDTH, int HEIGHT, String title, HashMap<UUID, RootGameObject> gameRootObjects, Inputs input) {

		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.title = title;


		gameRootObjects.forEach(
			(uuid, gameRootObject) -> {
				this.camera = getPrimaryCamera(gameRootObject, null);
			}
		);

		this.input = input;

		this.projectionMatrix = Matrix4d.Projection((double) WIDTH / (double) HEIGHT, Math.toRadians(70.0), 0.001, 1000);

		this.gameObjects = gameRootObjects;
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
			actOneMeshesMeshes(gameObjectNode, Mesh::destroy);
		}

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void actOneMeshesMeshes(GameObjectNode gameObjectNode, Consumer<Mesh> meshFunction) {
		if (gameObjectNode instanceof MeshGameObject) {
			MeshGameObject meshGameObject = (MeshGameObject) gameObjectNode;
			for (MeshObject meshObject : meshGameObject.getMeshGroup().getMeshObjectArray()) {
				meshFunction.accept(meshObject.getMesh());
			}
		}
		if (gameObjectNode.getGameObjectNodeData().containsMeshes()) {
			for (GameObjectNode child : gameObjectNode.getGameObjectNodeData().getChildren()) {
				actOneMeshesMeshes(child, meshFunction);
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
			actOneMeshesMeshes(gameObjectNode, Mesh::create);
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

		// todo this will be inefficient. could improve by using ids for everything and having render lists only update data
		// if the data in teh scene tree has been updated
		// todo could only load thing that will be viewable by the camera
		// get all lights, meshes and cameras in the scene in arrays to pass to renderer.
		HashMap<Light, ArrayList<Matrix4d>> lights = new HashMap<>();
		HashMap<MeshGroup, ArrayList<Matrix4d>> meshes = new HashMap<>();
		HashMap<Camera, ArrayList<Matrix4d>> cameras = new HashMap<>();

		for (RootGameObject rootGameObject : gameObjects.values()) {
			walkSceneTree(lights, meshes, cameras, rootGameObject, Matrix4d.Identity);
		}

		for (Map.Entry<MeshGroup, ArrayList<Matrix4d>> meshGroupArrayListEntry : meshes.entrySet()) {
			renderer.renderMesh(meshGroupArrayListEntry, cameras, lights);
		}

		glfwSwapBuffers(window); // swap the color buffers

	}

	private void walkSceneTree(HashMap<Light, ArrayList<Matrix4d>> lights, HashMap<MeshGroup, ArrayList<Matrix4d>> meshes, HashMap<Camera, ArrayList<Matrix4d>> cameras, GameObjectNode gameObjectNode, Matrix4d transformationSoFar) {


		if (isAvailableRenderData(gameObjectNode.getGameObjectNodeData())) {

			for (GameObjectNode child : gameObjectNode.getGameObjectNodeData().getChildren()) {

				switch (child.getGameObjectNodeData().getType()) {

					case TRANSFORM:
						TransformGameObject transformGameObject = (TransformGameObject) child;
						transformationSoFar = transformationSoFar.multiply(transformGameObject.getTransform().getTransform());
						walkSceneTree(lights, meshes, cameras, transformGameObject, Matrix4d.Identity);
						break;
					case LIGHT:
						LightGameObject lightGameObject = (LightGameObject) child;
						if (lights.containsKey(lightGameObject.getLight())) {
							lights.get(lightGameObject.getLight()).add(transformationSoFar);
						} else {
							ArrayList<Matrix4d> matrix4ds = new ArrayList<>();
							matrix4ds.add(transformationSoFar);
							lights.put(lightGameObject.getLight(), matrix4ds);
						}
						walkSceneTree(lights, meshes, cameras, lightGameObject, transformationSoFar);
						break;
					case MESH:
						MeshGameObject meshGameObject = (MeshGameObject) child;
						if (meshes.containsKey(meshGameObject.getMeshGroup())) {
							meshes.get(meshGameObject.getMeshGroup()).add(transformationSoFar);
						} else {
							ArrayList<Matrix4d> matrix4ds = new ArrayList<>();
							matrix4ds.add(transformationSoFar);
							meshes.put(meshGameObject.getMeshGroup(), matrix4ds);
						}
						walkSceneTree(lights, meshes, cameras, meshGameObject, transformationSoFar);
						break;
					case CAMERA:
						CameraGameObject cameraGameObject = (CameraGameObject) child;
						if (cameras.containsKey(cameraGameObject.getCamera())) {
							cameras.get(cameraGameObject.getCamera()).add(transformationSoFar);
						} else {
							ArrayList<Matrix4d> matrix4ds = new ArrayList<>();
							matrix4ds.add(transformationSoFar);
							cameras.put(cameraGameObject.getCamera(), matrix4ds);
						}
						walkSceneTree(lights, meshes, cameras, cameraGameObject, transformationSoFar);
						break;
					default:
						walkSceneTree(lights, meshes, cameras, child, transformationSoFar);
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

	public Matrix4d getProjectionMatrix() {
		return projectionMatrix;
	}

	public Shader getShader() {
		return shader;
	}
}
