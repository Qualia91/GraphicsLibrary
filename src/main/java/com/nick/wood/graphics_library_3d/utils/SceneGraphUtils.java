package com.nick.wood.graphics_library_3d.utils;

import com.nick.wood.graphics_library_3d.lighting.Light;
import com.nick.wood.graphics_library_3d.objects.Camera;
import com.nick.wood.graphics_library_3d.objects.game_objects.*;
import com.nick.wood.graphics_library_3d.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.matrix.Matrix4f;

import java.util.UUID;
import java.util.WeakHashMap;

public class SceneGraphUtils {

	static private void createRenderLists(WeakHashMap<UUID, RenderObject<Light>> lights, WeakHashMap<UUID, RenderObject<MeshObject>> meshes, WeakHashMap<UUID, RenderObject<Camera>> cameras, GameObjectNode gameObjectNode, Matrix4f transformationSoFar) {

		if (isAvailableRenderData(gameObjectNode.getGameObjectNodeData())) {

			for (GameObjectNode child : gameObjectNode.getGameObjectNodeData().getChildren()) {

				switch (child.getGameObjectNodeData().getType()) {

					case TRANSFORM:
						TransformGameObject transformGameObject = (TransformGameObject) child;
						Matrix4f newTransformationSoFar = transformGameObject.getTransformForRender().multiply(transformationSoFar);
						createRenderLists(lights, meshes, cameras, transformGameObject, newTransformationSoFar);
						break;
					case LIGHT:
						LightGameObject lightGameObject = (LightGameObject) child;
						RenderObject<Light> lightRenderObject = new RenderObject<>(lightGameObject.getLight(), transformationSoFar, child.getGameObjectNodeData().getUuid());
						lights.put(child.getGameObjectNodeData().getUuid(), lightRenderObject);
						createRenderLists(lights, meshes, cameras, lightGameObject, transformationSoFar);
						break;
					case MESH:
						MeshGameObject meshGameObject = (MeshGameObject) child;
						RenderObject<MeshObject> meshGroupRenderObject = new RenderObject<>(meshGameObject.getMeshObject(), transformationSoFar, child.getGameObjectNodeData().getUuid());
						meshes.put(child.getGameObjectNodeData().getUuid(), meshGroupRenderObject);
						createRenderLists(lights, meshes, cameras, meshGameObject, transformationSoFar);
						break;
					case CAMERA:
						CameraGameObject cameraGameObject = (CameraGameObject) child;
						RenderObject<Camera> cameraRenderObject = new RenderObject<>(cameraGameObject.getCamera(), transformationSoFar.invert(), child.getGameObjectNodeData().getUuid());
						cameras.put(child.getGameObjectNodeData().getUuid(), cameraRenderObject);
						createRenderLists(lights, meshes, cameras, cameraGameObject, transformationSoFar);
						break;
					default:
						createRenderLists(lights, meshes, cameras, child, transformationSoFar);
						break;

				}

			}

		}

	}

	static private boolean isAvailableRenderData(GameObjectNodeData gameObjectNodeData) {
		return gameObjectNodeData.containsMeshes() || gameObjectNodeData.containsCameras() || gameObjectNodeData.containsLights();
	}
}
