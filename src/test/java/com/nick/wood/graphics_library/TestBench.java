package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.input.DirectCameraController;
import com.nick.wood.graphics_library.input.DirectTransformController;
import com.nick.wood.graphics_library.input.LWJGLGameControlManager;
import com.nick.wood.graphics_library.lighting.DirectionalLight;
import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.lighting.PointLight;
import com.nick.wood.graphics_library.lighting.SpotLight;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.scene_graph_objects.*;
import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.graphics_library.objects.mesh_objects.*;
import com.nick.wood.graphics_library.utils.ChunkLoader;
import com.nick.wood.graphics_library.utils.ProceduralGeneration;
import com.nick.wood.maths.noise.Perlin2Df;
import com.nick.wood.maths.noise.Perlin3D;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TestBench {

	@Test
	public void empty() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		Camera camera = new Camera(new Vec3f(-10.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new Transform(
				Vec3f.X.scale(-10),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		gameObjects.put(UUID.randomUUID(), rootGameObject);

		try (Window window = new Window(
				1200,
				800,
				"")) {

			window.init();

			while (!window.shouldClose()) {

				window.loop(gameObjects, new HashMap<>(), cameraTransformGameObject.getSceneGraphNodeData().getUuid());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

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


		try (Window window = new Window(
				1200,
				800,
				"")) {

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

		} catch (Exception exception) {
			exception.printStackTrace();
		}

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

	@Test
	void infiniteHeightMapTerrain() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		int size = 1000;

		MeshObject meshGroupLight = new MeshBuilder()
				.setInvertedNormals(true)
				.build();

		DirectionalLight sun = new DirectionalLight(
				new Vec3f(0.9f, 1.0f, 1.0f),
				Vec3f.Y.add(Vec3f.Z.neg()).normalise(),
				0.5f);

		LightSceneGraph lightGameObject = new LightSceneGraph(rootGameObject, sun);


		Camera camera = new Camera(new Vec3f(size, size, 100.0f), new Vec3f(0.0f, 0.0f, 0.0f), 10f, 0.1f);

		Transform cameraTransform = new Transform(
				Vec3f.X.scale(10),
				Vec3f.ONE,
				Matrix4f.Identity
		);

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		SceneGraph skyboxSceneGraph = new SceneGraph();
		MeshObject skybox = new MeshBuilder().setInvertedNormals(true).setTransform(Matrix4f.Scale(new Vec3f(10000, 10000, 10000))).setTexture("/textures/2k_neptune.jpg").build();
		SkyBox meshSceneGraph = new SkyBox(skyboxSceneGraph, skybox);
		gameObjects.put(skyboxSceneGraph.getSceneGraphNodeData().getUuid(), skyboxSceneGraph);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);

		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		try (Window window = new Window(
				1200,
				800,
				"")) {

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			window.init();

			ChunkLoader chunkLoader = new ChunkLoader(gameObjects, 5, 2);

			while (!window.shouldClose()) {

				LWJGLGameControlManager.checkInputs();

				chunkLoader.loadChunk(camera.getPos());

				window.loop(gameObjects, new HashMap<>(), cameraGameObject.getSceneGraphNodeData().getUuid());


			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	@Test
	void terrain() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		int size = 1000;

		ProceduralGeneration proceduralGeneration = new ProceduralGeneration();
		float[][] grid = proceduralGeneration.generateHeightMapChunk(
				1000,
				size,
				5,
				2,
				0.7,
				1000,
				0,
				0,
				20,
				(amp) -> amp * amp
		);

		MeshObject terrain = new MeshBuilder()
				.setMeshType(MeshType.TERRAIN)
				.setTerrainHeightMap(grid)
				.setTexture("/textures/terrain.png")
				.setCellSpace(2.0)
				.build();

		MeshSceneGraph meshSceneGraph = new MeshSceneGraph(rootGameObject, terrain);

		MeshObject meshGroupLight = new MeshBuilder()
				.setInvertedNormals(true)
				.build();

		DirectionalLight sun = new DirectionalLight(
				new Vec3f(0.9f, 1.0f, 1.0f),
				Vec3f.Y,
				0.5f);

		LightSceneGraph lightGameObject = new LightSceneGraph(rootGameObject, sun);

		Camera camera = new Camera(new Vec3f(size / 2.0f, size / 2.0f, 100.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new Transform(
				Vec3f.X.scale(10),
				Vec3f.ONE,
				Matrix4f.Identity
		);

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);

		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		try (Window window = new Window(
				1200,
				800,
				"")) {

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			window.init();

			float scaleVal = 0.005f;
			Vec3f sumMovement = Vec3f.Z.scale(-scaleVal).add(Vec3f.Y.scale(-scaleVal));

			while (!window.shouldClose()) {

				window.loop(gameObjects, new HashMap<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

				//sun.setDirection(sun.getDirection().add(sumMovement).normalise());
//
				//if (sun.getDirection().getZ() < -0.99) {
				//	sumMovement = Vec3f.Z.scale(scaleVal).add(Vec3f.Y.scale(-scaleVal));
				//}

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Test
	void terrain3D() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		int cubeSize = 2;

		MeshObject cubeSand = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/sand_blocky.jpg")
				.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		MeshObject cubeGrass = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/grass.png")
				.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		MeshObject cubeSnow = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/white.png")
				.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		MeshObject cubeFire = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/8k_venus_surface.jpg")
				.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		int segmentSize = 10;
		int hillHeight = 60;
		Perlin3D perlin3D = new Perlin3D(500, segmentSize);
		Perlin2Df perlin2D = new Perlin2Df(500, segmentSize);
		int size = 30;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					double point = perlin3D.getPoint(i, j, k);

					double weight = (k - (size / 2.0)) / (size / 2.0) - 0.15;

					if (point < (weight * weight * weight * weight)) {

						Transform transform = new Transform(
								new Vec3f(i * cubeSize, j * cubeSize, k * cubeSize),
								Vec3f.ONE,
								Matrix4f.Identity
						);

						TransformSceneGraph transformSceneGraph = new TransformSceneGraph(rootGameObject, transform);

						if (k < 2) {
							MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeFire);
						}
						if (k < size - 2) {
							MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeSand);
						} else {
							MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeGrass);
						}

					}
				}

				double point = (int) (perlin2D.getPoint(i, j) * hillHeight);

				for (int k = 0; k < point; k++) {

					Transform transform = new Transform(
							new Vec3f(i * cubeSize, j * cubeSize, (k + size) * cubeSize),
							Vec3f.ONE,
							Matrix4f.Identity
					);

					TransformSceneGraph transformSceneGraph = new TransformSceneGraph(rootGameObject, transform);

					if (k > 15) {
						MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeSnow);
					} else {
						MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeGrass);
					}

				}
			}
		}

		DirectionalLight y = new DirectionalLight(
				new Vec3f(1f, 1f, 1f),
				Vec3f.Y,
				0.5f);

		DirectionalLight yn = new DirectionalLight(
				new Vec3f(1f, 1f, 1f),
				Vec3f.Y.neg(),
				0.2f);

		DirectionalLight x = new DirectionalLight(
				new Vec3f(1f, 1f, 1f),
				Vec3f.X,
				0.5f);

		DirectionalLight xn = new DirectionalLight(
				new Vec3f(1f, 1f, 1f),
				Vec3f.X.neg(),
				0.2f);

		DirectionalLight z = new DirectionalLight(
				new Vec3f(1f, 1f, 1f),
				Vec3f.Z,
				0.5f);

		DirectionalLight zn = new DirectionalLight(
				new Vec3f(1f, 1f, 1f),
				Vec3f.Z.neg(),
				0.2f);

		LightSceneGraph lightGameObjectZ = new LightSceneGraph(rootGameObject, z);
		LightSceneGraph lightGameObjectZn = new LightSceneGraph(rootGameObject, zn);
		LightSceneGraph lightGameObjectY = new LightSceneGraph(rootGameObject, y);
		LightSceneGraph lightGameObjectYn = new LightSceneGraph(rootGameObject, yn);
		LightSceneGraph lightGameObjectX = new LightSceneGraph(rootGameObject, x);
		LightSceneGraph lightGameObjectXn = new LightSceneGraph(rootGameObject, xn);

		Camera camera = new Camera(new Vec3f(0, 0, size * 2), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new Transform(
				Vec3f.X.scale(10),
				Vec3f.ONE,
				Matrix4f.Identity
		);

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);

		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		try (Window window = new Window(
				1200,
				800,
				"")) {


			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			window.init();

			while (!window.shouldClose()) {

				window.loop(gameObjects, new HashMap<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	HashMap<String, TransformSceneGraph> cubeMap = new HashMap<>();

	@Test
	void infiniteTerrain3D() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		int cubeSize = 1;

		MeshObject cubeSand = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/sand_blocky.jpg")
				.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		MeshObject cubeGrass = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/grass.png")
				.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		MeshObject cubeSnow = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/white.png")
				.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		MeshObject cubeFire = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/8k_venus_surface.jpg")
				.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		int segmentSize = 10;
		int hillHeight = 30;
		Vec3f cullCube = new Vec3f(30, 30, 30);
		Perlin3D perlin3D = new Perlin3D(50000, segmentSize);
		Perlin2Df perlin2D = new Perlin2Df(50000, segmentSize);

		DirectionalLight pos = new DirectionalLight(
				new Vec3f(1f, 1f, 1f),
				Vec3f.Y.add(Vec3f.X).add(Vec3f.Z),
				0.6f);

		LightSceneGraph lightGameObject = new LightSceneGraph(rootGameObject, pos);

		Vec3f cameraStartPos = new Vec3f(0, 0, 100);

		Camera camera = new Camera(cameraStartPos, new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new Transform(
				Vec3f.ZERO,
				Vec3f.ONE,
				Matrix4f.Identity
		);

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		MeshObject skybox = new MeshBuilder().setInvertedNormals(true).setTransform(Matrix4f.Scale(new Vec3f(1000, 1000, 1000))).setTexture("/textures/2k_neptune.jpg").build();

		MeshSceneGraph meshSceneGraph = new MeshSceneGraph(cameraTransformGameObject, skybox);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);

		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		try (Window window = new Window(
				1200,
				800,
				"")) {


			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			window.init();

			HashMap<UUID, SceneGraph> objectObjectHashMap = new HashMap<>();

			while (!window.shouldClose()) {

				window.loop(gameObjects, objectObjectHashMap, cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

				createMap(camera.getPos(), cullCube, perlin3D, perlin2D, cubeSize, gameObjects, hillHeight, cubeFire, cubeSand, cubeGrass, cubeSnow);


				if (cubeMap.size() > 1000) {
					cullCubes(rootGameObject, camera.getPos());
					//System.gc();
				}

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	private void cullCubes(SceneGraph rootGameObject, Vec3f pos) {

		ArrayList<String> removeList = new ArrayList<>();

		for (Map.Entry<String, TransformSceneGraph> integerTransformSceneGraphEntry : cubeMap.entrySet()) {

			if (integerTransformSceneGraphEntry.getValue().getTransform().getPosition().subtract(pos).length2() > 900) {
				StringBuilder stringBuffer = new StringBuilder();
				stringBuffer.append(((int) integerTransformSceneGraphEntry.getValue().getTransform().getPosition().getX()))
						.append("_")
						.append(((int) (integerTransformSceneGraphEntry.getValue().getTransform().getPosition().getY())))
						.append("_")
						.append(((int) (integerTransformSceneGraphEntry.getValue().getTransform().getPosition().getZ())));
				removeList.add(stringBuffer.toString());

				rootGameObject.getSceneGraphNodeData().removeGameObjectNode(integerTransformSceneGraphEntry.getValue());
				for (SceneGraphNode child : integerTransformSceneGraphEntry.getValue().getSceneGraphNodeData().getChildren()) {
					if (child instanceof MeshSceneGraph) {
						MeshSceneGraph meshSceneGraph = (MeshSceneGraph) child;
						meshSceneGraph.removeMeshObject();
					}
					child.getSceneGraphNodeData().setParent(null);
				}
				integerTransformSceneGraphEntry.getValue().getSceneGraphNodeData().getChildren().clear();
				integerTransformSceneGraphEntry.getValue().getSceneGraphNodeData().setParent(null);
			} else {
				// check if box is behind camera
			}

		}


		for (String index : removeList) {
			cubeMap.remove(index);
		}

	}

	private void createMap(Vec3f center,
	                       Vec3f cullCube,
	                       Perlin3D perlin3D,
	                       Perlin2Df perlin2D,
	                       int cubeSize,
	                       HashMap<UUID, SceneGraph> sceneGraphHashMap,
	                       int hillHeight,
	                       MeshObject cubeFire,
	                       MeshObject cubeSand,
	                       MeshObject cubeGrass,
	                       MeshObject cubeSnow) {


		Vec3f bottomCornerToLoad = center.add(cullCube.scale(-0.5f));
		Vec3f topCornerToLoad = bottomCornerToLoad.add(cullCube);

		for (int i = (int) bottomCornerToLoad.getX(); i < topCornerToLoad.getX(); i++) {
			for (int j = (int) bottomCornerToLoad.getY(); j < topCornerToLoad.getY(); j++) {
				for (int k = (int) bottomCornerToLoad.getZ(); k < topCornerToLoad.getZ(); k++) {

					if (k >= 0) {

						String index = i + "_" + j + "_" + k;

						if (!cubeMap.containsKey(index)) {

							double point = perlin3D.getPoint(Math.abs(i), Math.abs(j), Math.abs(k));

							if (point < 0.05) {

								double weight = -1;

								if (k < 100) {
									weight = ((k - 50.0) / 50.0) - 0.4;
									weight *= weight * weight * weight;
								} else if (k < 110) {
									weight = ((103.0 - k) / 10.0);
								}

								if (point < weight) {

									SceneGraph sceneGraph = new SceneGraph();

									Transform transform = new Transform(
											new Vec3f(i * cubeSize, j * cubeSize, k * cubeSize),
											Vec3f.ONE,
											Matrix4f.Identity
									);

									TransformSceneGraph transformSceneGraph = new TransformSceneGraph(sceneGraph, transform);

									if (k < 2) {
										MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeFire);
									} else if (k < 100) {
										MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeSand);
									} else {
										MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeGrass);
									}
									cubeMap.put(index, transformSceneGraph);
									sceneGraphHashMap.put(sceneGraph.getSceneGraphNodeData().getUuid(), sceneGraph);
								}
							}
						}
					}
				}
			}
		}
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

		try (Window window = new Window(
				1200,
				800,
				"")) {

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			window.init();

			while (!window.shouldClose()) {

				window.loop(gameObjects, new HashMap<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	@Test
	public void wgs84() {
		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		Transform transform = new Transform(
				Vec3f.X.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
		);

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);


		//new Vec3f(-90.0f, 180.0f, 90.0f)

		Matrix4f multiply = Matrix4f.Rotation(-90, Vec3f.X).multiply(Matrix4f.Rotation(180, Vec3f.Y)).multiply(Matrix4f.Rotation(90, Vec3f.Z));

		//MeshObject earth = new MeshBuilder()
		//		.setMeshType(MeshType.MODEL)
		//		.setInvertedNormals(false)
		//		.setTexture("/textures/worldHeightMapUpsideDown.png")
		//		.setTransform(Matrix4f.Transform(Vec3f.ZERO, multiply, Vec3f.ONE.scale(10)))
		//		.build();
//
		//MeshSceneGraph meshSceneGraph = new MeshSceneGraph(wholeSceneTransform, earth);


		MeshObject meshGroupLight = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setInvertedNormals(true)
				.setTexture("/textures/mars.jpg")
				.setTransform(Matrix4f.Transform(Vec3f.ZERO, Matrix4f.Identity, Vec3f.ONE))
				.build();

		PointLight pointLight = new PointLight(
				new Vec3f(1.0f, 1.0f, 1.0f),
				1000f);

		createLight(pointLight, wholeSceneTransform, new Vec3f(50.0f, 0.0f, 0), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		createLight(pointLight, wholeSceneTransform, new Vec3f(-50.0f, 0.0f, 0), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 50.0f, 0), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, -50.0f, 0), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, 50), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);
		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -50), Vec3f.ONE, Matrix4f.Identity, meshGroupLight);

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

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);


		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		try (Window window = new Window(
				1200,
				800,
				"")) {

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			window.init();

			while (!window.shouldClose()) {

				window.loop(gameObjects, new HashMap<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/*@Test
	public void vr() {
		System.err.println("VR_IsRuntimeInstalled() = " + VR_IsRuntimeInstalled());
		System.err.println("VR_RuntimePath() = " + VR_RuntimePath());
		System.err.println("VR_IsHmdPresent() = " + VR_IsHmdPresent());

		try (MemoryStack stack = stackPush()) {
			IntBuffer peError = stack.mallocInt(1);

			int token = VR_InitInternal(peError, 0);
			if (peError.get(0) == 0) {
				try {
					OpenVR.create(token);

					System.err.println("Model Number : " + VRSystem_GetStringTrackedDeviceProperty(
							k_unTrackedDeviceIndex_Hmd,
							ETrackedDeviceProperty_Prop_ModelNumber_String,
							peError
					));
					System.err.println("Serial Number: " + VRSystem_GetStringTrackedDeviceProperty(
							k_unTrackedDeviceIndex_Hmd,
							ETrackedDeviceProperty_Prop_SerialNumber_String,
							peError
					));

					IntBuffer w = stack.mallocInt(1);
					IntBuffer h = stack.mallocInt(1);
					VRSystem_GetRecommendedRenderTargetSize(w, h);
					System.err.println("Recommended width : " + w.get(0));
					System.err.println("Recommended height: " + h.get(0));
				} finally {
					VR_ShutdownInternal();
				}
			} else {
				System.out.println("INIT ERROR SYMBOL: " + VR_GetVRInitErrorAsSymbol(peError.get(0)));
				System.out.println("INIT ERROR  DESCR: " + VR_GetVRInitErrorAsEnglishDescription(peError.get(0)));
			}
		}
	}*/

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

	private void createLight(Light light, SceneGraphNode parent, Vec3f position, Vec3f scale, Matrix4f
			rotation, MeshObject meshGroup) {
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

	private void createLight(Light light, SceneGraphNode parent, Transform lightGameObjectTransform, MeshObject
			meshGroup) {
		TransformSceneGraph transformGameObject = new TransformSceneGraph(parent, lightGameObjectTransform);
		LightSceneGraph lightGameObject = new LightSceneGraph(transformGameObject, light);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				transformGameObject,
				meshGroup
		);
	}

	@Test
	public void particleSystem() {
		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		Transform hudTransform = new Transform(
				Vec3f.X,
				Vec3f.ONE,
				Matrix4f.Identity
		);

		Transform transform = new Transform(
				Vec3f.X.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
		);

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

		TransformSceneGraph hudTransformGameObject = new TransformSceneGraph(rootGameObject, hudTransform);

		MeshObject point = new MeshBuilder()
				.setMeshType(MeshType.POINT)
				.build();

		MeshSceneGraph textMeshObject = new MeshSceneGraph(wholeSceneTransform, point);

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

		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -1), Vec3f.ONE, Matrix4f.Identity, point);
		createLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0.0f), Vec3f.ONE, Matrix4f.Identity, point);
		createLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0), Vec3f.ONE, Matrix4f.Identity, point);

		Camera camera = new Camera(new Vec3f(-1.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new Transform(
				Vec3f.X,
				Vec3f.ONE,
				Matrix4f.Identity
		);

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(wholeSceneTransform, cameraTransform);
		DirectTransformController directTransformController = new DirectTransformController(wholeSceneTransform, true, true);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);


		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		try (Window window = new Window(
				1200,
				800,
				"")) {

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			window.init();

			while (!window.shouldClose()) {

				window.loop(gameObjects, new HashMap<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}