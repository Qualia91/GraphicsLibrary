package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.frame_buffers.WaterFrameBuffer;
import com.nick.wood.graphics_library.input.GraphicsLibraryInput;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.Scene;
import com.nick.wood.graphics_library.objects.scene_graph_objects.*;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengles.GLES20;
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
	private Renderer renderer;
	private Matrix4f projectionMatrix;

	private boolean windowSizeChanged = false;
	private boolean titleChanged = false;

	private WaterFrameBuffer waterFrameBuffer;

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

		this.waterFrameBuffer = new WaterFrameBuffer(WIDTH, HEIGHT);

		this.scene.attachShader(shader);
		this.scene.attachSkyboxShader(skyboxShader);
		this.scene.attachWaterShader(waterShader);
		this.scene.addFrameBufferObject(waterFrameBuffer);
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

	public void loop(HashMap<UUID, SceneGraph> gameObjects, HashMap<UUID, SceneGraph> hudObjects, UUID primaryCamera) {

		// user inputs
		if (graphicsLibraryInput.isKeyPressed(GLFW_KEY_ESCAPE)) {
			glfwSetWindowShouldClose(windowHandler, true);
		}

		if (windowSizeChanged) {
			glViewport(0, 0, WIDTH, HEIGHT);
			windowSizeChanged = false;
			this.projectionMatrix = this.projectionMatrix.updateProjection((float) WIDTH / (float) HEIGHT, fov);
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

		Iterator<Map.Entry<UUID, SceneGraph>> mainIterator = gameObjects.entrySet().iterator();

		while (mainIterator.hasNext()) {
			Map.Entry<UUID, SceneGraph> next = mainIterator.next();
			createRenderLists(scene, next.getValue(), Matrix4f.Identity);
			if (next.getValue().getSceneGraphNodeData().isDelete()) {
				mainIterator.remove();
			}
		}

		scene.render(renderer, WIDTH, HEIGHT);
		// this makes sure hud is ontop of everything in scene
		glClear(GL_DEPTH_BUFFER_BIT);

		Iterator<Map.Entry<UUID, SceneGraph>> hudIterator = hudObjects.entrySet().iterator();

		while (hudIterator.hasNext()) {
			Map.Entry<UUID, SceneGraph> next = hudIterator.next();
			createRenderLists(hudScene, next.getValue(), Matrix4f.Identity);
			if (next.getValue().getSceneGraphNodeData().isDelete()) {
				hudIterator.remove();
			}
		}

		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : scene.getCameras().entrySet()) {
			if (cameraInstanceObjectEntry.getValue().getUuid().equals(primaryCamera)) {
				hudScene.getCameras().clear();
				hudScene.getCameras().put(cameraInstanceObjectEntry.getKey(), cameraInstanceObjectEntry.getValue());
			}
		}

		hudScene.render(renderer, WIDTH, HEIGHT);
		glfwSwapBuffers(windowHandler); // swap the color buffers

	}

	private void createRenderLists(Scene scene, SceneGraphNode sceneGraphNode, Matrix4f transformationSoFar) {

		Iterator<SceneGraphNode> iterator = sceneGraphNode.getSceneGraphNodeData().getChildren().iterator();

		while (iterator.hasNext()) {

			SceneGraphNode child = iterator.next();

			switch (child.getSceneGraphNodeData().getType()) {

				case TRANSFORM:
					if (child.getSceneGraphNodeData().isDelete()) {
						iterator.remove();
					} else {
						TransformSceneGraph transformGameObject = (TransformSceneGraph) child;
						createRenderLists(scene, transformGameObject, transformGameObject.getTransformForRender().multiply(transformationSoFar));
					}
					break;
				case LIGHT:
					if (child.getSceneGraphNodeData().isDelete()) {
						scene.removeLight(child.getSceneGraphNodeData().getUuid());
						iterator.remove();
					} else {
						LightSceneGraph lightGameObject = (LightSceneGraph) child;
						if (scene.getLights().containsKey(lightGameObject.getLight())) {
							scene.getLights().get(lightGameObject.getLight()).setTransformation(transformationSoFar);
						} else {
							InstanceObject lightInstance = new InstanceObject(child.getSceneGraphNodeData().getUuid(), transformationSoFar);
							scene.getLights().put(lightGameObject.getLight(), lightInstance);
						}
						createRenderLists(scene, lightGameObject, transformationSoFar);
					}
					break;
				case MESH:
					if (child.getSceneGraphNodeData().isDelete()) {
						scene.removeMesh(child.getSceneGraphNodeData().getUuid());
						iterator.remove();
					} else {
						MeshSceneGraph meshGameObject = (MeshSceneGraph) child;
						if (!meshGameObject.getMeshObject().getMesh().isCreated()) {
							meshGameObject.getMeshObject().getMesh().create();
						}
						if (scene.getMeshes().containsKey(meshGameObject.getMeshObject())) {
							InstanceObject meshInstance = new InstanceObject(child.getSceneGraphNodeData().getUuid(), transformationSoFar);
							scene.getMeshes().get(meshGameObject.getMeshObject()).add(meshInstance);
						} else {
							ArrayList<InstanceObject> meshObjects = new ArrayList<>();
							InstanceObject meshInstance = new InstanceObject(child.getSceneGraphNodeData().getUuid(), transformationSoFar);
							meshObjects.add(meshInstance);
							scene.getMeshes().put(meshGameObject.getMeshObject(), meshObjects);

						}
						createRenderLists(scene, meshGameObject, transformationSoFar);
					}
					break;
				case WATER:
					if (child.getSceneGraphNodeData().isDelete()) {
						scene.removeWater(child.getSceneGraphNodeData().getUuid());
						iterator.remove();
					} else {
						WaterSceneObject meshGameObject = (WaterSceneObject) child;
						if (!meshGameObject.getWater().getMesh().isCreated()) {
							meshGameObject.getWater().getMesh().create();
						}
						if (scene.getWaterMeshes().containsKey(meshGameObject.getWater())) {
							InstanceObject meshInstance = new InstanceObject(child.getSceneGraphNodeData().getUuid(), transformationSoFar);
							scene.getWaterMeshes().get(meshGameObject.getWater()).add(meshInstance);
						} else {
							ArrayList<InstanceObject> meshObjects = new ArrayList<>();
							InstanceObject meshInstance = new InstanceObject(child.getSceneGraphNodeData().getUuid(), transformationSoFar);
							meshObjects.add(meshInstance);
							scene.getWaterMeshes().put(meshGameObject.getWater(), meshObjects);

						}
						createRenderLists(scene, meshGameObject, transformationSoFar);
					}
					break;
				case SKYBOX:
					if (child.getSceneGraphNodeData().isDelete()) {
						scene.removeSkybox();
						iterator.remove();
					} else {
						SkyBox skyBox = (SkyBox) child;
						if (!skyBox.getSkybox().getMesh().isCreated()) {
							skyBox.getSkybox().getMesh().create();
						}
						scene.setSkybox(skyBox.getSkybox());
						createRenderLists(scene, skyBox, transformationSoFar);
					}
					break;
				case CAMERA:
					if (child.getSceneGraphNodeData().isDelete()) {
						scene.removeCamera(child.getSceneGraphNodeData().getUuid());
						iterator.remove();
					} else {
						CameraSceneGraph cameraGameObject = (CameraSceneGraph) child;
						if (scene.getCameras().containsKey(cameraGameObject.getCamera())) {
							scene.getCameras().get(cameraGameObject.getCamera()).setTransformation(transformationSoFar);
						} else {
							InstanceObject cameraInstance = new InstanceObject(child.getSceneGraphNodeData().getUuid(), transformationSoFar);
							scene.getCameras().put(cameraGameObject.getCamera(), cameraInstance);
						}
						createRenderLists(scene, cameraGameObject, transformationSoFar);
					}
					break;
				default:
					if (child.getSceneGraphNodeData().isDelete()) {
						iterator.remove();
					} else {
						createRenderLists(scene, child, transformationSoFar);
					}
					break;

			}

		}

	}

	private boolean isAvailableRenderData(SceneGraphNodeData sceneGraphNodeData) {
		return sceneGraphNodeData.containsMeshes() || sceneGraphNodeData.containsCameras() || sceneGraphNodeData.containsLights();
	}

	public void setTitle(String title) {
		this.title = title;
		this.titleChanged = true;
	}

	public long getWindowHandler() {
		return windowHandler;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Shader getShader() {
		return shader;
	}

	public Shader getHudShader() {
		return hudShader;
	}

	public void setWIDTH(int WIDTH) {
		this.WIDTH = WIDTH;
	}

	public void setHEIGHT(int HEIGHT) {
		this.HEIGHT = HEIGHT;
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


	public void setProjectionMatrix(Matrix4f projection) {
		this.projectionMatrix = projection;
	}
}
