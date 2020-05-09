package com.nick.wood.graphics_library_3d;

import com.nick.wood.graphics_library_3d.input.DirectCameraController;
import com.nick.wood.graphics_library_3d.input.DirectTransformController;
import com.nick.wood.graphics_library_3d.input.LWJGLGameControlManager;
import com.nick.wood.graphics_library_3d.input.GraphicsLibraryInput;
import com.nick.wood.graphics_library_3d.lighting.DirectionalLight;
import com.nick.wood.graphics_library_3d.lighting.Light;
import com.nick.wood.graphics_library_3d.lighting.PointLight;
import com.nick.wood.graphics_library_3d.lighting.SpotLight;
import com.nick.wood.graphics_library_3d.objects.Camera;
import com.nick.wood.graphics_library_3d.objects.scene_graph_objects.*;
import com.nick.wood.graphics_library_3d.objects.Transform;
import com.nick.wood.graphics_library_3d.objects.mesh_objects.*;
import com.nick.wood.maths.noise.Perlin2D;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

class TestBench {

	@Test
	public void shadow() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		Transform transform = new Transform(
				Vec3f.X.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

		//MeshObject cubeMesh = new ModelMesh(
		//		"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj",
		//		"/textures/white.png",
		//		Matrix4f.Rotation(-90, Vec3f.X),
		//		false
		//);

		MeshObject cubeMesh = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.build();

		Transform transformMesh = new Transform(
				Vec3f.Z.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//.multiply(Matrix4f.Rotation(90, Vec3f.Y))
				//.multiply(Matrix4f.Rotation(90, Vec3f.Z))
		);
		TransformSceneGraph meshTransform = new TransformSceneGraph(wholeSceneTransform, transformMesh);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				meshTransform,
				cubeMesh
		);


		Transform transformMeshWall = new Transform(
				Vec3f.Y.scale(10),
				new Vec3f(10.0f, 10f,  10.0f),
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//.multiply(Matrix4f.Rotation(90, Vec3f.Y))
				//.multiply(Matrix4f.Rotation(90, Vec3f.Z))
		);
		TransformSceneGraph meshTransformWall = new TransformSceneGraph(wholeSceneTransform, transformMeshWall);
		MeshSceneGraph meshGameObjectWall = new MeshSceneGraph(
				meshTransformWall,
				cubeMesh
		);

		createAxis(wholeSceneTransform);

		PointLight pointLight = new PointLight(
				new Vec3f(0.0f, 1.0f, 0.0f),
				1f);
		DirectionalLight directionalLight = new DirectionalLight(
				new Vec3f(1.0f, 1.0f, 1.0f),
				new Vec3f(0.0f, 0.0f, -1.0f),
				0.1f);
		SpotLight spotLight = new SpotLight(
				new PointLight(
						new Vec3f(1.0f, 0.0f, 0.0f),
						100f),
				Vec3f.Y,
				0.02f
		);

		MeshObject sphereMesh = new MeshBuilder()
				.setTexture("/textures/sand.jpg")
				.setInvertedNormals(true)
				.build();

