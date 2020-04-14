package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.lighting.DirectionalLight;
import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.lighting.PointLight;
import com.nick.wood.graphics_library.lighting.SpotLight;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.game_objects.*;
import com.nick.wood.graphics_library.input.Inputs;
import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.ModelLoader;
import com.nick.wood.graphics_library.objects.mesh_objects.ModelMesh;
import com.nick.wood.graphics_library.objects.mesh_objects.SingleMesh;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;
import com.nick.wood.maths.objects.Vec3f;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WindowTest {

	@Test
	public void stressTest() {

		HashMap<UUID, RootGameObject> gameObjects = new HashMap<>();

		RootGameObject rootGameObject = new RootGameObject();

		Transform transform = new Transform(
				Vec3d.X.scale(0),
				Vec3d.ONE,
				Matrix4d.Identity
				//Matrix4d.Rotation(90, Vec3d.X)
				//Matrix4d.Rotation(90, Vec3d.Y)
				//Matrix4d.Rotation(90, Vec3d.Z)
		);

		MeshObject meshGroup = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\dragon.obj",
				"/textures/white.png",
				Matrix4d.Rotation(-90, Vec3d.X),
				false
		);

		TransformGameObject wholeSceneTransform = new TransformGameObject(rootGameObject, transform);

		for (int i = 0; i < 1100; i++) {
			createObject(Vec3d.Y.scale(i), wholeSceneTransform, meshGroup);
		}


		createAxis(wholeSceneTransform);

		MeshObject meshGroupLight = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\sphere.obj",
				"/textures/white.png",
				Matrix4d.Rotation(-90, Vec3d.X),
				true
		);



		PointLight pointLight = new PointLight(
				new Vec3d(0.0, 1.0, 0.0),
				10f);
		DirectionalLight directionalLight = new DirectionalLight(
				new Vec3d(1.0, 1.0, 1.0),
				new Vec3d(0.0, 0.0, -1.0),
				0.1f);
		SpotLight spotLight = new SpotLight(
				new PointLight(
						new Vec3d(1.0, 0.0, 0.0),
						100f),
				Vec3f.Y,
				0.1f
		);

		createLight(pointLight, wholeSceneTransform, new Vec3d(0.0, 0.0, -10), Vec3d.ONE.scale(0.5), Matrix4d.Identity, meshGroupLight);
		createLight(spotLight, wholeSceneTransform, new Vec3d(0.0, -10.0, 0.0), Vec3d.ONE.scale(0.5), Matrix4d.Rotation(0.0, Vec3d.Y), meshGroupLight);
		createLight(directionalLight, wholeSceneTransform, new Vec3d(0.0, -10.0, 0), Vec3d.ONE.scale(0.5), Matrix4d.Identity, meshGroupLight);

		Camera camera = new Camera(new Vec3d(0.0, 0.0, 10.0), new Vec3d(0.0, 0.0, 0.0), 0.5, 0.1);

		CameraGameObject cameraGameObject = new CameraGameObject(wholeSceneTransform, camera, CameraType.PRIMARY);

		gameObjects.put(UUID.randomUUID(), rootGameObject);

		ExecutorService executorService = Executors.newFixedThreadPool(2);

		Window window = new Window(
				1200,
				800,
				"",
				gameObjects,
				new Inputs());

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

	private void createObject(Vec3d pos, GameObjectNode parent, MeshObject meshGroup) {

		Transform transformMesh = new Transform(
				pos,
				Vec3d.ONE,
				Matrix4d.Identity
				//Matrix4d.Rotation(90, Vec3d.X)
				//.multiply(Matrix4d.Rotation(90, Vec3d.Y))
				//.multiply(Matrix4d.Rotation(90, Vec3d.Z))
		);
		TransformGameObject meshTransform = new TransformGameObject(parent, transformMesh);
		MeshGameObject meshGameObject = new MeshGameObject(
				meshTransform,
				meshGroup
		);
	}

	@Test
	public void test() {

		HashMap<UUID, RootGameObject> gameObjects = new HashMap<>();

		RootGameObject rootGameObject = new RootGameObject();

		Transform transform = new Transform(
				Vec3d.X.scale(0),
				Vec3d.ONE,
				Matrix4d.Identity
				//Matrix4d.Rotation(90, Vec3d.X)
				//Matrix4d.Rotation(90, Vec3d.Y)
				//Matrix4d.Rotation(90, Vec3d.Z)
		);

		TransformGameObject wholeSceneTransform = new TransformGameObject(rootGameObject, transform);

		MeshObject meshGroup = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\dragon.obj",
				"/textures/white.png",
				Matrix4d.Rotation(-90, Vec3d.X),
				false
		);
		Transform transformMesh = new Transform(
				Vec3d.Z.scale(-2),
				Vec3d.ONE,
				Matrix4d.Identity
				//Matrix4d.Rotation(90, Vec3d.X)
				//.multiply(Matrix4d.Rotation(90, Vec3d.Y))
				//.multiply(Matrix4d.Rotation(90, Vec3d.Z))
		);
		TransformGameObject meshTransform = new TransformGameObject(wholeSceneTransform, transformMesh);
		MeshGameObject meshGameObject = new MeshGameObject(
				meshTransform,
				meshGroup
		);

		createAxis(wholeSceneTransform);

		MeshObject meshGroupLight = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\sphere.obj",
				"/textures/white.png",
				Matrix4d.Rotation(-90, Vec3d.X),
				true
		);



		PointLight pointLight = new PointLight(
				new Vec3d(0.0, 1.0, 0.0),
				10f);
		DirectionalLight directionalLight = new DirectionalLight(
				new Vec3d(1.0, 1.0, 1.0),
				new Vec3d(0.0, 0.0, -1.0),
				1f);
		SpotLight spotLight = new SpotLight(
				new PointLight(
						new Vec3d(1.0, 0.0, 0.0),
						100f),
				Vec3f.Y,
				0.02f
		);

		createLight(pointLight, wholeSceneTransform, new Vec3d(0.0, 0.0, -10), Vec3d.ONE.scale(0.5), Matrix4d.Identity, meshGroupLight);
		createLight(spotLight, wholeSceneTransform, new Vec3d(0.0, -10.0, 0.0), Vec3d.ONE.scale(0.5), Matrix4d.Rotation(0.0, Vec3d.Y), meshGroupLight);
		createLight(directionalLight, wholeSceneTransform, new Vec3d(0.0, -10.0, 0), Vec3d.ONE.scale(0.5), Matrix4d.Identity, meshGroupLight);

		Camera camera = new Camera(new Vec3d(0.0, 0.0, 10.0), new Vec3d(0.0, 0.0, 0.0), 0.5, 0.1);

		CameraGameObject cameraGameObject = new CameraGameObject(wholeSceneTransform, camera, CameraType.PRIMARY);

		gameObjects.put(UUID.randomUUID(), rootGameObject);

		Window window = new Window(
				1200,
				800,
				"",
				gameObjects,
				new Inputs());

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
				Matrix4d.Rotation(-90, Vec3d.X),
				false
		);

		Transform transformMeshX = new Transform(
				Vec3d.X.scale(5),
				Vec3d.ONE.scale(0.1).add(Vec3d.X.scale(10)),
				Matrix4d.Identity
		);
		TransformGameObject meshTransformX = new TransformGameObject(wholeSceneTransform, transformMeshX);
		MeshGameObject meshGameObjectX = new MeshGameObject(
				meshTransformX,
				meshGroupX
		);

		MeshObject meshGroupY = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj",
				"/textures/green.png",
				Matrix4d.Rotation(-90, Vec3d.X),
				false
		);

		Transform transformMeshY = new Transform(
				Vec3d.Y.scale(5),
				Vec3d.ONE.scale(0.1).add(Vec3d.Y.scale(10)),
				Matrix4d.Identity
		);
		TransformGameObject meshTransformY = new TransformGameObject(wholeSceneTransform, transformMeshY);
		MeshGameObject meshGameObjectY = new MeshGameObject(
				meshTransformY,
				meshGroupY
		);

		MeshObject meshGroupZ = new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj",
				"/textures/blue.png",
				Matrix4d.Rotation(-90, Vec3d.X),
				false
		);

		Transform transformMeshZ = new Transform(
				Vec3d.Z.scale(5),
				Vec3d.ONE.scale(0.1).add(Vec3d.Z.scale(10)),
				Matrix4d.Identity
		);
		TransformGameObject meshTransformZ = new TransformGameObject(wholeSceneTransform, transformMeshZ);
		MeshGameObject meshGameObjectZ = new MeshGameObject(
				meshTransformZ,
				meshGroupZ
		);
	}

	private void createLight(Light light, GameObjectNode parent, Vec3d position, Vec3d scale, Matrix4d rotation, MeshObject meshGroup) {
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