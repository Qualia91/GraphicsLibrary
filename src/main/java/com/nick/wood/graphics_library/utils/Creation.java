package com.nick.wood.graphics_library.utils;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.graphics_library.objects.game_objects.LightSceneGraph;
import com.nick.wood.graphics_library.objects.game_objects.MeshSceneGraph;
import com.nick.wood.graphics_library.objects.game_objects.SceneGraphNode;
import com.nick.wood.graphics_library.objects.game_objects.TransformSceneGraph;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Creation {

	static public void CreateLight(Light light, SceneGraphNode parent, Vec3f position, Vec3f scale,
	                         QuaternionF rotation, MeshObject meshGroup) {

		TransformBuilder transformBuilder = new TransformBuilder(
				position,
				scale,
				rotation
		);

		Transform lightGameObjectTransform = transformBuilder.build();

		TransformSceneGraph transformGameObject = new TransformSceneGraph(parent, lightGameObjectTransform);
		LightSceneGraph lightGameObject = new LightSceneGraph(transformGameObject, light);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				transformGameObject,
				meshGroup
		);
	}

	static public void CreateLight(Light light, SceneGraphNode parent, Vec3f position, Vec3f scale,
	                               QuaternionF rotation) {

		TransformBuilder transformBuilder = new TransformBuilder(
				position,
				scale,
				rotation
		);

		Transform lightGameObjectTransform = transformBuilder.build();

		TransformSceneGraph transformGameObject = new TransformSceneGraph(parent, lightGameObjectTransform);
		LightSceneGraph lightGameObject = new LightSceneGraph(transformGameObject, light);

	}

	static public void CreateLight(Light light, SceneGraphNode parent, Transform lightGameObjectTransform, MeshObject
			meshGroup) {
		TransformSceneGraph transformGameObject = new TransformSceneGraph(parent, lightGameObjectTransform);
		LightSceneGraph lightGameObject = new LightSceneGraph(transformGameObject, light);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				transformGameObject,
				meshGroup
		);
	}

	static public void CreateLight(Light light, SceneGraphNode parent, Transform lightGameObjectTransform) {
		TransformSceneGraph transformGameObject = new TransformSceneGraph(parent, lightGameObjectTransform);
		LightSceneGraph lightGameObject = new LightSceneGraph(transformGameObject, light);
	}

	static public void CreateAxis(TransformSceneGraph wholeSceneTransform) {

		TransformBuilder transformBuilder = new TransformBuilder();

		MeshObject meshGroupX = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/red.png")
				.build();

		Transform transformMeshX = transformBuilder
				.setPosition(Vec3f.X.scale(2))
				.setScale(Vec3f.ONE.scale(0.1f).add(Vec3f.X.scale(10)))
				.build();
		TransformSceneGraph meshTransformX = new TransformSceneGraph(wholeSceneTransform, transformMeshX);
		MeshSceneGraph meshGameObjectX = new MeshSceneGraph(
				meshTransformX,
				meshGroupX
		);

		MeshObject meshGroupY = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/blue.png")
				.build();

		Transform transformMeshY = transformBuilder
				.setPosition(Vec3f.Y.scale(2))
				.setRotation(QuaternionF.RotationZ((float) Math.PI/2))
				.build();

		TransformSceneGraph meshTransformY = new TransformSceneGraph(wholeSceneTransform, transformMeshY);
		MeshSceneGraph meshGameObjectY = new MeshSceneGraph(
				meshTransformY,
				meshGroupY
		);

		MeshObject meshGroupZ = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/green.png")
				.build();

		Transform transformMeshZ = transformBuilder
				.setPosition(Vec3f.Z.scale(2))
				.setRotation(QuaternionF.RotationY((float) Math.PI/2))
				.build();

		TransformSceneGraph meshTransformZ = new TransformSceneGraph(wholeSceneTransform, transformMeshZ);
		MeshSceneGraph meshGameObjectZ = new MeshSceneGraph(
				meshTransformZ,
				meshGroupZ
		);
	}

	static public Transform CreateObject(Vec3f pos, SceneGraphNode parent, MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos).build();

		TransformSceneGraph meshTransform = new TransformSceneGraph(parent, transformMesh);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				meshTransform,
				meshGroup
		);

		return transformMesh;
	}

	static public Transform CreateObject(Vec3f pos, QuaternionF rotation, SceneGraphNode parent, MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos)
				.setRotation(rotation)
				.build();

		TransformSceneGraph meshTransform = new TransformSceneGraph(parent, transformMesh);
		MeshSceneGraph meshGameObject = new MeshSceneGraph(
				meshTransform,
				meshGroup
		);

		return transformMesh;
	}

	static public MeshSceneGraph CreateObjectAndGetSceneObject(Vec3f pos, SceneGraphNode parent, MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos).build();

		TransformSceneGraph meshTransform = new TransformSceneGraph(parent, transformMesh);

		return new MeshSceneGraph(
				meshTransform,
				meshGroup
		);
	}

	static public MeshSceneGraph CreateObjectAndGetSceneObject(Vec3f pos, QuaternionF rotation, SceneGraphNode parent, MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos)
				.setRotation(rotation)
				.build();

		TransformSceneGraph meshTransform = new TransformSceneGraph(parent, transformMesh);

		return new MeshSceneGraph(
				meshTransform,
				meshGroup
		);
	}

	static public MeshSceneGraph CreateObjectAndGetSceneObject(Vec3f pos, Vec3f scale, QuaternionF rotation, SceneGraphNode parent, MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setScale(scale)
				.setPosition(pos)
				.setRotation(rotation)
				.build();

		TransformSceneGraph meshTransform = new TransformSceneGraph(parent, transformMesh);

		return new MeshSceneGraph(
				meshTransform,
				meshGroup
		);
	}

}
