package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.input.DirectTransformController;
import com.nick.wood.graphics_library.input.LWJGLGameControlManager;
import com.nick.wood.graphics_library.lighting.DirectionalLight;
import com.nick.wood.graphics_library.lighting.PointLight;
import com.nick.wood.graphics_library.lighting.SpotLight;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.game_objects.*;
import com.nick.wood.graphics_library.objects.mesh_objects.*;
import com.nick.wood.graphics_library.utils.*;
import com.nick.wood.maths.noise.Perlin2Df;
import com.nick.wood.maths.noise.Perlin3D;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec2i;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.ArrayList;

class TestBench {

	// this is to get world in sensible coordinate system to start with
	private final QuaternionF quaternionX = QuaternionF.RotationX((float) Math.toRadians(-90));
	private final QuaternionF quaternionY = QuaternionF.RotationY((float) Math.toRadians(180));
	private final QuaternionF quaternionZ = QuaternionF.RotationZ((float) Math.toRadians(90));
	private final QuaternionF cameraRotation = quaternionZ.multiply(quaternionY).multiply(quaternionX);

	@Test
	public void empty() {

		ArrayList<RootObject> gameObjects = new ArrayList<>();

		RootObject rootGameObject = new RootObject();

		Camera camera = new Camera(1.22173f, 1, 100000);

		Transform cameraTransform = new TransformBuilder()
				.setPosition(Vec3f.X.scale(-10)).build();

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera);

		gameObjects.add(rootGameObject);

		WindowInitialisationParametersBuilder windowInitialisationParametersBuilder = new WindowInitialisationParametersBuilder();

