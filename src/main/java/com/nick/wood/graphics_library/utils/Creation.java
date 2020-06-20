package com.nick.wood.graphics_library.utils;

import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.graphics_library.objects.game_objects.LightObject;
import com.nick.wood.graphics_library.objects.game_objects.MeshObject;
import com.nick.wood.graphics_library.objects.game_objects.GameObject;
import com.nick.wood.graphics_library.objects.game_objects.TransformObject;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Creation {

	static public void CreateLight(Light light, GameObject parent, Vec3f position, Vec3f scale,
	                               QuaternionF rotation, com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshGroup) {

		TransformBuilder transformBuilder = new TransformBuilder(
				position,
				scale,
				rotation
		);

		Transform lightGameObjectTransform = transformBuilder.build();

		TransformObject transformGameObject = new TransformObject(parent, lightGameObjectTransform);
		LightObject lightObject = new LightObject(transformGameObject, light);
		MeshObject meshObject = new MeshObject(
				transformGameObject,
				meshGroup
		);
	}

	static public void CreateLight(Light light, GameObject parent, Vec3f position, Vec3f scale,
	                               QuaternionF rotation) {

		TransformBuilder transformBuilder = new TransformBuilder(
				position,
				scale,
				rotation
		);

		Transform lightGameObjectTransform = transformBuilder.build();

		TransformObject transformGameObject = new TransformObject(parent, lightGameObjectTransform);
		LightObject lightObject = new LightObject(transformGameObject, light);

	}

	static public void CreateLight(Light light, GameObject parent, Transform lightGameObjectTransform, com.nick.wood.graphics_library.objects.mesh_objects.MeshObject
			meshGroup) {
		TransformObject transformGameObject = new TransformObject(parent, lightGameObjectTransform);
		LightObject lightObject = new LightObject(transformGameObject, light);
		MeshObject meshObject = new MeshObject(
				transformGameObject,
				meshGroup
		);
	}

	static public void CreateLight(Light light, GameObject parent, Transform lightGameObjectTransform) {
		TransformObject transformGameObject = new TransformObject(parent, lightGameObjectTransform);
		LightObject lightObject = new LightObject(transformGameObject, light);
	}

	static public void CreateAxis(TransformObject wholeSceneTransform) {

		TransformBuilder transformBuilder = new TransformBuilder();

		com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshGroupX = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/red.png")
				.build();

		Transform transformMeshX = transformBuilder
				.setPosition(Vec3f.X.scale(2))
				.setScale(Vec3f.ONE.scale(0.1f).add(Vec3f.X.scale(10)))
				.build();
		TransformObject meshTransformX = new TransformObject(wholeSceneTransform, transformMeshX);
		MeshObject meshObjectX = new MeshObject(
				meshTransformX,
				meshGroupX
		);

		com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshGroupY = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/blue.png")
				.build();

		Transform transformMeshY = transformBuilder
				.setPosition(Vec3f.Y.scale(2))
				.setRotation(QuaternionF.RotationZ((float) Math.PI/2))
				.build();

		TransformObject meshTransformY = new TransformObject(wholeSceneTransform, transformMeshY);
		MeshObject meshObjectY = new MeshObject(
				meshTransformY,
				meshGroupY
		);

		com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshGroupZ = new MeshBuilder()
				.setMeshType(MeshType.CUBOID)
				.setTexture("/textures/green.png")
				.build();

		Transform transformMeshZ = transformBuilder
				.setPosition(Vec3f.Z.scale(2))
				.setRotation(QuaternionF.RotationY((float) Math.PI/2))
				.build();

		TransformObject meshTransformZ = new TransformObject(wholeSceneTransform, transformMeshZ);
		MeshObject meshObjectZ = new MeshObject(
				meshTransformZ,
				meshGroupZ
		);
	}

	static public Transform CreateObject(Vec3f pos, GameObject parent, com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos).build();

		TransformObject meshTransform = new TransformObject(parent, transformMesh);
		MeshObject meshObject = new MeshObject(
				meshTransform,
				meshGroup
		);

		return transformMesh;
	}

	static public Transform CreateObject(Vec3f pos, QuaternionF rotation, GameObject parent, com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos)
				.setRotation(rotation)
				.build();

		TransformObject meshTransform = new TransformObject(parent, transformMesh);
		MeshObject meshObject = new MeshObject(
				meshTransform,
				meshGroup
		);

		return transformMesh;
	}

	static public MeshObject CreateObjectAndGetSceneObject(Vec3f pos, GameObject parent, com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos).build();

		TransformObject meshTransform = new TransformObject(parent, transformMesh);

		return new MeshObject(
				meshTransform,
				meshGroup
		);
	}

	static public MeshObject CreateObjectAndGetSceneObject(Vec3f pos, QuaternionF rotation, GameObject parent, com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setPosition(pos)
				.setRotation(rotation)
				.build();

		TransformObject meshTransform = new TransformObject(parent, transformMesh);

		return new MeshObject(
				meshTransform,
				meshGroup
		);
	}

	static public MeshObject CreateObjectAndGetSceneObject(Vec3f pos, Vec3f scale, QuaternionF rotation, GameObject parent, com.nick.wood.graphics_library.objects.mesh_objects.MeshObject meshGroup) {

		Transform transformMesh = new TransformBuilder()
				.setScale(scale)
				.setPosition(pos)
				.setRotation(rotation)
				.build();

		TransformObject meshTransform = new TransformObject(parent, transformMesh);

		return new MeshObject(
				meshTransform,
				meshGroup
		);
	}

}
