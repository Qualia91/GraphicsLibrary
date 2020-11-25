package com.boc_dev.graphics_library.objects.render_scene;

import com.boc_dev.graphics_library.frame_buffers.PickingFrameBuffer;
import com.boc_dev.graphics_library.frame_buffers.SceneFrameBuffer;
import com.boc_dev.graphics_library.frame_buffers.WaterFrameBuffer;
import com.boc_dev.graphics_library.objects.Camera;
import com.boc_dev.graphics_library.objects.CameraType;
import com.boc_dev.graphics_library.Renderer;
import com.boc_dev.graphics_library.Shader;
import com.boc_dev.graphics_library.objects.lighting.Fog;
import com.boc_dev.graphics_library.objects.lighting.Light;
import com.boc_dev.graphics_library.objects.managers.MaterialManager;
import com.boc_dev.graphics_library.objects.managers.ModelManager;
import com.boc_dev.graphics_library.objects.managers.TextureManager;
import com.boc_dev.graphics_library.objects.materials.Material;
import com.boc_dev.graphics_library.objects.materials.WaterMaterial;
import com.boc_dev.graphics_library.objects.mesh_objects.Mesh;
import com.boc_dev.graphics_library.objects.mesh_objects.Model;
import com.boc_dev.maths.objects.matrix.Matrix4f;
import com.boc_dev.maths.objects.vector.Vec3f;
import com.boc_dev.maths.objects.vector.Vec4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class Scene {

	private final String name;

	private int screenWidth;
	private int screenHeight;
	private boolean updateProjectionMatrices = false;

	private Fog fog;
	private Vec3f ambientLight;

	private Shader mainShader;
	private Shader skyboxShader;
	private Shader waterShader;
	private Shader pickingShader;
	private Shader terrainShader;

	private final HashMap<Integer, HashMap<Integer, UUID>> indexToUUIDMap = new HashMap<>();

	private WaterFrameBuffer waterFrameBuffer;
	private PickingFrameBuffer pickingFrameBuffer;
	private final HashMap<String, SceneFrameBuffer> cameraNameToSceneFrameBuffersMap = new HashMap<>();

	private float waveSpeed = 0.0005f;
	private float moveFactor = 0;

	private final Vec4f reflectionClippingPlane = new Vec4f(0, 0, 1, -1);
	private final Vec4f refractionClippingPlane = new Vec4f(0, 0, -1, 1);

	private final Matrix4f waterCameraReflection;

	private final Matrix4f backFaceCullFlip = new Matrix4f(
			-1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
	);

	public Scene(String name,
	             Shader mainShader,
	             Shader waterShader,
	             Shader skyboxShader,
	             Shader pickingShader,
	             Shader terrainShader,
	             Fog fog,
	             Vec3f ambientLight) {

		this.name = name;

		this.mainShader = mainShader;
		this.waterShader = waterShader;
		this.skyboxShader = skyboxShader;
		this.pickingShader = pickingShader;
		this.terrainShader = terrainShader;

		this.fog = fog;
		this.ambientLight = ambientLight;

		this.waterCameraReflection = createReflectionMatrix(reflectionClippingPlane);
	}

	public void init(int width, int height) {

		if (mainShader != null) {
			mainShader.create();
		}
		if (skyboxShader != null) {
			skyboxShader.create();
		}
		if (waterShader != null) {
			waterShader.create();
			waterFrameBuffer = new WaterFrameBuffer(width, height);
		}
		if (pickingShader != null) {
			pickingShader.create();
			pickingFrameBuffer = new PickingFrameBuffer(width, height);
		}
		if (terrainShader != null) {
			terrainShader.create();
		}

	}

	private Matrix4f createReflectionMatrix(Vec4f plane) {
		return new Matrix4f(
				1 - (2 * plane.getX() * plane.getX()), -2 * plane.getX() * plane.getY(), -2 * plane.getX() * plane.getZ(), 2 * plane.getX() * plane.getS(),
				-2 * plane.getX() * plane.getY(), 1 - (2 * plane.getY() * plane.getY()), -2 * plane.getY() * plane.getZ(), 2 * plane.getY() * plane.getS(),
				-2 * plane.getX() * plane.getZ(), -2 * plane.getY() * plane.getZ(), 1 - (2 * plane.getZ() * plane.getZ()), 2 * plane.getZ() * plane.getS(),
				0, 0, 0, 1);
	}

	public void render(Renderer renderer, RenderGraph renderGraph, TextureManager textureManager, long step) {

		// update camera projection matrices if need be
		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : renderGraph.getCameras().entrySet()) {
			if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {
				// see if projection matrix needs updating
				if (updateProjectionMatrices || cameraInstanceObjectEntry.getKey().getProjectionMatrix() == null) {
					cameraInstanceObjectEntry.getKey().updateProjectionMatrix();
					updateProjectionMatrices = false;
					break;
				}
			}
		}

		// Todo need sim time here
		moveFactor += waveSpeed;
		moveFactor %= 1;

		GL11.glViewport(0, 0, screenWidth, screenHeight);

//		// update textures that are being rendered via fbos
//		for (Map.Entry<String, ArrayList<InstanceObject>> meshObjectArrayListEntry : renderGraph.getMeshes().entrySet()) {
//			if (!meshObjectArrayListEntry.getKey().getFboTextureCameraName().isEmpty()) {
//				meshObjectArrayListEntry.getKey().getMesh().getMaterial().setTexturePath(meshObjectArrayListEntry.getKey().getFboTextureCameraName());
//			}
//		}
//


		// Set the clear color
		glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		GL11.glViewport(0, 0, screenWidth, screenHeight);

		if (pickingShader != null && pickingFrameBuffer != null) {
			for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : renderGraph.getCameras().entrySet()) {
				if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {

					pickingFrameBuffer.bindFrameBuffer(cameraInstanceObjectEntry.getKey().getWidth(), cameraInstanceObjectEntry.getKey().getHeight());
					// Set the clear color
					glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
					renderer.renderPickingScene(renderGraph.getPickingMeshes(), cameraInstanceObjectEntry, pickingShader, indexToUUIDMap);
					pickingFrameBuffer.unbindCurrentFrameBuffer();
				}
			}
		}

		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : renderGraph.getCameras().entrySet()) {

			if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.FBO)) {
//				if (mainShader != null) {
//
//					if (!cameraNameToSceneFrameBuffersMap.containsKey(cameraInstanceObjectEntry.getKey().getName())) {
//						cameraNameToSceneFrameBuffersMap.put(
//								cameraInstanceObjectEntry.getKey().getName(),
//								new SceneFrameBuffer(cameraInstanceObjectEntry.getKey().getWidth(), cameraInstanceObjectEntry.getKey().getHeight()));
//					}
//
//					cameraNameToSceneFrameBuffersMap.get(cameraInstanceObjectEntry.getKey().getName()).bindFrameBuffer();
//					renderSceneToBuffer(renderer, cameraInstanceObjectEntry, null, renderGraph.getSkybox(), renderGraph.getMeshes(), renderGraph.getTerrainMeshes(), renderGraph.getLights());
//					cameraNameToSceneFrameBuffersMap.get(cameraInstanceObjectEntry.getKey().getName()).unbindCurrentFrameBuffer();
//
//					textureManager.addTexture(cameraInstanceObjectEntry.getKey().getName(), cameraNameToSceneFrameBuffersMap.get(cameraInstanceObjectEntry.getKey().getName()).getTexture());
//
//
//				}
//				break;
			}

			else if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {

				if (waterFrameBuffer != null && waterShader != null && !renderGraph.getWaterMeshes().isEmpty()) {

					Matrix4f newCameraMatrix = backFaceCullFlip.multiply(cameraInstanceObjectEntry.getValue().getTransformation()).multiply(waterCameraReflection);
					Map.Entry<Camera, InstanceObject> reflectedCamera =
							new AbstractMap.SimpleEntry<>(cameraInstanceObjectEntry.getKey(),
									new InstanceObject(cameraInstanceObjectEntry.getValue().getUuid(), newCameraMatrix));
					waterFrameBuffer.bindReflectionFrameBuffer();
					renderSceneToBuffer(renderer, reflectedCamera, reflectionClippingPlane, renderGraph.getSkybox(), renderGraph.getMeshes(), renderGraph.getTerrainMeshes(), renderGraph.getLights());
					waterFrameBuffer.bindRefractionFrameBuffer();
					renderSceneToBuffer(renderer, cameraInstanceObjectEntry, refractionClippingPlane, renderGraph.getSkybox(), renderGraph.getMeshes(), renderGraph.getTerrainMeshes(), renderGraph.getLights());
					waterFrameBuffer.unbindCurrentFrameBuffer(screenWidth, screenHeight);

					textureManager.addTexture("REFLECTION_TEXTURE", waterFrameBuffer.getReflectionTexture());
					textureManager.addTexture("REFRACTION_TEXTURE", waterFrameBuffer.getRefractionTexture());

				}
				if (skyboxShader != null && renderGraph.getSkybox() != null) {
					renderer.renderSkybox(renderGraph.getSkybox(), cameraInstanceObjectEntry, skyboxShader);
				}
				if (waterShader != null && waterFrameBuffer != null) {
					renderer.renderWater(
							renderGraph.getWaterMeshes(),
							cameraInstanceObjectEntry,
							renderGraph.getLights(),
							waterShader,
							fog,
							moveFactor,
							ambientLight);
				}

//				if (terrainShader != null) {
//					if (!renderGraph.getTerrainMeshes().isEmpty()) {
//						for (Mesh meshObject : renderGraph.getTerrainMeshes().keySet()) {
//							Terrain terrain = (Terrain) meshObject;
//							renderer.renderTerrain(renderGraph.getTerrainMeshes(), cameraInstanceObjectEntry, renderGraph.getLights(), terrainShader, ambientLight, fog, null);
//							break;
//						}
//					}
//				}
				if (mainShader != null) {
					GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
					renderer.renderScene(renderGraph.getMeshes(), cameraInstanceObjectEntry, renderGraph.getLights(), mainShader, ambientLight, fog, null);
				}
				break;
			}
		}

	}

	private void renderSceneToBuffer(Renderer renderer,
	                                 Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry,
	                                 Vec4f clippingPlane,
	                                 Pair<String,InstanceObject> skybox,
	                                 HashMap<String, ArrayList<InstanceObject>> models,
	                                 HashMap<String, InstanceObject> terrainMeshes,
	                                 HashMap<Light, InstanceObject> lights) {
		// enable clip planes
		// this clips everything under the water
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		if (skyboxShader != null && skybox != null) {
			// have to put back on skybox so the reflected camera that reverses triangle loop will render it
			GL11.glDisable(GL20.GL_CULL_FACE);
			renderer.renderSkybox(skybox, cameraInstanceObjectEntry, skyboxShader);
			GL11.glEnable(GL20.GL_CULL_FACE);
			GL11.glCullFace(GL20.GL_BACK);
		}
//		if (terrainShader != null) {
//			renderer.renderTerrain(terrainMeshes, cameraInstanceObjectEntry, lights, terrainShader, ambientLight, fog, clippingPlane);
//		}
		renderer.renderScene(models, cameraInstanceObjectEntry, lights, mainShader, ambientLight, fog, clippingPlane);
	}

	public void updateScreen(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		updateProjectionMatrices = true;
	}

	public PickingFrameBuffer getPickingFrameBuffer() {
		return pickingFrameBuffer;
	}

	public Shader getPickingShader() {
		return pickingShader;
	}

	public HashMap<Integer, HashMap<Integer, UUID>> getIndexToUUIDMap() {
		return indexToUUIDMap;
	}

	public int getHeight() {
		return screenHeight;
	}

	public String getName() {
		return name;
	}

	public void destroy() {
		if (mainShader != null) {
			mainShader.destroy();
		}
		if (skyboxShader != null) {
			skyboxShader.destroy();
		}
		if (waterShader != null) {
			waterShader.destroy();
		}
		if (pickingShader != null) {
			pickingShader.destroy();
		}
	}
}
