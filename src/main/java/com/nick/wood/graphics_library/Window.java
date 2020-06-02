package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.input.GraphicsLibraryInput;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.Scene;
import com.nick.wood.graphics_library.objects.scene_graph_objects.*;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.system.Callback;
import org.lwjgl.system.Configuration;
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
	private long window;
	private int WIDTH;
	private int HEIGHT;
	private String title;

	private Shader shader;
	private Shader hudShader;
	private Shader skyboxShader;
	private Renderer renderer;
	private final Matrix4f projectionMatrix;

	private boolean windowSizeChanged = false;

	public Window(int WIDTH, int HEIGHT, String title) {

		this.scene = new Scene();
		this.hudScene = new Scene();
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.title = title;

		this.graphicsLibraryInput = new GraphicsLibraryInput();

		this.projectionMatrix = Matrix4f.Projection((float) WIDTH / (float) HEIGHT, (float) Math.toRadians(70.0), 0.1f, 100000f);

	}

	public GraphicsLibraryInput getGraphicsLibraryInput() {
		return graphicsLibraryInput;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}

	public void init() {

		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		hudShader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		skyboxShader = new Shader("/shaders/skyboxVertex.glsl", "/shaders/skyboxFragment.glsl");

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

		// debug
		//glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

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

		// debug
		//Callback callback = GLUtil.setupDebugMessageCallback();


		// cull back faces
		GL11.glEnable(GLES20.GL_CULL_FACE);
		GL11.glCullFace(GLES20.GL_BACK);

		GL11.glEnable(GL_DEPTH_TEST);

		// support for transparencies
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// this locks cursor to center so can always look about
		GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		shader.create();
		skyboxShader.create();
		hudShader.create();

		this.scene.attachShader(shader);
		this.scene.attachSkyboxShader(skyboxShader);
		this.hudScene.attachShader(hudShader);

		this.renderer.init();

	}

	private void createCallbacks() {
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, graphicsLibraryInput.getKeyboard());
		glfwSetCursorPosCallback(window, graphicsLibraryInput.getMouseMove());
		glfwSetMouseButtonCallback(window, graphicsLibraryInput.getMouseButton());
		glfwSetScrollCallback(window, graphicsLibraryInput.getGlfwScrollCallback());
		glfwSetJoystickCallback(graphicsLibraryInput.getGlfwJoystickCallback());

		glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
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
			glfwSetWindowShouldClose(window, true);
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

		scene.setPrimaryCamera(primaryCamera);

		Iterator<Map.Entry<UUID, SceneGraph>> mainIterator = gameObjects.entrySet().iterator();

		while (mainIterator.hasNext()) {
			Map.Entry<UUID, SceneGraph> next = mainIterator.next();
			createRenderLists(scene, next.getValue(), Matrix4f.Identity);
			if (next.getValue().getSceneGraphNodeData().isDelete()) {
				mainIterator.remove();
			}
		}

		Iterator<Map.Entry<UUID, SceneGraph>> hudIterator = hudObjects.entrySet().iterator();

		while (hudIterator.hasNext()) {
			Map.Entry<UUID, SceneGraph> next = hudIterator.next();
			createRenderLists(hudScene, next.getValue(), Matrix4f.Identity);
			if (next.getValue().getSceneGraphNodeData().isDelete()) {
				hudIterator.remove();
			}
		}

		scene.render(renderer);
		// this makes sure hud is ontop of everything in scene
		glClear(GL_DEPTH_BUFFER_BIT);
		hudScene.render(renderer);
		glfwSwapBuffers(window); // swap the color buffers

	}

	private void createRenderLists(Scene scene, SceneGraphNode sceneGraphNode, Matrix4f transformationSoFar) {

		Iterator<SceneGraphNode> iterator = sceneGraphNode.getSceneGraphNodeData().getChildren().iterator();

		while (iterator.hasNext()) {

			SceneGraphNode child = iterator.next();

			if (child == null) {
				System.out.println();
			}

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
						if (scene.getMeshes().containsKey(meshGameObject.getMeshObject())) {
							InstanceObject meshInstance = new InstanceObject(child.getSceneGraphNodeData().getUuid(), transformationSoFar);
							scene.getMeshes().get(meshGameObject.getMeshObject()).add(meshInstance);
						} else {
							ArrayList<InstanceObject> meshObjects = new ArrayList<>();
							InstanceObject meshInstance = new InstanceObject(child.getSceneGraphNodeData().getUuid(), transformationSoFar);
							meshObjects.add(meshInstance);
							scene.getMeshes().put(meshGameObject.getMeshObject(), meshObjects);
							if (!meshGameObject.getMeshObject().getMesh().isCreated()) {
								meshGameObject.getMeshObject().getMesh().create();
							}
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

	public Shader getHudShader() {
		return hudShader;
	}

	@Override
	public void close() throws Exception {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		shader.destroy();
		hudShader.destroy();
		renderer.destroy();

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		GL.setCapabilities(null);
	}
}
