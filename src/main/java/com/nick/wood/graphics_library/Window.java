package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.frame_buffers.PickingFrameBuffer;
import com.nick.wood.graphics_library.frame_buffers.SceneFrameBuffer;
import com.nick.wood.graphics_library.frame_buffers.WaterFrameBuffer;
import com.nick.wood.graphics_library.input.GraphicsLibraryInput;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.Scene;
import com.nick.wood.graphics_library.objects.game_objects.*;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetJoystickCallback;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements AutoCloseable {

	private final GraphicsLibraryInput graphicsLibraryInput;
	private final Scene scene;
	private final Scene hudScene;
	// The window handle
	private long windowHandler;

	private int WIDTH;
	private int HEIGHT;
	private float fov;
	private String title;

	private Shader shader;
	private Shader hudShader;
	private Shader skyboxShader;
	private Shader waterShader;
	private Shader pickingShader;
	private Renderer renderer;

	private boolean windowSizeChanged = false;
	private boolean titleChanged = false;

	private WaterFrameBuffer waterFrameBuffer;
	private SceneFrameBuffer sceneFrameBuffer;
	private PickingFrameBuffer pickingFrameBuffer;

	public Window() {
		this.scene = new Scene();
		this.hudScene = new Scene();
		this.graphicsLibraryInput = new GraphicsLibraryInput();
	}

	public Scene getScene() {
		return scene;
	}

	public Scene getHudScene() {
		return hudScene;
	}

	public GraphicsLibraryInput getGraphicsLibraryInput() {
		return graphicsLibraryInput;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowHandler);
	}

	public void init(WindowInitialisationParameters windowInitialisationParameters) {

		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		hudShader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		skyboxShader = new Shader("/shaders/skyboxVertex.glsl", "/shaders/skyboxFragment.glsl");
		waterShader = new Shader("/shaders/waterVertex.glsl", "/shaders/waterFragment.glsl");
		pickingShader = new Shader("/shaders/simpleVertex.glsl", "/shaders/simpleFragment.glsl");

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


		// window settings //
		// debug
		//glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation

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
		//Callback callback = GLUtil.setupDebugMessageCallback();


		// cull back faces
		GL11.glEnable(GLES20.GL_CULL_FACE);
		GL11.glCullFace(GLES20.GL_BACK);
		GL11.glEnable(GL_DEPTH_TEST);

		// support for transparencies
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		shader.create();
		skyboxShader.create();
		hudShader.create();
		waterShader.create();
		pickingShader.create();

		this.waterFrameBuffer = new WaterFrameBuffer(WIDTH, HEIGHT);
		this.sceneFrameBuffer = new SceneFrameBuffer(2048);
		this.pickingFrameBuffer = new PickingFrameBuffer(WIDTH, HEIGHT);

		GL11.glViewport(0, 0, WIDTH, HEIGHT);

		this.scene.attachShader(shader);
		this.scene.attachSkyboxShader(skyboxShader);
		this.scene.attachWaterShader(waterShader);
		this.scene.attachPickingShader(pickingShader);
		this.scene.setWaterFrameBufferObject(waterFrameBuffer);
		this.scene.setSceneFrameBufferObject(sceneFrameBuffer);
		this.scene.setPickingFrameBufferObject(pickingFrameBuffer);
		this.hudScene.attachShader(hudShader);

		hudScene.setAmbientLight(new Vec3f(0.8f, 0.8f, 0.8f));

		this.renderer.init();

	}

	private void createCallbacks() {
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(windowHandler, graphicsLibraryInput.getKeyboard());
		glfwSetCursorPosCallback(windowHandler, graphicsLibraryInput.getMouseMove());
		glfwSetMouseButtonCallback(windowHandler, graphicsLibraryInput.getMouseButton());
		glfwSetScrollCallback(windowHandler, graphicsLibraryInput.getGlfwScrollCallback());
		glfwSetJoystickCallback(graphicsLibraryInput.getGlfwJoystickCallback());

		glfwSetWindowSizeCallback(windowHandler, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				WIDTH = width;
				HEIGHT = height;
				windowSizeChanged = true;
			}
		});

	}

	public void loop(ArrayList<GameObject> gameObjects, ArrayList<GameObject> hudObjects, UUID primaryCamera) {

		// user inputs
		if (graphicsLibraryInput.isKeyPressed(GLFW_KEY_ESCAPE)) {
			glfwSetWindowShouldClose(windowHandler, true);
		}

		if (windowSizeChanged) {
			glViewport(0, 0, WIDTH, HEIGHT);
			windowSizeChanged = false;
			scene.updateScreen(WIDTH, HEIGHT);
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

		scene.setPrimaryCamera(primaryCamera);
		hudScene.setPrimaryCamera(primaryCamera);

		Iterator<GameObject> mainIterator = gameObjects.iterator();

		while (mainIterator.hasNext()) {
			GameObject next = mainIterator.next();
			createRenderLists(scene, next, Matrix4f.Identity);
			if (next.getGameObjectData().isDelete()) {
				mainIterator.remove();
			}
		}

		scene.render(renderer);
		// this makes sure hud is ontop of everything in scene
		glClear(GL_DEPTH_BUFFER_BIT);

		Iterator<GameObject> hudIterator = hudObjects.iterator();

		while (hudIterator.hasNext()) {
			GameObject next = hudIterator.next();
			createRenderLists(hudScene, next, Matrix4f.Identity);
			if (next.getGameObjectData().isDelete()) {
				hudIterator.remove();
			}
		}

		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : scene.getCameras().entrySet()) {
			if (cameraInstanceObjectEntry.getValue().getUuid().equals(primaryCamera)) {
				hudScene.getCameras().clear();
				hudScene.getCameras().put(cameraInstanceObjectEntry.getKey(), cameraInstanceObjectEntry.getValue());
			}
		}

		hudScene.render(renderer);
		glfwSwapBuffers(windowHandler); // swap the color buffers


	}

	private void createRenderLists(Scene scene, GameObject gameObject, Matrix4f transformationSoFar) {

		Iterator<GameObject> iterator = gameObject.getGameObjectData().getChildren().iterator();

		while (iterator.hasNext()) {

			GameObject child = iterator.next();

			switch (child.getGameObjectData().getType()) {

				case TRANSFORM:
					if (child.getGameObjectData().isDelete()) {
						iterator.remove();
					} else {
						TransformObject transformGameObject = (TransformObject) child;
						createRenderLists(scene, transformGameObject, transformGameObject.getTransformForRender().multiply(transformationSoFar));
					}
					break;
				case LIGHT:
					if (child.getGameObjectData().isDelete()) {
						scene.removeLight(child.getGameObjectData().getUuid());
						iterator.remove();
					} else {
						LightObject lightObject = (LightObject) child;
						if (scene.getLights().containsKey(lightObject.getLight())) {
							scene.getLights().get(lightObject.getLight()).setTransformation(transformationSoFar);
						} else {
							InstanceObject lightInstance = new InstanceObject(child.getGameObjectData().getUuid(), transformationSoFar);
							scene.getLights().put(lightObject.getLight(), lightInstance);
						}
						createRenderLists(scene, lightObject, transformationSoFar);
					}
					break;
				case MESH:
					if (child.getGameObjectData().isDelete()) {
						scene.removeMesh(child.getGameObjectData().getUuid());
						iterator.remove();
					} else {
						MeshGameObject meshGameObject = (MeshGameObject) child;
						if (!meshGameObject.getMeshObject().getMesh().isCreated()) {
							meshGameObject.getMeshObject().getMesh().create();
						}
						boolean found = false;
						for (Map.Entry<com.nick.wood.graphics_library.objects.mesh_objects.MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry : scene.getMeshes().entrySet()) {
							if (meshObjectArrayListEntry.getKey().getStringToCompare().equals(meshGameObject.getMeshObject().getStringToCompare())) {
								InstanceObject meshInstance = new InstanceObject(child.getGameObjectData().getUuid(), transformationSoFar);
								meshObjectArrayListEntry.getValue().add(meshInstance);
								found = true;
								break;
							}
						}
						if (!found) {
							ArrayList<InstanceObject> meshObjects = new ArrayList<>();
							InstanceObject meshInstance = new InstanceObject(child.getGameObjectData().getUuid(), transformationSoFar);
							meshObjects.add(meshInstance);
							scene.getMeshes().put(meshGameObject.getMeshObject(), meshObjects);

						}
						createRenderLists(scene, meshGameObject, transformationSoFar);
					}
					break;
				case WATER:
					if (child.getGameObjectData().isDelete()) {
						scene.removeWater(child.getGameObjectData().getUuid());
						iterator.remove();
					} else {
						WaterObject meshGameObject = (WaterObject) child;
						if (!meshGameObject.getWater().getMesh().isCreated()) {
							meshGameObject.getWater().getMesh().create();
						}
						if (scene.getWaterMeshes().containsKey(meshGameObject.getWater())) {
							InstanceObject meshInstance = new InstanceObject(child.getGameObjectData().getUuid(), transformationSoFar);
							scene.getWaterMeshes().get(meshGameObject.getWater()).add(meshInstance);
						} else {
							ArrayList<InstanceObject> meshObjects = new ArrayList<>();
							InstanceObject meshInstance = new InstanceObject(child.getGameObjectData().getUuid(), transformationSoFar);
							meshObjects.add(meshInstance);
							scene.getWaterMeshes().put(meshGameObject.getWater(), meshObjects);

						}
						createRenderLists(scene, meshGameObject, transformationSoFar);
					}
					break;
				case SKYBOX:
					if (child.getGameObjectData().isDelete()) {
						scene.removeSkybox();
						iterator.remove();
					} else {
						SkyBoxObject skyBoxObject = (SkyBoxObject) child;
						if (!skyBoxObject.getSkybox().getMesh().isCreated()) {
							skyBoxObject.getSkybox().getMesh().create();
						}
						scene.setSkybox(skyBoxObject.getSkybox());
						createRenderLists(scene, skyBoxObject, transformationSoFar);
					}
					break;
				case CAMERA:
					if (child.getGameObjectData().isDelete()) {
						scene.removeCamera(child.getGameObjectData().getUuid());
						iterator.remove();
					} else {
						CameraObject cameraObject = (CameraObject) child;
						if (scene.getCameras().containsKey(cameraObject.getCamera())) {
							scene.getCameras().get(cameraObject.getCamera()).setTransformation(transformationSoFar);
						} else {
							InstanceObject cameraInstance = new InstanceObject(child.getGameObjectData().getUuid(), transformationSoFar);
							scene.getCameras().put(cameraObject.getCamera(), cameraInstance);
						}
						createRenderLists(scene, cameraObject, transformationSoFar);
					}
					break;
				default:
					if (child.getGameObjectData().isDelete()) {
						iterator.remove();
					} else {
						createRenderLists(scene, child, transformationSoFar);
					}
					break;

			}

		}

	}

	public void setTitle(String title) {
		this.title = title;
		this.titleChanged = true;
	}

	public long getWindowHandler() {
		return windowHandler;
	}

	public Shader getShader() {
		return shader;
	}

	public Shader getHudShader() {
		return hudShader;
	}

	public void setScreenDimensions(int WIDTH, int HEIGHT) {
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		scene.updateScreen(this.WIDTH, this.HEIGHT);
	}

	public void setFov(float fov) {
		this.fov = fov;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public void close() throws Exception {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(windowHandler);
		glfwDestroyWindow(windowHandler);

		waterFrameBuffer.destroy();

		shader.destroy();
		hudShader.destroy();
		skyboxShader.destroy();
		waterShader.destroy();
		renderer.destroy();

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		GL.setCapabilities(null);
	}

	public void setAmbientLight(Vec3f ambientLight) {
		scene.setAmbientLight(ambientLight);
	}

	public void setAmbientHudLight(Vec3f ambientLight) {
		hudScene.setAmbientLight(ambientLight);
	}

	public int getWIDTH() {
		return WIDTH;
	}

	public int getHEIGHT() {
		return HEIGHT;
	}
}
