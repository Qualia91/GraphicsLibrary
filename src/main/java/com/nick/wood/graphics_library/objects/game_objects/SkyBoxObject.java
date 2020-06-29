package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.UUID;

public class SkyBoxObject implements GameObject {

	private final GameObjectData skyboxSceneGraph;
	private final MeshObject skybox;
	private final String skyboxTexture;
	private final SkyboxType skyboxType;
	private final float distance;

	public SkyBoxObject(GameObject parent, String skyboxTexture, SkyboxType skyboxType, int distance) {
		this.skyboxSceneGraph = new GameObjectData(parent, ObjectType.SKYBOX, this);

		this.skyboxTexture = skyboxTexture;
		this.skyboxType = skyboxType;
		this.distance = distance;

		Transform build = new TransformBuilder()
				.setScale(new Vec3f(distance, distance, distance))
				.setRotation(QuaternionF.RotationY(Math.PI))
				.build();

		switch (skyboxType) {
			case CUBE: skybox = new MeshBuilder()
					.setMeshType(MeshType.CUBOID)
					.setInvertedNormals(true)
					.setTransform(build)
					.setTexture(skyboxTexture)
					.build();
			break;
			default: skybox = new MeshBuilder()
					.setMeshType(MeshType.SPHERE)
					.setInvertedNormals(true)
					.setTriangleNumber(10)
					.setTransform(build)
					.setTexture(skyboxTexture).build();
				break;
		};

		/** for java 14
		 * skybox = switch (skyboxType) {
		 * 			case CUBE -> new MeshBuilder()
		 * 					.setMeshType(MeshType.CUBOID)
		 * 					.setInvertedNormals(false)
		 * 					.setTransform(build)
		 * 					.setTexture(skyboxTexture)
		 * 					.build();
		 * 			default ->  new MeshBuilder()
		 * 					.setMeshType(MeshType.SPHERE)
		 * 					.setInvertedNormals(false)
		 * 					.setTriangleNumber(10)
		 * 					.setTransform(build)
		 * 					.setTexture(skyboxTexture).build();
		 *                };
		 */
	}

	public SkyBoxObject(UUID uuid, GameObject parent, String skyboxTexture, SkyboxType skyboxType, float distance) {
		this.skyboxSceneGraph = new GameObjectData(uuid, parent, ObjectType.SKYBOX, this);

		this.skyboxTexture = skyboxTexture;
		this.skyboxType = skyboxType;
		this.distance = distance;

		Transform build = new TransformBuilder()
				.setScale(new Vec3f(distance, distance, distance))
				.setRotation(QuaternionF.RotationY(Math.PI))
				.build();

		switch (skyboxType) {
			case CUBE: skybox = new MeshBuilder()
					.setMeshType(MeshType.CUBOID)
					.setInvertedNormals(false)
					.setTransform(build)
					.setTexture(skyboxTexture)
					.build();
				break;
			default: skybox = new MeshBuilder()
					.setMeshType(MeshType.SPHERE)
					.setInvertedNormals(false)
					.setTriangleNumber(10)
					.setTransform(build)
					.setTexture(skyboxTexture).build();
				break;
		};

		/** for java 14
		 * skybox = switch (skyboxType) {
		 * 			case CUBE -> new MeshBuilder()
		 * 					.setMeshType(MeshType.CUBOID)
		 * 					.setInvertedNormals(false)
		 * 					.setTransform(build)
		 * 					.setTexture(skyboxTexture)
		 * 					.build();
		 * 			default ->  new MeshBuilder()
		 * 					.setMeshType(MeshType.SPHERE)
		 * 					.setInvertedNormals(false)
		 * 					.setTriangleNumber(10)
		 * 					.setTransform(build)
		 * 					.setTexture(skyboxTexture).build();
		 * 				}
		 */

	}

	public float getDistance() {
		return distance;
	}

	public String getSkyboxTexture() {
		return skyboxTexture;
	}

	public SkyboxType getSkyboxType() {
		return skyboxType;
	}

	public GameObjectData getSkyboxSceneGraph() {
		return skyboxSceneGraph;
	}

	public MeshObject getSkybox() {
		return skybox;
	}

	@Override
	public GameObjectData getGameObjectData() {
		return skyboxSceneGraph;
	}
}
