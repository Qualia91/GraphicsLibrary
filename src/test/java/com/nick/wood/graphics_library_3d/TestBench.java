package com.nick.wood.graphics_library_3d;

import com.nick.wood.graphics_library_3d.lighting.DirectionalLight;
import com.nick.wood.graphics_library_3d.lighting.Light;
import com.nick.wood.graphics_library_3d.lighting.PointLight;
import com.nick.wood.graphics_library_3d.lighting.SpotLight;
import com.nick.wood.graphics_library_3d.objects.Camera;
import com.nick.wood.graphics_library_3d.objects.game_objects.*;
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

		HashMap<UUID, RootGameObject> gameObjects = new HashMap<>();

		RootGameObject rootGameObject = new RootGameObject();

		Transform transform = new Transform(
				Vec3f.X.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		TransformGameObject wholeSceneTransform = new TransformGameObject(rootGameObject, transform);

		//MeshObject cubeMesh = new ModelMesh(
		//		"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj",
		//		"/textures/white.png",
		//		Matrix4f.Rotation(-90, Vec3f.X),
		//		false
		//);
		
		MeshObject cubeMesh = new CubeMesh(false, new Material("/textures/white.png"));
		Transform transformMesh = new Transform(
				Vec3f.Z.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//.multiply(Matrix4f.Rotation(90, Vec3f.Y))
				//.multiply(Matrix4f.Rotation(90, Vec3f.Z))
		);
		TransformGameObject meshTransform = new TransformGameObject(wholeSceneTransform, transformMesh);
		MeshGameObject meshGameObject = new MeshGameObject(
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
		TransformGameObject meshTransformWall = new TransformGameObject(wholeSceneTransform, transformMeshWall);
		MeshGameObject meshGameObjectWall = new MeshGameObject(
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

		MeshObject sphereMesh = new SphereMesh(10, new Material("/textures/sand.jpg"), true);

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

		TransformGameObject cameraTransformGameObject = new TransformGameObject(wholeSceneTransform, cameraTransform);

		CameraGameObject cameraGameObject = new CameraGameObject(cameraTransformGameObject, camera, CameraType.PRIMARY);

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

			window.loop();

		}

		window.destroy();

	}

	@Test
	public void stress() {

		HashMap<UUID, RootGameObject> gameObjects = new HashMap<>();

		RootGameObject rootGameObject = new RootGameObject();

		Transform transform = new Transform(
				Vec3f.X.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		MeshObject meshGroup = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\dragon.obj",
				"/textures/white.png",
				Matrix4f.Rotation(-90, Vec3f.X),
				false
		);

		TransformGameObject wholeSceneTransform = new TransformGameObject(rootGameObject, transform);

		for (int i = 0; i < 1100; i++) {
			createObject(Vec3f.Y.scale(i), wholeSceneTransform, meshGroup);
		}


		createAxis(wholeSceneTransform);

		MeshObject meshGroupLight = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\sphere.obj",
				"/textures/white.png",
				Matrix4f.Rotation(-90, Vec3f.X),
				true
		);



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

		CameraGameObject cameraGameObject = new CameraGameObject(wholeSceneTransform, camera, CameraType.PRIMARY);

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

			window.loop();

			long currentTime = System.currentTimeMillis();

			window.setTitle("Diff Time: " + (currentTime - oldTime));

			oldTime = currentTime;

		}

		window.destroy();

	}

	private void createObject(Vec3f pos, GameObjectNode parent, MeshObject meshGroup) {

		Transform transformMesh = new Transform(
				pos,
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//.multiply(Matrix4f.Rotation(90, Vec3f.Y))
				//.multiply(Matrix4f.Rotation(90, Vec3f.Z))
		);
		TransformGameObject meshTransform = new TransformGameObject(parent, transformMesh);
		MeshGameObject meshGameObject = new MeshGameObject(
				meshTransform,
				meshGroup
		);
	}

	@Test
	public void normal() {

		HashMap<UUID, RootGameObject> gameObjects = new HashMap<>();

		RootGameObject rootGameObject = new RootGameObject();

		Transform hudTransform = new Transform(
				Vec3f.X,
				Vec3f.ONE.scale(10),
				Matrix4f.Identity
		);

		TransformGameObject hudTransformGameObject = new TransformGameObject(rootGameObject, hudTransform);

		TextItem textItem = new TextItem("hello", "/font/gothic.bmp", 15, 17);

		MeshGameObject textMeshObject = new MeshGameObject(hudTransformGameObject, textItem);

		Transform transform = new Transform(
				Vec3f.X.scale(0),
				Vec3f.ONE,
				Matrix4f.Identity
				//Matrix4f.Rotation(90, Vec3f.X)
				//Matrix4f.Rotation(90, Vec3f.Y)
				//Matrix4f.Rotation(90, Vec3f.Z)
		);

		TransformGameObject wholeSceneTransform = new TransformGameObject(rootGameObject, transform);

		MeshObject meshGroupLight =  new SphereMesh(10,
				new Material("/textures/white.png"),
				false
		);

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

		TransformGameObject cameraTransformGameObject = new TransformGameObject(wholeSceneTransform, cameraTransform);

		CameraGameObject cameraGameObject = new CameraGameObject(cameraTransformGameObject, camera, CameraType.PRIMARY);

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

			window.loop();

		}

		window.destroy();

	}

	private void createAxis(TransformGameObject wholeSceneTransform) {
		MeshObject meshGroupX = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj",
				"/textures/red.png",
				Matrix4f.Rotation(-90, Vec3f.X),
				false
		);

		Transform transformMeshX = new Transform(
				Vec3f.X.scale(5),
				Vec3f.ONE.scale(0.1f).add(Vec3f.X.scale(10)),
				Matrix4f.Identity
		);
		TransformGameObject meshTransformX = new TransformGameObject(wholeSceneTransform, transformMeshX);
		MeshGameObject meshGameObjectX = new MeshGameObject(
				meshTransformX,
				meshGroupX
		);

		MeshObject meshGroupY = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj",
				"/textures/green.png",
				Matrix4f.Rotation(-90, Vec3f.X),
				false
		);

		Transform transformMeshY = new Transform(
				Vec3f.Y.scale(5),
				Vec3f.ONE.scale(0.1f).add(Vec3f.Y.scale(10)),
				Matrix4f.Identity
		);
		TransformGameObject meshTransformY = new TransformGameObject(wholeSceneTransform, transformMeshY);
		MeshGameObject meshGameObjectY = new MeshGameObject(
				meshTransformY,
				meshGroupY
		);

		MeshObject meshGroupZ = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj",
				"/textures/blue.png",
				Matrix4f.Rotation(-90, Vec3f.X),
				false
		);

		Transform transformMeshZ = new Transform(
				Vec3f.Z.scale(5),
				Vec3f.ONE.scale(0.1f).add(Vec3f.Z.scale(10)),
				Matrix4f.Identity
		);
		TransformGameObject meshTransformZ = new TransformGameObject(wholeSceneTransform, transformMeshZ);
		MeshGameObject meshGameObjectZ = new MeshGameObject(
				meshTransformZ,
				meshGroupZ
		);
	}

	private void createLight(Light light, GameObjectNode parent, Vec3f position, Vec3f scale, Matrix4f rotation, MeshObject meshGroup) {
		Transform lightGameObjectTransform = new Transform(
				position,
				scale,
				rotation
		);
		TransformGameObject transformGameObject = new TransformGameObject(parent, lightGameObjectTransform);
		LightGameObject lightGameObject = new LightGameObject(transformGameObject, light);
		MeshGameObject meshGameObject = new MeshGameObject(
				transformGameObject,
				meshGroup
		);
	}

	private void createLight(Light light, GameObjectNode parent, Transform lightGameObjectTransform, MeshObject meshGroup) {
		TransformGameObject transformGameObject = new TransformGameObject(parent, lightGameObjectTransform);
		LightGameObject lightGameObject = new LightGameObject(transformGameObject, light);
		MeshGameObject meshGameObject = new MeshGameObject(
				transformGameObject,
				meshGroup
		);
	}

}