		createLight(pointLight, wholeSceneTransform, new Vec3f(-10.0f, 0.0f, 0.0f), Vec3f.ONE, Matrix4f.Identity, sphereMesh);
		createLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -15.0f, 0.0f), Vec3f.ONE, Matrix4f.Identity, sphereMesh);
		createLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -10), Vec3f.ONE, Matrix4f.Identity, sphereMesh);

		Camera camera = new Camera(new Vec3f(-10.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new Transform(
				Vec3f.X.scale(-10),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(wholeSceneTransform, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		gameObjects.put(UUID.randomUUID(), rootGameObject);

		Window window = new Window(
				1200,
				800,
				"");

		window.init();

		while (!window.shouldClose()) {

			window.loop(gameObjects, new HashMap<>(), cameraTransformGameObject.getSceneGraphNodeData().getUuid());

		}

		window.destroy();

	}

	@Test
	public void stress() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		Transform transform = new Transform(
				Vec3f.X.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);


		MeshObject meshGroup = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setModelFile("D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\dragon.obj")
				.setTexture("/textures/white.png")
				.setTransform(Matrix4f.Rotation(-90, Vec3f.X))
				.build();

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

		for (int i = 0; i < 1500; i++) {
			createObject(Vec3f.Y.scale(i), wholeSceneTransform, meshGroup);
		}


		createAxis(wholeSceneTransform);

		MeshObject meshGroupLight = new MeshBuilder()
				.setInvertedNormals(true)
				.build();

		PointLight pointLight = new PointLight(
				new Vec3f(0.0f, 1.0f, 0.0f),
				10f);
		DirectionalLight directionalLight = new DirectionalLight(
				new Vec3f(1.0f, 1.0f, 1.0f),
				new Vec3f(0.0f, 0.0f, -1.0f),
				0.1f);
		SpotLight spotLight = new SpotLight(
				new PointLight(
						new Vec3f(1.0f, 0.0f, 0.0f),
						100f),
				Vec3f.Y,
				0.1f
		);

		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -10f), Vec3f.ONE.scale(0.5f), Matrix4f.Identity, meshGroupLight);
		createLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0.0f), Vec3f.ONE.scale(0.5f), Matrix4f.Rotation(0.0f, Vec3f.Y), meshGroupLight);
		createLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0), Vec3f.ONE.scale(0.5f), Matrix4f.Identity, meshGroupLight);

		Camera camera = new Camera(new Vec3f(0.0f, 0.0f, 10.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(wholeSceneTransform, camera, CameraType.PRIMARY);

		gameObjects.put(UUID.randomUUID(), rootGameObject);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);


		Window window = new Window(
				1200,
				800,
				"");

		LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

		window.init();

		long oldTime = System.currentTimeMillis();

		while (!window.shouldClose()) {

			window.loop(gameObjects, new HashMap<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

			LWJGLGameControlManager.checkInputs();

			long currentTime = System.currentTimeMillis();

			window.setTitle("Diff Time: " + (currentTime - oldTime));

			oldTime = currentTime;

		}

		window.destroy();

	}

	private void createObject(Vec3f pos, SceneGraphNode parent, MeshObject meshGroup) {

		Transform transformMesh = new Transform(
				pos,
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//.multiply(Matrix4f.Rotation(90, Vec3f.Y))
				//.multiply(Matrix4f.Rotation(90, Vec3f.Z))
		);
		TransformSceneGraph meshTransform = new TransformSceneGraph(parent, transformMesh);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				meshTransform,
				meshGroup
		);
	}

	@Test void terrain() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		Perlin2D perlin2D = new Perlin2D(5000);
		int size = 1000;
		double[][] grid = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j] = perlin2D.getPoint(i/50.0, j/50.0) * 50.0;
			}
		}

		MeshObject terrain = new MeshBuilder()
				.setMeshType(MeshType.TERRAIN)
				.setTerrainHeightMap(grid)
				.build();

		MeshSceneGraph meshSceneGraph = new MeshSceneGraph(rootGameObject, terrain);

		MeshObject meshGroupLight = new MeshBuilder()
				.setInvertedNormals(true)
				.build();

		PointLight pointLightGreen = new PointLight(
				new Vec3f(0.0f, 1.0f, 0.0f),
				100f);

		PointLight pointLightRed = new PointLight(
				new Vec3f(1.0f, 0.0f, 0.0f),
				100f);

		PointLight pointLightBlue = new PointLight(
				new Vec3f(0.0f, 0.0f, 1.0f),
				1000f);

		for (int i = 0; i < 10; i++) {
			createLight(pointLightGreen, rootGameObject, new Vec3f(i*100, 500f, 100), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		}
		for (int i = 0; i < 10; i++) {
			createLight(pointLightRed, rootGameObject, new Vec3f(500f, i*100, 100), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		}
		createLight(pointLightBlue, rootGameObject, new Vec3f(0f, 0f, 200), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		createLight(pointLightBlue, rootGameObject, new Vec3f(1000f, 0f, 200), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		createLight(pointLightBlue, rootGameObject, new Vec3f(1000f, 1000f, 200), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		createLight(pointLightBlue, rootGameObject, new Vec3f(0f, 1000f, 200), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);

		Camera camera = new Camera(new Vec3f(500.0f, 500.0f, 100.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new Transform(
				Vec3f.X.scale(10),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);

		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		Window window = new Window(
				1200,
				800,
				"");

		LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

		window.init();

		while (!window.shouldClose()) {

			window.loop(gameObjects, new HashMap<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

			LWJGLGameControlManager.checkInputs();

		}

		window.destroy();

	}

	@Test
	public void normal() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		Transform hudTransform = new Transform(
				Vec3f.X,
				Vec3f.ONE.scale(10),
				Matrix4f.Identity
		);

		Transform transform = new Transform(
				Vec3f.X.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

		TransformSceneGraph hudTransformGameObject = new TransformSceneGraph(rootGameObject, hudTransform);

		MeshObject textItem = new MeshBuilder()
				.setMeshType(MeshType.TEXT)
		.build();

		MeshSceneGraph textMeshObject = new MeshSceneGraph(hudTransformGameObject, textItem);

		MeshObject meshGroupLight = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setInvertedNormals(true)
				.setTexture("/textures/mars.jpg")
				.setTransform(Matrix4f.Transform(Vec3f.ZERO, Matrix4f.Identity, Vec3f.ONE.scale(10)))
				.build();

		PointLight pointLight = new PointLight(
				new Vec3f(0.0f, 1.0f, 0.0f),
				10f);
		DirectionalLight directionalLight = new DirectionalLight(
				new Vec3f(1.0f, 1.0f, 1.0f),
				new Vec3f(0.0f, 0.0f, -1.0f),
				1f);
		SpotLight spotLight = new SpotLight(
				new PointLight(
						new Vec3f(1.0f, 0.0f, 0.0f),
						100f),
				Vec3f.Y,
				0.1f
		);

		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -10), Vec3f.ONE.scale(0.5f), Matrix4f.Identity, meshGroupLight);
		createLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0.0f), Vec3f.ONE.scale(0.5f), Matrix4f.Rotation(0.0f, Vec3f.Y), meshGroupLight);
		createLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0), Vec3f.ONE.scale(0.5f), Matrix4f.Identity, meshGroupLight);

		Camera camera = new Camera(new Vec3f(-50.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new Transform(
				Vec3f.X.scale(10),
				Vec3f.ONE,
				//Matrix4f.Identity
				Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(wholeSceneTransform, cameraTransform);
		DirectTransformController directTransformController = new DirectTransformController(wholeSceneTransform, true, true);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);


		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		Window window = new Window(
				1200,
				800,
				"");

		LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

		window.init();

		while (!window.shouldClose()) {

			window.loop(gameObjects, new HashMap<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

			LWJGLGameControlManager.checkInputs();

		}

		window.destroy();

	}

	public void buildMeshes(ArrayList<SceneGraphNode> gameObjects) {
		for (SceneGraphNode gameObject : gameObjects) {
			if (gameObject instanceof MeshObject) {
				((MeshObject) gameObject).getMesh().create();
			}
			buildMeshes(gameObject.getSceneGraphNodeData().getChildren());
		}
	}

	private void createAxis(TransformSceneGraph wholeSceneTransform) {

		MeshObject meshGroupX = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/red.png")
				.build();

		Transform transformMeshX = new Transform(
				Vec3f.X.scale(5),
				Vec3f.ONE.scale(0.1f).add(Vec3f.X.scale(10)),
				Matrix4f.Identity
		);
		TransformSceneGraph meshTransformX = new TransformSceneGraph(wholeSceneTransform, transformMeshX);
		MeshSceneGraph meshGameObjectX = new MeshSceneGraph(
				meshTransformX,
				meshGroupX
		);

		MeshObject meshGroupY = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/red.png")
				.build();

		Transform transformMeshY = new Transform(
				Vec3f.Y.scale(5),
				Vec3f.ONE.scale(0.1f).add(Vec3f.Y.scale(10)),
				Matrix4f.Identity
		);
		TransformSceneGraph meshTransformY = new TransformSceneGraph(wholeSceneTransform, transformMeshY);
		MeshSceneGraph meshGameObjectY = new MeshSceneGraph(
				meshTransformY,
				meshGroupY
		);

		MeshObject meshGroupZ = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/red.png")
				.build();

		Transform transformMeshZ = new Transform(
				Vec3f.Z.scale(5),
				Vec3f.ONE.scale(0.1f).add(Vec3f.Z.scale(10)),
				Matrix4f.Identity
		);
		TransformSceneGraph meshTransformZ = new TransformSceneGraph(wholeSceneTransform, transformMeshZ);
		MeshSceneGraph meshGameObjectZ = new MeshSceneGraph(
				meshTransformZ,
				meshGroupZ
		);
	}

	private void createLight(Light light, SceneGraphNode parent, Vec3f position, Vec3f scale, Matrix4f rotation, MeshObject meshGroup) {
		Transform lightGameObjectTransform = new Transform(
				position,
				scale,
				rotation
		);
		TransformSceneGraph transformGameObject = new TransformSceneGraph(parent, lightGameObjectTransform);
		LightSceneGraph lightGameObject = new LightSceneGraph(transformGameObject, light);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				transformGameObject,
				meshGroup
		);
	}

	private void createLight(Light light, SceneGraphNode parent, Transform lightGameObjectTransform, MeshObject meshGroup) {
		TransformSceneGraph transformGameObject = new TransformSceneGraph(parent, lightGameObjectTransform);
		LightSceneGraph lightGameObject = new LightSceneGraph(transformGameObject, light);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				transformGameObject,
				meshGroup
		);
	}

}