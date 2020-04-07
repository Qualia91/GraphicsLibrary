package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.objects.game_objects.GameObject;
import com.nick.wood.graphics_library.input.Inputs;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshGroup;
import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.graphics_library.objects.mesh_objects.ModelMesh;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

class WindowTest {

	@Test
	public void testWall() {

		HashMap<UUID, GameObject> gameObjects = new HashMap<>();

		MeshGroup meshGroup = new MeshGroup();
		meshGroup.getMeshObjectArray().add(new ModelMesh(
				new Transform(
						new Vec3d(0.0, 10.0, 0.0),
						Vec3d.ONE,
						Matrix4d.Identity),
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\cube.obj",
				"/textures/white.png"
		));



		gameObjects.put(UUID.randomUUID(), new GameObject(
				Vec3d.ZERO,
				Matrix4d.Identity,
				new Vec3d(100.0, 1, 100),
				meshGroup,
				false
		));


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

	@Test
	public void testSpheresInMeshGroup() {

		HashMap<UUID, GameObject> gameObjects = new HashMap<>();

		MeshGroup meshGroup = new MeshGroup();
		for (int i = 0; i < 40; i+=4) {
			meshGroup.getMeshObjectArray().add(new ModelMesh(
					new Transform(
							new Vec3d(i, 0.0, 0.0),
							Vec3d.ONE,
							Matrix4d.Identity),
					"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\sphere.obj",
					"/textures/white.png"
			));
		}



		gameObjects.put(UUID.randomUUID(), new GameObject(
				Vec3d.ZERO,
				Matrix4d.Identity,
				Vec3d.ONE,
				meshGroup,
				false
		));


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

	@Test
	public void testSpheresInGameObjectsGroup() {

		HashMap<UUID, GameObject> gameObjects = new HashMap<>();

		MeshGroup meshGroup = new MeshGroup();
		meshGroup.getMeshObjectArray().add(new ModelMesh(
				new Transform(
						new Vec3d(0.0, 0.0, 0.0),
						Vec3d.ONE,
						Matrix4d.Identity),
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\sphere.obj",
				"/textures/white.png"
		));



		for (int i = 0; i < 40; i+=4) {
			gameObjects.put(UUID.randomUUID(), new GameObject(
					new Vec3d(i, 0.0, 0.0),
					Matrix4d.Identity,
					Vec3d.ONE,
					meshGroup,
					false
			));
		}

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