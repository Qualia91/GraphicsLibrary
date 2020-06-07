package com.nick.wood.graphics_library.objects.scene_graph_objects;

import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec3f;

public class SkyBox implements SceneGraphNode {

	private final SceneGraphNodeData skyboxSceneGraph;
	private final MeshObject skybox;


	public SkyBox(SceneGraph parent, String skyboxTexture, SkyboxType skyboxType) {
		this.skyboxSceneGraph = new SceneGraphNodeData(parent, RenderObjectType.SKYBOX, this);

		Transform build = new TransformBuilder()
				.setScale(new Vec3f(10000, 10000, 10000)).build();

		skybox = switch (skyboxType) {
			case CUBE -> new MeshBuilder().setMeshType(MeshType.CUBOID).setInvertedNormals(true).setTransform(build).setTexture(skyboxTexture).build();
			default ->  new MeshBuilder().setMeshType(MeshType.MODEL).setInvertedNormals(true).setTransform(build).setTexture(skyboxTexture).build();
		};

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
