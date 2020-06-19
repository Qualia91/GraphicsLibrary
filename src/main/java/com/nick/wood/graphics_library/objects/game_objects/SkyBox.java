package com.nick.wood.graphics_library.objects.game_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec3f;

public class SkyBox implements SceneGraphNode {

	private final SceneGraphNodeData skyboxSceneGraph;
	private final MeshObject skybox;
	private final String skyboxTexture;
	private final SkyboxType skyboxType;


	public SkyBox(RootObject parent, String skyboxTexture, SkyboxType skyboxType) {
		this.skyboxSceneGraph = new SceneGraphNodeData(parent, RenderObjectType.SKYBOX, this);

		this.skyboxTexture = skyboxTexture;
		this.skyboxType = skyboxType;

		Transform build = new TransformBuilder()
				.setScale(new Vec3f(1000, 1000, 1000))
				.setRotation(QuaternionF.RotationY(Math.PI))
				.build();

		skybox = switch (skyboxType) {
			case CUBE -> new MeshBuilder()
					.setMeshType(MeshType.CUBOID)
					.setInvertedNormals(false)
					.setTransform(build)
					.setTexture(skyboxTexture)
					.build();
			default ->  new MeshBuilder()
					.setMeshType(MeshType.SPHERE)
					.setInvertedNormals(false)
					.setTriangleNumber(10)
					.setTransform(build)
					.setTexture(skyboxTexture).build();
		};

	}

	public String getSkyboxTexture() {
		return skyboxTexture;
	}

	public SkyboxType getSkyboxType() {
		return skyboxType;
	}

	public SceneGraphNodeData getSkyboxSceneGraph() {
		return skyboxSceneGraph;
	}

	public MeshObject getSkybox() {
		return skybox;
	}

	@Override
	public SceneGraphNodeData getSceneGraphNodeData() {
		return skyboxSceneGraph;
	}
}
