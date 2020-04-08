package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.lighting.DirectionalLight;
import com.nick.wood.graphics_library.lighting.PointLight;
import com.nick.wood.graphics_library.lighting.SpotLight;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.game_objects.*;
import com.nick.wood.graphics_library.input.Inputs;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshGroup;
import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.graphics_library.objects.mesh_objects.ModelMesh;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;
import com.nick.wood.maths.objects.Vec3f;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

class WindowTest {

	@Test
	public void testDragon() {

		HashMap<UUID, RootGameObject> gameObjects = new HashMap<>();

		MeshGroup meshGroup = new MeshGroup();
		meshGroup.getMeshObjectArray().add(new ModelMesh(
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\dragon.obj",
				"/textures/white.png"
		));

		RootGameObject rootGameObject = new RootGameObject();

		MeshGameObject meshGameObject = new MeshGameObject(
				rootGameObject,
				meshGroup
		);

		// todo this obs needs to go somewhere else
		PointLight pointLight = new PointLight(
				new Vec3d(0.0, 1.0, 0.0),
				new Vec3d(0.0, -10.0, 0),
				10f);
		DirectionalLight directionalLight = new DirectionalLight(
				new Vec3d(0.0, 0.0, 1.0),
				new Vec3d(0.0, 0.0, 10.0),
				1f);
		SpotLight spotLight = new SpotLight(
				new PointLight(
						new Vec3d(1.0, 0.0, 0.0),
						new Vec3d(0.0, -10.0, 2.0),
						100f),
				Vec3f.Y,
				0.01f
		);

		LightGameObject lightGameObject1 = new LightGameObject(rootGameObject, pointLight);
		LightGameObject lightGameObject2 = new LightGameObject(rootGameObject, directionalLight);
		LightGameObject lightGameObject3 = new LightGameObject(rootGameObject, spotLight);

		Camera camera = new Camera(new Vec3d(-5, 0.0, 0.0), new Vec3d(-90.0, 180.0, 90.0), 0.5, 0.1);

		CameraGameObject cameraGameObject = new CameraGameObject(rootGameObject, camera, CameraType.PRIMARY);

		gameObjects.put(UUID.randomUUID(), rootGameObject);

		Window window = new Window(
				900,
				600,
				"",
				gameObjects,
				new Inputs());

		window.init();

		while (!window.shouldClose()) {

			window.loop();

		}

		window.destroy();

	}

}