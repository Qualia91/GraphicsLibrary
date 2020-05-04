package com.nick.wood.graphics_library_3d;

import com.nick.wood.graphics_library_3d.lighting.DirectionalLight;
import com.nick.wood.graphics_library_3d.lighting.Light;
import com.nick.wood.graphics_library_3d.lighting.PointLight;
import com.nick.wood.graphics_library_3d.lighting.SpotLight;
import com.nick.wood.graphics_library_3d.objects.Camera;
import com.nick.wood.graphics_library_3d.objects.scene_graph_objects.*;
import com.nick.wood.graphics_library_3d.input.Inputs;
import com.nick.wood.graphics_library_3d.objects.Transform;
import com.nick.wood.graphics_library_3d.objects.mesh_objects.*;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TestBench {

	@Test
	public void shadow() {

		HashMap<UUID, RootSceneGraph> gameObjects = new HashMap<>();

		RootSceneGraph rootGameObject = new RootSceneGraph();

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
				"",
				gameObjects,
				new Inputs(),
				true, true);

		window.init();

		while (!window.shouldClose()) {

			window.loop(new HashMap<>(), new HashMap<>());

		}

		window.destroy();

	}

	@Test
	public void stress() {

		HashMap<UUID, RootSceneGraph> gameObjects = new HashMap<>();

		RootSceneGraph rootGameObject = new RootSceneGraph();

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
				.setTexture("/textures/sand.jpg")
				.setTransform(Matrix4f.Rotation(-90, Vec3f.X))
				.build();

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

		for (int i = 0; i < 1100; i++) {
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

		ExecutorService executorService = Executors.newFixedThreadPool(2);

		Window window = new Window(
				1200,
				800,
				"",
				gameObjects,
				new Inputs(),
				true, true);

		window.init();

		long oldTime = System.currentTimeMillis();

		while (!window.shouldClose()) {

			window.loop(new HashMap<>(), new HashMap<>());

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

	@Test
	public void normal() {

		HashMap<UUID, RootSceneGraph> gameObjects = new HashMap<>();

		RootSceneGraph rootGameObject = new RootSceneGraph();

		Transform hudTransform = new Transform(
				Vec3f.X,
				Vec3f.ONE.scale(10),
				Matrix4f.Identity
		);

		TransformSceneGraph hudTransformGameObject = new TransformSceneGraph(rootGameObject, hudTransform);

		TextItem textItem = new TextItem("hello", "/font/gothic.bmp", 16, 16);

		MeshSceneGraph textMeshObject = new MeshSceneGraph(hudTransformGameObject, textItem);

		Transform transform = new Transform(
				Vec3f.X.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		TransformSceneGraph wholeSceneTransform = new TransformSceneGraph(rootGameObject, transform);

		MeshObject meshGroupLight = new MeshBuilder()
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

		Camera camera = new Camera(new Vec3f(-10.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), 0.5f, 0.1f);

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

		gameObjects.put(UUID.randomUUID(), rootGameObject);

		Window window = new Window(
				1200,
				800,
				"",
				gameObjects,
				new Inputs(),
				true, true);

		window.init();

		while (!window.shouldClose()) {

			window.loop(new HashMap<>(), new HashMap<>());

		}

		window.destroy();

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