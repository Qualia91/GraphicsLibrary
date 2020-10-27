package com.nick.wood.graphics_library;

import com.nick.wood.game_engine.event_bus.event_types.ManagementEventType;
import com.nick.wood.game_engine.event_bus.events.ManagementEvent;
import com.nick.wood.game_engine.event_bus.events.RenderEvent;
import com.nick.wood.game_engine.event_bus.interfaces.Bus;
import com.nick.wood.game_engine.event_bus.interfaces.Event;
import com.nick.wood.game_engine.event_bus.interfaces.Subscribable;
import com.nick.wood.graphics_library.input.GLInputListener;
import com.nick.wood.graphics_library.logging.Logger;
import com.nick.wood.graphics_library.logging.Stats;
import com.nick.wood.graphics_library.logging.StatsCalc;
import com.nick.wood.graphics_library.materials.Material;
import com.nick.wood.graphics_library.materials.TextureManager;
import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.Terrain;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;
import com.nick.wood.graphics_library.objects.render_scene.Scene;
import com.nick.wood.maths.objects.vector.Vec3f;
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
import java.util.concurrent.ArrayBlockingQueue;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Subscribable {

	private Logger LOGGER = new Logger();
	private ArrayList<Stats> STATS_array = new ArrayList<>();

	private final Set<Class<?>> supports = new HashSet<>();
	private final ArrayBlockingQueue<ManagementEvent> managementEvents = new ArrayBlockingQueue<>(10);
	private final ArrayBlockingQueue<RenderEvent> renderEvents = new ArrayBlockingQueue<>(100);

	private final ArrayList<ManagementEvent> drainedEventList = new ArrayList<>(10);
	private final ArrayList<RenderEvent> drainedRenderEventList = new ArrayList<>(100);

	private final GLInputListener graphicsLibraryInput;
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

	private final HashMap<String, RenderGraph> renderGraphs;
	private final HashMap<String, MeshInstanceCounter> builtMeshes = new HashMap<>();
	private long cullStepSize;

	public Window(ArrayList<Scene> sceneLayers, Bus bus) {
		this.sceneLayers = sceneLayers;
		this.graphicsLibraryInput = new GLInputListener(bus);
		this.textureManager = new TextureManager();
		renderGraphs = new HashMap<>();
		for (Scene sceneLayer : sceneLayers) {
			renderGraphs.put(sceneLayer.getName(), null);
			if (sceneLayer.getPickingShader() != null) {
				bus.register(new Picking(bus, sceneLayer, renderGraphs));
			}
		}
		this.supports.add(ManagementEvent.class);
		this.supports.add(RenderEvent.class);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowHandler);
	}

	public void init(WindowInitialisationParameters windowInitialisationParameters) throws IOException {

		cullStepSize = windowInitialisationParameters.getCullStepSize();

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

		glfwSetWindowSizeCallback(windowHandler, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				Window.this.width = width;
				Window.this.height = height;
				windowSizeChanged = true;
			}
		});

	}

	public void render(long step) {

		Stats STATS = new Stats();
//		LOGGER.getStringBuilder().append("Start step ").append(step).append("\n");
//		LOGGER.getStringBuilder().append("Time: ").append(System.currentTimeMillis()).append("\n");
		STATS.start(System.currentTimeMillis());
		// deal with events
		managementEvents.drainTo(drainedEventList);

		for (ManagementEvent event : drainedEventList) {
			if (event.getType().equals(ManagementEventType.SHUTDOWN)) {
				glfwSetWindowShouldClose(windowHandler, true);
				return;
			}
		}

		drainedEventList.clear();

		// deal with events
		renderEvents.drainTo(drainedRenderEventList);

//		if (!drainedRenderEventList.isEmpty()) {
//			LOGGER.getStringBuilder().append("Getting render events, amount: ").append(drainedRenderEventList.size()).append("\n");
//			LOGGER.getStringBuilder().append("Time: ").append(System.currentTimeMillis()).append("\n");
//		}
		STATS.beginGetRenderEvent(System.currentTimeMillis());
		for (RenderEvent renderEvent : drainedRenderEventList) {
			RenderEventData data = (RenderEventData) renderEvent.getData();
			if (renderGraphs.containsKey(data.getLayerName())) {
				if (renderGraphs.get(data.getLayerName()) == null || data.getStep() > renderGraphs.get(data.getLayerName()).getStep()) {
					renderGraphs.put(data.getLayerName(), data.getRenderGraph());
				}
			} else {
				renderGraphs.put(data.getLayerName(), data.getRenderGraph());
			}
		}
//		LOGGER.getStringBuilder().append("Getting render events, finished").append("\n");
//		LOGGER.getStringBuilder().append("Time: ").append(System.currentTimeMillis()).append("\n");
		STATS.endGetRenderEvent(System.currentTimeMillis());

		drainedRenderEventList.clear();

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

//		LOGGER.getStringBuilder().append("Rendering").append("\n");
//		LOGGER.getStringBuilder().append("Time: ").append(System.currentTimeMillis()).append("\n");
		STATS.beginRender(System.currentTimeMillis());

		for (Scene sceneLayer : sceneLayers) {

			RenderGraph renderGraph = this.renderGraphs.get(sceneLayer.getName());

			if (renderGraph != null) {

				construct(renderGraph, step);

				sceneLayer.render(renderer, renderGraph, textureManager, step);
				// this makes sure next scene is on top of last scene
				glClear(GL_DEPTH_BUFFER_BIT);
			}

		}

//		LOGGER.getStringBuilder().append("Rendering finished").append("\n");
//		LOGGER.getStringBuilder().append("Time: ").append(System.currentTimeMillis()).append("\n");
		STATS.endRender(System.currentTimeMillis());

		glfwSwapBuffers(windowHandler); // swap the color buffers

		// sort out build meshes
		Iterator<Map.Entry<String, MeshInstanceCounter>> iterator = builtMeshes.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, MeshInstanceCounter> next = iterator.next();
			if (step - next.getValue().getStepLastSeen() > cullStepSize) {
				next.getValue().getMesh().destroy();
				iterator.remove();
			}
		}

