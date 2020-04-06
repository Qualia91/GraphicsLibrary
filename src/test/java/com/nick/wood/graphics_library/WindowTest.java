package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.game_objects.GameObject;
import com.nick.wood.graphics_library.input.Inputs;
import com.nick.wood.graphics_library.mesh_objects.MeshGroup;
import com.nick.wood.graphics_library.mesh_objects.MeshTransform;
import com.nick.wood.graphics_library.mesh_objects.ModelMesh;
import com.nick.wood.graphics_library.mesh_objects.Sphere;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

class WindowTest {

	@Test
	public void test() {

		HashMap<UUID, GameObject> gameObjects = new HashMap<>();

		MeshGroup meshGroup = new MeshGroup();
		meshGroup.getMeshObjectArray().add(new ModelMesh(
					new MeshTransform(
							new Vec3d(10.0, 0.0, 0.0),
					Vec3d.ONE,
					Matrix4d.Identity),
				"D:\\Software\\Programming\\projects\\Java\\GraphicsLibrary\\src\\main\\resources\\models\\dragon.obj",
				"/textures/red.png"
		));

		gameObjects.put(UUID.randomUUID(), new GameObject(
				Vec3d.X,
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

}