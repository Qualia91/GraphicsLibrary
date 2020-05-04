package com.nick.wood.graphics_library_3d.utils;

import com.nick.wood.graphics_library_3d.lighting.Light;
import com.nick.wood.graphics_library_3d.objects.Camera;
import com.nick.wood.graphics_library_3d.objects.scene_graph_objects.*;
import com.nick.wood.graphics_library_3d.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.matrix.Matrix4f;

import java.util.UUID;
import java.util.WeakHashMap;

public class SceneGraphUtils {

	static private void createRenderLists(WeakHashMap<UUID, RenderObject<Light>> lights, WeakHashMap<UUID, RenderObject<MeshObject>> meshes, WeakHashMap<UUID, RenderObject<Camera>> cameras, SceneGraphNode sceneGraphNode, Matrix4f transformationSoFar) {

		if (isAvailableRenderData(sceneGraphNode.getSceneGraphNodeData())) {

			for (SceneGraphNode child : sceneGraphNode.getSceneGraphNodeData().getChildren()) {

				switch (child.getSceneGraphNodeData().getType()) {

					case TRANSFORM:
						TransformSceneGraph transformGameObject = (TransformSceneGraph) child;
						Matrix4f newTransformationSoFar = transformGameObject.getTransformForRender().multiply(transformationSoFar);
						createRenderLists(lights, meshes, cameras, transformGameObject, newTransformationSoFar);
						break;
					case LIGHT:
						LightSceneGraph lightGameObject = (LightSceneGraph) child;
						RenderObject<Light> lightRenderObject = new RenderObject<>(lightGameObject.getLight(), transformationSoFar, child.getSceneGraphNodeData().getUuid());
						lights.put(child.getSceneGraphNodeData().getUuid(), lightRenderObject);
						createRenderLists(lights, meshes, cameras, lightGameObject, transformationSoFar);
						break;
					case MESH:
						MeshSceneGraph meshGameObject = (MeshSceneGraph) child;
						RenderObject<MeshObject> meshGroupRenderObject = new RenderObject<>(meshGameObject.getMeshObject(), transformationSoFar, child.getSceneGraphNodeData().getUuid());
						meshes.put(child.getSceneGraphNodeData().getUuid(), meshGroupRenderObject);
						createRenderLists(lights, meshes, cameras, meshGameObject, transformationSoFar);
						break;
					case CAMERA:
						CameraSceneGraph cameraGameObject = (CameraSceneGraph) child;
						RenderObject<Camera> cameraRenderObject = new RenderObject<>(cameraGameObject.getCamera(), transformationSoFar.invert(), child.getSceneGraphNodeData().getUuid());
						cameras.put(child.getSceneGraphNodeData().getUuid(), cameraRenderObject);
						createRenderLists(lights, meshes, cameras, cameraGameObject, transformationSoFar);
						break;
					default:
						createRenderLists(lights, meshes, cameras, child, transformationSoFar);
						break;

				}

			}

		}

	}

	static private boolean isAvailableRenderData(SceneGraphNodeData sceneGraphNodeData) {
		return sceneGraphNodeData.containsMeshes() || sceneGraphNodeData.containsCameras() || sceneGraphNodeData.containsLights();
	}
}