//		LOGGER.getStringBuilder().append("Loop finished").append("\n");
//		LOGGER.getStringBuilder().append("Time: ").append(System.currentTimeMillis()).append("\n");
		STATS.finish(System.currentTimeMillis());

//		LOGGER.finish();
		STATS_array.add(STATS);

	}

	private void construct(RenderGraph renderGraph, long step) {
		construct(renderGraph.getMeshes(), step);
		construct(renderGraph.getWaterMeshes(), step);

		for (Map.Entry<Terrain, InstanceObject> terrainInstanceObjectEntry : renderGraph.getTerrainMeshes().entrySet()) {
			if (!builtMeshes.containsKey(terrainInstanceObjectEntry.getKey().getStringToCompare())) {
				builtMeshes.put(terrainInstanceObjectEntry.getKey().getStringToCompare(), new MeshInstanceCounter(terrainInstanceObjectEntry.getKey().getMesh(), step));
				System.out.println("Building terrain mesh");
			} else {
				builtMeshes.get(terrainInstanceObjectEntry.getKey().getStringToCompare()).seen(step);
			}
		}

		if (renderGraph.getSkybox() != null) {
			if (!builtMeshes.containsKey(renderGraph.getSkybox().getStringToCompare())) {
				builtMeshes.put(renderGraph.getSkybox().getStringToCompare(), new MeshInstanceCounter(renderGraph.getSkybox().getMesh(), step));
			} else {
				builtMeshes.get(renderGraph.getSkybox().getStringToCompare()).seen(step);
			}
		}
	}

	private void construct(HashMap<MeshObject, ArrayList<InstanceObject>> meshObjs, long step) {
		for (MeshObject meshObject : meshObjs.keySet()) {
			if (!builtMeshes.containsKey(meshObject.getStringToCompare())) {
				builtMeshes.put(meshObject.getStringToCompare(), new MeshInstanceCounter(meshObject.getMesh(), step));
			} else {
				builtMeshes.get(meshObject.getStringToCompare()).seen(step);
			}
		}
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

	public void close() {

		StatsCalc statsCalc = new StatsCalc(STATS_array);

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

	@Override
	public void handle(Event<?> event) {
		if (event instanceof ManagementEvent) {
			managementEvents.offer((ManagementEvent) event);
		} else if (event instanceof RenderEvent) {
			renderEvents.offer((RenderEvent) event);
		}
	}

	@Override
	public boolean supports(Class<? extends Event> aClass) {
		return supports.contains(aClass);
	}
}