		try (Window window = new Window()) {

			window.init(windowInitialisationParametersBuilder.build());

			while (!window.shouldClose()) {

				window.loop(gameObjects, new ArrayList<>(), cameraTransformGameObject.getSceneGraphNodeData().getUuid());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void stress() {

		ArrayList<RootObject> gameObjects = new ArrayList<>();

		RootObject rootGameObject = new RootObject();

		TransformBuilder transformBuilder = new TransformBuilder();

		Transform transform = transformBuilder.build();

		MeshObject meshGroup = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setModelFile("D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\dragon.obj")
				.setTexture("/textures/white.png")
				.setTransform(transformBuilder
						.setPosition(Vec3f.ZERO)
						.setRotation(QuaternionF.RotationX(90)).build())
				.build();


		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

		for (int i = 0; i < 1500; i++) {
			Creation.CreateObject(Vec3f.Y.scale(i), wholeSceneTransform, meshGroup);
		}

		Creation.CreateAxis(wholeSceneTransform);

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

		Creation.CreateLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -10f), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);
		Creation.CreateLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0.0f), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);
		Creation.CreateLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);

		Transform cameraTransform = transformBuilder
				.setPosition(new Vec3f(-10, 0, 0))
				.setScale(Vec3f.ONE)
				.setRotation(cameraRotation)
				.build();
		TransformSceneGraph cameraTransformObj = new TransformSceneGraph(rootGameObject, cameraTransform);
		Camera camera = new Camera(1.22173f, 1, 100000);
		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformObj, camera);
		DirectTransformController directCameraController = new DirectTransformController(cameraTransformObj, true, true);

		gameObjects.add(rootGameObject);

		WindowInitialisationParametersBuilder windowInitialisationParametersBuilder = new WindowInitialisationParametersBuilder();

		try (Window window = new Window()) {

			window.init(windowInitialisationParametersBuilder.build());

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			long oldTime = System.currentTimeMillis();

			while (!window.shouldClose()) {

				window.loop(gameObjects, new ArrayList<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

				long currentTime = System.currentTimeMillis();

				window.setTitle("Diff Time: " + (currentTime - oldTime));

				oldTime = currentTime;

			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	@Test
	void infiniteHeightMapTerrain() {

		ArrayList<RootObject> gameObjects = new ArrayList<>();

		RootObject rootGameObject = new RootObject();

		int size = 1000;

		MeshObject meshGroupLight = new MeshBuilder()
				.setInvertedNormals(true)
				.build();

		DirectionalLight sun = new DirectionalLight(
				new Vec3f(0.9f, 1.0f, 1.0f),
				Vec3f.Y.add(Vec3f.Z.neg()).normalise(),
				0.5f);

		LightSceneGraph lightGameObject = new LightSceneGraph(rootGameObject, sun);


		Transform cameraTransform = new TransformBuilder()
				.setPosition(new Vec3f(1000, 1000, 500))
				.setScale(Vec3f.ONE)
				.setRotation(cameraRotation)
				.build();
		TransformSceneGraph cameraTransformObj = new TransformSceneGraph(rootGameObject, cameraTransform);
		Camera camera = new Camera(1.22173f, 10, 100000);
		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformObj, camera);
		DirectTransformController directTransformController = new DirectTransformController(cameraTransformObj, true, true);
		gameObjects.add(rootGameObject);

		RootObject skyboxRootObject = new RootObject();
		SkyBox skyBox = new SkyBox(skyboxRootObject, "/textures/2k_neptune.jpg", SkyboxType.SPHERE, 10000);
		gameObjects.add(skyboxRootObject);

		Transform waterTransform = new TransformBuilder()
				.reset()
				.setPosition(new Vec3f(0, 0, 0))
				.build();
		TransformSceneGraph waterTransformObj = new TransformSceneGraph(rootGameObject, waterTransform);
		WaterSceneObject water = new WaterSceneObject(waterTransformObj, "/textures/waterDuDvMap.jpg", "/textures/waterNormalMap.jpg", size, 10, 100);


		WindowInitialisationParametersBuilder windowInitialisationParametersBuilder = new WindowInitialisationParametersBuilder();
		windowInitialisationParametersBuilder.setLockCursor(true);

		try (Window window = new Window()) {

			window.init(windowInitialisationParametersBuilder.build());

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directTransformController);

			ChunkLoader chunkLoader = new ChunkLoader(gameObjects, 5, 2);

			waterTransform.setPosition(new Vec3f(cameraTransform.getPosition().getX() - (size*100/2), cameraTransform.getPosition().getY() - (size*100/2), 0));

			while (!window.shouldClose()) {

				LWJGLGameControlManager.checkInputs();

				chunkLoader.loadChunk(cameraTransform.getPosition());

				window.loop(gameObjects, new ArrayList<>(), cameraGameObject.getSceneGraphNodeData().getUuid());


			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	@Test
	void terrain() {

		ArrayList<RootObject> gameObjects = new ArrayList<>();

		RootObject rootGameObject = new RootObject();

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
				10,
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

		WaterSceneObject water = new WaterSceneObject(rootGameObject, "/textures/waterDuDvMap.jpg", "/textures/waterNormalMap.jpg", size, 0, 2);

		MeshObject meshGroupLight = new MeshBuilder()
				.setInvertedNormals(true)
				.build();

		DirectionalLight sun = new DirectionalLight(
				new Vec3f(0.9f, 1.0f, 1.0f),
				Vec3f.Y.add(Vec3f.Z.neg()),
				0.5f);

		LightSceneGraph lightGameObject = new LightSceneGraph(rootGameObject, sun);

		Camera camera = new Camera(1.22173f, 1, 100000);
		Transform cameraTransform = new TransformBuilder()
				.setPosition(new Vec3f(0, 0, 100))
				.setScale(Vec3f.ONE)
				.setRotation(cameraRotation)
				.build();
		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);
		DirectTransformController directTransformController = new DirectTransformController(cameraTransformGameObject, true, true);
		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera);
		gameObjects.add(rootGameObject);

		WindowInitialisationParametersBuilder windowInitialisationParametersBuilder = new WindowInitialisationParametersBuilder();

		try (Window window = new Window()) {

			window.init(windowInitialisationParametersBuilder.build());

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directTransformController);


			while (!window.shouldClose()) {

				window.loop(gameObjects, new ArrayList<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Test
	void terrain3D() {

		ArrayList<RootObject> gameObjects = new ArrayList<>();

		RootObject rootGameObject = new RootObject();

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


		Camera camera = new Camera(1.22173f, 1, 100000);
		Transform cameraTransform = transformBuilder
				.setPosition(new Vec3f(0, 0, 100))
				.setScale(Vec3f.ONE)
				.setRotation(cameraRotation)
				.build();
		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);
		DirectTransformController directTransformController = new DirectTransformController(cameraTransformGameObject, true, true);
		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera);
		gameObjects.add(rootGameObject);

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

		WindowInitialisationParametersBuilder windowInitialisationParametersBuilder = new WindowInitialisationParametersBuilder();

		try (Window window = new Window()) {

			window.init(windowInitialisationParametersBuilder.build());

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directTransformController);

			window.getScene().setAmbientLight(new Vec3f(0.9765f/2, 0.8431f/2, 0.1098f/2));

			while (!window.shouldClose()) {

				window.loop(gameObjects, new ArrayList<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	HashMap<String, TransformSceneGraph> cubeMap = new HashMap<>();

	@Test
	void infiniteTerrain3D() {

		ArrayList<RootObject> gameObjects = new ArrayList<>();

		RootObject rootGameObject = new RootObject();

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




		SkyBox skyBox = new SkyBox(rootGameObject, "/textures/2k_neptune.jpg", SkyboxType.SPHERE);

		Camera camera = new Camera(1.22173f, 1, 100000);
		Transform cameraTransform = transformBuilder
				.setPosition(new Vec3f(0, 0, 100))
				.setScale(Vec3f.ONE)
				.setRotation(cameraRotation)
				.build();
		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(rootGameObject, cameraTransform);
		DirectTransformController directTransformController = new DirectTransformController(cameraTransformGameObject, true, true);
		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera);
		gameObjects.add(rootGameObject);


		WindowInitialisationParametersBuilder windowInitialisationParametersBuilder = new WindowInitialisationParametersBuilder();

		try (Window window = new Window()) {

			window.init(windowInitialisationParametersBuilder.build());

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directTransformController);

			ArrayList<RootObject> objectObjectArrayList = new ArrayList<>();

			while (!window.shouldClose()) {

				window.loop(gameObjects, objectObjectArrayList, cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

				createMap(cameraTransform.getPosition(), cullCube, perlin3D, perlin2D, cubeSize, gameObjects, hillHeight, cubeFire, cubeSand, cubeGrass, cubeSnow);

				if (cubeMap.size() > 1000) {
					cullCubes(rootGameObject, cameraTransform.getPosition());
				}

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	private void cullCubes(RootObject rootGameObject, Vec3f pos) {

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
	                       ArrayList<RootObject> sceneGraphArrayList,
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

									RootObject rootObject = new RootObject();

									Transform transform = transformBuilder
											.setPosition(new Vec3f(i * cubeSize, j * cubeSize, k * cubeSize)).build();

									TransformSceneGraph transformSceneGraph = new TransformSceneGraph(rootObject, transform);

									if (k < 2) {
										MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeFire);
									} else if (k < 100) {
										MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeSand);
									} else {
										MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, cubeGrass);
									}
									cubeMap.put(index, transformSceneGraph);
									sceneGraphArrayList.add(rootObject);
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

		ArrayList<RootObject> gameObjects = new ArrayList<>();

		RootObject rootGameObject = new RootObject();

		TransformBuilder transformBuilder = new TransformBuilder();

		Transform transform = transformBuilder
				.setPosition(Vec3f.ZERO).build();

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

		Transform textTransform = transformBuilder
				.setPosition(new Vec3f(0, 10, 0))
				.setScale(Vec3f.ONE.scale(100)).build();

		transformBuilder.setPosition(Vec3f.ZERO);

		TransformSceneGraph textTransformSceneGraph = new TransformSceneGraph(rootGameObject, textTransform);

		TextItem textItem = (TextItem) new MeshBuilder()
				.setMeshType(MeshType.TEXT)
				.build();

		MeshSceneGraph textMeshObject = new MeshSceneGraph(textTransformSceneGraph, textItem);

		MeshObject meshGroupLight = new MeshBuilder()
				.setMeshType(MeshType.MODEL)
				.setInvertedNormals(false)
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


		SkyBox skyBox = new SkyBox(rootGameObject, "/textures/altimeterSphere.png", SkyboxType.SPHERE);

		Creation.CreateAxis(wholeSceneTransform);
		Creation.CreateLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -10), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);
		Creation.CreateLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0.0f), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);
		Creation.CreateLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -10.0f, 0), Vec3f.ONE.scale(0.5f), QuaternionF.Identity, meshGroupLight);

		Camera camera = new Camera(1.22173f, 1, 100000);
		Transform cameraTransform = transformBuilder
				.setPosition(new Vec3f(-10, 0, 0))
				.setScale(Vec3f.ONE)
				.setRotation(cameraRotation)
				.build();
		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(wholeSceneTransform, cameraTransform);
		DirectTransformController directTransformController = new DirectTransformController(cameraTransformGameObject, true, true);
		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera);
		gameObjects.add(rootGameObject);

		WindowInitialisationParametersBuilder windowInitialisationParametersBuilder = new WindowInitialisationParametersBuilder();
		windowInitialisationParametersBuilder.setLockCursor(true);

		try (Window window = new Window()) {

			window.init(windowInitialisationParametersBuilder.build());

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directTransformController);

			while (!window.shouldClose()) {

				window.loop(gameObjects, new ArrayList<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

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

	@Test
	public void particleSystem() {
		ArrayList<RootObject> gameObjects = new ArrayList<>();

		RootObject rootGameObject = new RootObject();

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

		Creation.CreateLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -1), Vec3f.ONE, QuaternionF.Identity, point);
		Creation.CreateLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0.0f), Vec3f.ONE, QuaternionF.Identity, point);
		Creation.CreateLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0), Vec3f.ONE, QuaternionF.Identity, point);

		Camera camera = new Camera(1.22173f, 1, 100000);

		Transform cameraTransform = transformBuilder
				.setPosition(Vec3f.X)
				.build();

		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(wholeSceneTransform, cameraTransform);
		DirectTransformController directTransformController = new DirectTransformController(wholeSceneTransform, true, true);

		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera);
		DirectTransformController directCameraController = new DirectTransformController(cameraTransformGameObject, true, true);



		gameObjects.add(rootGameObject);

		WindowInitialisationParametersBuilder windowInitialisationParametersBuilder = new WindowInitialisationParametersBuilder();

		try (Window window = new Window()) {

			window.init(windowInitialisationParametersBuilder.build());

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directCameraController);

			while (!window.shouldClose()) {

				window.loop(gameObjects, new ArrayList<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Test
	public void mase() {
		ArrayList<RootObject> gameObjects = new ArrayList<>();

		RootObject rootGameObject = new RootObject();

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

		Creation.CreateLight(pointLight, wholeSceneTransform, new Vec3f(0.0f, 0.0f, -1), Vec3f.ONE, QuaternionF.Identity, point);
		Creation.CreateLight(spotLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0.0f), Vec3f.ONE, QuaternionF.Identity, point);
		Creation.CreateLight(directionalLight, wholeSceneTransform, new Vec3f(0.0f, -1.0f, 0), Vec3f.ONE, QuaternionF.Identity, point);
		Camera camera = new Camera(1.22173f, 1, 100000);
		Transform cameraTransform = transformBuilder
				.setPosition(new Vec3f(-10, 0, 0))
				.setScale(Vec3f.ONE)
				.setRotation(cameraRotation)
				.build();
		TransformSceneGraph cameraTransformGameObject = new TransformSceneGraph(wholeSceneTransform, cameraTransform);
		DirectTransformController directTransformController = new DirectTransformController(cameraTransformGameObject, true, true);
		CameraSceneGraph cameraGameObject = new CameraSceneGraph(cameraTransformGameObject, camera);
		gameObjects.add(rootGameObject);

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

		WindowInitialisationParametersBuilder windowInitialisationParametersBuilder = new WindowInitialisationParametersBuilder();

		try (Window window = new Window()) {

			window.init(windowInitialisationParametersBuilder.build());

			LWJGLGameControlManager LWJGLGameControlManager = new LWJGLGameControlManager(window.getGraphicsLibraryInput(), directTransformController);

			while (!window.shouldClose()) {

				window.loop(gameObjects, new ArrayList<>(), cameraGameObject.getSceneGraphNodeData().getUuid());

				LWJGLGameControlManager.checkInputs();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}