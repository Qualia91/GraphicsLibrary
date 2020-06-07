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
import com.nick.wood.graphics_library.objects.mesh_objects.*;
import com.nick.wood.graphics_library.utils.Cell;
import com.nick.wood.graphics_library.utils.ChunkLoader;
import com.nick.wood.graphics_library.utils.ProceduralGeneration;
import com.nick.wood.graphics_library.utils.RecursiveBackTracker;
import com.nick.wood.maths.noise.Perlin2Df;
import com.nick.wood.maths.noise.Perlin3D;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec2i;
import com.nick.wood.maths.objects.vector.Vec3f;
import com.nick.wood.maths.objects.vector.Vec4f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class TestBench {

	@Test
	public void empty() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		Camera camera = new Camera(new Vec3f(-10.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new TransformBuilder()
				.setPosition(Vec3f.X.scale(-10)).build();

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

		TransformBuilder transformBuilder = new TransformBuilder();

		Transform transform = transformBuilder
				.setPosition(Vec3f.X.scale(0)).build();


		MeshObject meshGroup = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setModelFile("D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\dragon.obj")
				.setTexture("/textures/white.png")
				.setTransform(transformBuilder
						.setPosition(Vec3f.ZERO)
						.setRotation(QuaternionF.RotationX(-90)).build())
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

		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -10f), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);
		createLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0.0f), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);
		createLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);

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

	private Transform createObject(Vec3f pos, SceneGraphNode parent, MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos).build();

		TransformSceneGraph meshTransform = new TransformSceneGraph(parent, transformMesh);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				meshTransform,
				meshGroup
		);

		return transformMesh;
	}

	private Transform createObject(Vec3f pos, QuaternionF rotation, SceneGraphNode parent, MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos)
				.setRotation(rotation)
				.build();

		TransformSceneGraph meshTransform = new TransformSceneGraph(parent, transformMesh);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				meshTransform,
				meshGroup
		);

		return transformMesh;
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

		Transform cameraTransform = new TransformBuilder()
				.setPosition(Vec3f.X.scale(10)).build();

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		SceneGraph skyboxSceneGraph = new SceneGraph();
		SkyBox skyBox = new SkyBox(skyboxSceneGraph, "/textures/mars.jpg", SkyboxType.SPHERE);
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

		int size = 500;

		ProceduralGeneration proceduralGeneration = new ProceduralGeneration();
		float[][] grid = proceduralGeneration.generateHeightMapChunk(
				100000,
				size,
				5,
				2,
				0.7,
				100,
				0,
				0,
				5,
				(amp) -> amp * amp
		);

		SkyBox skyBox = new SkyBox(rootGameObject, "/textures/2k_neptune.jpg", SkyboxType.SPHERE);

		MeshObject terrain = new MeshBuilder()
				.setMeshType(MeshType.TERRAIN)
				.setTerrainHeightMap(grid)
				.setTexture("/textures/mars.jpg")
				.setCellSpace(2.0)
				.build();

		MeshSceneGraph meshSceneGraph = new MeshSceneGraph(rootGameObject, terrain);

		WaterSceneObject water = new WaterSceneObject(rootGameObject, "/textures/blue.png", size, 0, 2);

		MeshObject meshGroupLight = new MeshBuilder()
				.setInvertedNormals(true)
				.build();

		DirectionalLight sun = new DirectionalLight(
				new Vec3f(0.9f, 1.0f, 1.0f),
				Vec3f.Y.add(Vec3f.Z.neg()),
				0.5f);

		LightSceneGraph lightGameObject = new LightSceneGraph(rootGameObject, sun);

		Camera camera = new Camera(Vec3f.ZERO, new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = new TransformBuilder()
				.setPosition(new Vec3f(size / 2.0f, size / 2.0f, 100.0f)).build();

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		DirectTransformController directCameraController = new DirectTransformController(cameraTransformGameObject, true, true);
		//DirectCameraController directCameraController = new DirectCameraController(camera, true, true);

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
	void terrain3D() {

		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		int cubeSize = 2;

		TransformBuilder transformBuilder = new TransformBuilder();

		MeshObject cubeSand = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setModelFile("D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj")
				.setTexture("/textures/brickwall.jpg")
				.setNormalTexture("/textures/brickwall_normal.jpg")
				//.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		MeshObject cubeGrass = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setModelFile("D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj")
				.setTexture("/textures/grass.png")
				.setNormalTexture("/textures/sandNormalMap.jpg")
				//.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		MeshObject cubeSnow = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setModelFile("D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj")
				.setTexture("/textures/white.png")
				.setTransform(transformBuilder.setScale(new Vec3f(cubeSize, cubeSize, cubeSize)).build())
				.build();

		MeshObject cubeFire = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setModelFile("D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj")
				.setTexture("/textures/8k_venus_surface.jpg")
				.setNormalTexture("/textures/sandNormalMap.jpg")
				//.setTransform(Matrix4f.Scale(new Vec3f(cubeSize, cubeSize, cubeSize)))
				.build();

		int segmentSize = 10;
		int hillHeight = 20;
		Perlin3D perlin3D = new Perlin3D(500, segmentSize);
		Perlin2Df perlin2D = new Perlin2Df(500, segmentSize);
		int size = 50;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					double point = perlin3D.getPoint(i, j, k);

					double weight = (k - (size / 2.0)) / (size / 2.0) - 0.15;

					if (point < (weight * weight * weight * weight)) {

						Transform transform = transformBuilder
								.setPosition(new Vec3f(i * cubeSize, j * cubeSize, k * cubeSize))
								.setScale(Vec3f.ONE).build();

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

					Transform transform = transformBuilder
							.setPosition(new Vec3f(i * cubeSize, j * cubeSize, (k + size) * cubeSize)).build();

					TransformSceneGraph transformSceneGraph = new TransformSceneGraph(rootGameObject, transform);

					if (k > 15) {
						MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeSnow);
					} else {
						MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeGrass);
					}

				}
			}
		}



		Camera camera = new Camera(new Vec3f(0, 0, 0), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = transformBuilder
				.setPosition(Vec3f.X.scale(10)).build();

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		float width = (size * cubeSize);
		int space = 50;

		int counter = 0;
		for (int i = -space; i < width + space; i+= space) {
			for (int j = -space; j < width + space; j+= space) {
				for (int k = -space; k < width + space; k+= space) {
					Transform t = transformBuilder
							.setPosition(new Vec3f(i, j, k)).build();

					PointLight pointLight = new PointLight(
							new Vec3f(0.5412f, 0.1f, 0.1f),
							50
					);
					TransformSceneGraph ct = new TransformSceneGraph(rootGameObject, t);
					LightSceneGraph pointLightSceneObj = new LightSceneGraph(ct, pointLight);
					counter++;
				}
			}
		}

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);

		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		try (Window window = new Window(
				1200,
				800,
				"")) {


			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			window.init();

			window.getScene().setAmbientLight(new Vec3f(0.9765f/2, 0.8431f/2, 0.1098f/2));

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

		TransformBuilder transformBuilder = new TransformBuilder();

		MeshObject cubeSand = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/sand_blocky.jpg")
				.setTransform(transformBuilder
						.setScale(new Vec3f(cubeSize, cubeSize, cubeSize)).build())
				.build();

		MeshObject cubeGrass = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/grass.png")
				.setTransform(transformBuilder.build())
				.build();

		MeshObject cubeSnow = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/white.png")
				.setTransform(transformBuilder.build())
				.build();

		MeshObject cubeFire = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/8k_venus_surface.jpg")
				.setTransform(transformBuilder.build())
				.build();

		int segmentSize = 10;
		int hillHeight = 30;
		Vec3f cullCube = new Vec3f(30, 30, 30);
		Perlin3D perlin3D = new Perlin3D(50000, segmentSize);
		Perlin2Df perlin2D = new Perlin2Df(50000, segmentSize);

		DirectionalLight pos = new DirectionalLight(
				new Vec3f(1f, 1f, 1f),
				Vec3f.Y.add(Vec3f.X).add(Vec3f.Z),
				1f);

		LightSceneGraph lightGameObject = new LightSceneGraph(rootGameObject, pos);

		Vec3f cameraStartPos = new Vec3f(0, 0, 100);

		Camera camera = new Camera(cameraStartPos, new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = transformBuilder
				.setScale(Vec3f.ONE).build();

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		SkyBox skyBox = new SkyBox(rootGameObject, "/textures/2k_neptune.jpg", SkyboxType.SPHERE);

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
					System.gc();
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

		TransformBuilder transformBuilder = new TransformBuilder();

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

									Transform transform = transformBuilder
											.setPosition(new Vec3f(i * cubeSize, j * cubeSize, k * cubeSize)).build();

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

		TransformBuilder transformBuilder = new TransformBuilder();

		Transform hudTransform = transformBuilder
				.setPosition(Vec3f.X)
				.setScale(Vec3f.ONE.scale(10)).build();

		Transform transform = transformBuilder
				.setPosition(Vec3f.ZERO)
				.setScale(Vec3f.ONE).build();

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
				.setTransform(transformBuilder
						.setScale(Vec3f.ONE).build())
				.build();

		MeshObject mesh = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/brickwall.jpg")
				.setNormalTexture("/textures/brickwall_normal.jpg")
				.setTransform(transformBuilder.build())
				.build();


		MeshSceneGraph meshSceneGraph = new MeshSceneGraph(wholeSceneTransform, mesh);

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

		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -10), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);
		createLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0.0f), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);
		createLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);

		Camera camera = new Camera(new Vec3f(-50.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = transformBuilder
				.setPosition(Vec3f.X.scale(10))
				.setRotation(QuaternionF.RotationX(90))
				.setScale(Vec3f.ONE)
				.build();

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

		TransformBuilder transformBuilder = new TransformBuilder();

		MeshObject meshGroupX = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/red.png")
				.build();

		Transform transformMeshX = transformBuilder
				.setPosition(Vec3f.X.scale(5))
				.setScale(Vec3f.ONE.scale(0.1f).add(Vec3f.X.scale(10)))
				.build();
		TransformSceneGraph meshTransformX = new TransformSceneGraph(wholeSceneTransform, transformMeshX);
		MeshSceneGraph meshGameObjectX = new MeshSceneGraph(
				meshTransformX,
				meshGroupX
		);

		MeshObject meshGroupY = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/blue.png")
				.build();

		Transform transformMeshY = transformBuilder
				.setRotation(QuaternionF.RotationZ(90))
				.build();

		TransformSceneGraph meshTransformY = new TransformSceneGraph(wholeSceneTransform, transformMeshY);
		MeshSceneGraph meshGameObjectY = new MeshSceneGraph(
				meshTransformY,
				meshGroupY
		);

		MeshObject meshGroupZ = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/green.png")
				.build();

		Transform transformMeshZ = transformBuilder
				.setRotation(QuaternionF.RotationX(90))
				.build();

		TransformSceneGraph meshTransformZ = new TransformSceneGraph(wholeSceneTransform, transformMeshZ);
		MeshSceneGraph meshGameObjectZ = new MeshSceneGraph(
				meshTransformZ,
				meshGroupZ
		);
	}

	private void createLight(Light light, SceneGraphNode parent, Vec3f position, Vec3f scale,
	                         QuaternionF rotation, MeshObject meshGroup) {

		TransformBuilder transformBuilder = new TransformBuilder(
				position,
				scale,
				rotation
		);

		Transform lightGameObjectTransform = transformBuilder.build();

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

		TransformBuilder transformBuilder = new TransformBuilder();

		Transform hudTransform = transformBuilder
				.setPosition(Vec3f.X)
				.build();

		Transform transform = transformBuilder
				.setPosition(Vec3f.ZERO)
				.build();

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

		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -1), Vec3f.ONE, QuaternionF.Identity, point);
		createLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0.0f), Vec3f.ONE, QuaternionF.Identity, point);
		createLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0), Vec3f.ONE, QuaternionF.Identity, point);

		Camera camera = new Camera(new Vec3f(-1.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = transformBuilder
				.setPosition(Vec3f.X)
				.build();

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
	public void mase() {
		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		TransformBuilder transformBuilder = new TransformBuilder();

		Transform transform = transformBuilder.build();

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

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

		createLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -1), Vec3f.ONE, QuaternionF.Identity, point);
		createLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0.0f), Vec3f.ONE, QuaternionF.Identity, point);
		createLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0), Vec3f.ONE, QuaternionF.Identity, point);

		Camera camera = new Camera(new Vec3f(-1.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = transformBuilder
				.setPosition(Vec3f.X).build();

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(wholeSceneTransform, cameraTransform);
		DirectTransformController directTransformController = new DirectTransformController(wholeSceneTransform, true, true);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera, CameraType.PRIMARY);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);


		gameObjects.put(cameraGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		int width = 100;
		int height = 100;

		RecursiveBackTracker recursiveBackTracker = new RecursiveBackTracker(width, height);
		ArrayList<Cell> visited = recursiveBackTracker.getVisited();

		// build mase
		MeshObject cuboid = new MeshBuilder().setMeshType(MeshType.CUBOID).setNormalTexture("/textures/sandNormalMap.jpg").build();

		// render diagonals
		for (int i = -1; i < width * 2 + 1; i += 2) {

			for (int j = -1; j < height * 2 + 1; j += 2) {

				Transform cellTransformcell = transformBuilder
						.setPosition(new Vec3f(i, j, 0)).build();

				TransformSceneGraph transformSceneGraphcell = new TransformSceneGraph(wholeSceneTransform, cellTransformcell);

				MeshSceneGraph meshSceneGraphcell = new MeshSceneGraph(transformSceneGraphcell, cuboid);

			}

		}

		Vec2i north = new Vec2i(0, -1);
		Vec2i west = new Vec2i(-1, 0);
		Vec2i south = new Vec2i(0, 1);
		Vec2i east = new Vec2i(1, 0);

		// render walls
		for (Cell cell : visited) {

			if (!cell.getPathDirections().contains(north)) {

				Transform cellTransformcell = transformBuilder
						.setPosition(new Vec3f((cell.getPosition().getX() * 2), (cell.getPosition().getY() * 2) - 1, 0)).build();

				TransformSceneGraph transformSceneGraphcell = new TransformSceneGraph(wholeSceneTransform, cellTransformcell);

				MeshSceneGraph meshSceneGraphcell = new MeshSceneGraph(transformSceneGraphcell, cuboid);

			}

			if (!cell.getPathDirections().contains(south)) {

				Transform cellTransformcell = transformBuilder
						.setPosition(new Vec3f((cell.getPosition().getX() * 2), (cell.getPosition().getY() * 2) + 1, 0)).build();

				TransformSceneGraph transformSceneGraphcell = new TransformSceneGraph(wholeSceneTransform, cellTransformcell);

				MeshSceneGraph meshSceneGraphcell = new MeshSceneGraph(transformSceneGraphcell, cuboid);

			}

			if (!cell.getPathDirections().contains(west) && !cell.getPosition().equals(Vec2i.ZERO)) {

				Transform cellTransformcell = transformBuilder
						.setPosition(new Vec3f((cell.getPosition().getX() * 2) - 1, (cell.getPosition().getY() * 2), 0)).build();

				TransformSceneGraph transformSceneGraphcell = new TransformSceneGraph(wholeSceneTransform, cellTransformcell);

				MeshSceneGraph meshSceneGraphcell = new MeshSceneGraph(transformSceneGraphcell, cuboid);

			}

			if (!cell.getPathDirections().contains(east) && !cell.getPosition().equals(new Vec2i(width - 1, height - 1))) {

				Transform cellTransformcell = transformBuilder
						.setPosition(new Vec3f((cell.getPosition().getX() * 2) + 1, (cell.getPosition().getY() * 2), 0)).build();

				TransformSceneGraph transformSceneGraphcell = new TransformSceneGraph(wholeSceneTransform, cellTransformcell);

				MeshSceneGraph meshSceneGraphcell = new MeshSceneGraph(transformSceneGraphcell, cuboid);

			}

		}

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
	public void reflectionOverAPlane() {
		HashMap<UUID, SceneGraph> gameObjects = new HashMap<>();

		SceneGraph rootGameObject = new SceneGraph();

		gameObjects.put(rootGameObject.getSceneGraphNodeData().getUuid(), rootGameObject);

		TransformBuilder transformBuilder = new TransformBuilder();

		Transform transform = transformBuilder.build();

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

		createAxis(wholeSceneTransform);

		MeshObject meshGroup = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setModelFile("D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\dragon.obj")
				.setTexture("/textures/white.png")
				.build();

		Vec3f pointWeWantToReflect = Vec3f.Z.add(Vec3f.Y).add(Vec3f.X).scale(10);
		Transform object = createObject(pointWeWantToReflect, QuaternionF.RotationX(-135), rootGameObject, meshGroup);

		Vec4f plane = new Vec4f(0, 0, 1, 20);
		//Transform object = createObject(pointWeWantToReflect.reflectionOverPlane(plane), rootGameObject, meshGroup);
		Matrix4f matrix = object.getSRT();

		Matrix4f reflectionMatrix = new Matrix4f(
				1 - (2 * plane.getX() * plane.getX()), -2 * plane.getX() * plane.getY(), -2 * plane.getX() * plane.getZ(), 2 * plane.getX() * plane.getS(),
				-2 * plane.getX() * plane.getY(), 1 - (2 * plane.getY() * plane.getY()), -2 * plane.getY() * plane.getZ(), 2 * plane.getY() * plane.getS(),
				-2 * plane.getX() * plane.getZ(), -2 * plane.getY() * plane.getZ(), 1 - (2 * plane.getZ() * plane.getZ()), 2 * plane.getZ() * plane.getS(),
				0, 0, 0, 1);

		Matrix4f backFaceCullFlip = new Matrix4f(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, -1, 0,
				0, 0, 0, 1
		);

		Matrix4f multiply = backFaceCullFlip.multiply(matrix).multiply(reflectionMatrix);

		Matrix4f rotation = new Matrix4f(
				multiply.get(0,0), multiply.get(1,0), multiply.get(2,0), 0,
				multiply.get(0,1), multiply.get(1,1), multiply.get(2,1), 0,
				multiply.get(0,2), multiply.get(1,2), multiply.get(2,2), 0,
				0, 0, 0, 1);



		Transform object2 = createObject(multiply.getTranslation(), rotation, rootGameObject, meshGroup);

		MeshObject point = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.build();

		DirectionalLight directionalLight = new DirectionalLight(
				new Vec3f(1.0f, 1.0f, 1.0f),
				new Vec3f(1.0f, 1.0f, 1.0f),
				1);

		createLight(directionalLight, wholeSceneTransform, new Vec3f(100.0f, 100.0f, 100), Vec3f.ONE, QuaternionF.Identity, point);

		Camera camera = new Camera(new Vec3f(-1.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

		Transform cameraTransform = transformBuilder
				.setPosition(Vec3f.X)
				.setScale(Vec3f.ONE)
				.setRotation(QuaternionF.Identity)
				.build();

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(wholeSceneTransform, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(wholeSceneTransform, camera, CameraType.PRIMARY);

		DirectCameraController directCameraController = new DirectCameraController(camera, true, true);

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
*/
}