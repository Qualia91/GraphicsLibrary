package com.nick.wood.graphics_library.objects.render_scene;

import com.nick.wood.graphics_library.Renderer;
import com.nick.wood.graphics_library.Shader;
import com.nick.wood.graphics_library.frame_buffers.PickingFrameBuffer;
import com.nick.wood.graphics_library.frame_buffers.WaterFrameBuffer;
import com.nick.wood.graphics_library.lighting.Fog;
import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.materials.TextureManager;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.CameraType;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.TextItem;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import com.nick.wood.maths.objects.vector.Vec4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.*;

public class Scene {

	private final String name;

	private int screenWidth;
	private int screenHeight;
	private boolean updateProjectionMatrices = false;

	private Fog fog;
	private Vec3f ambientLight;
	private Vec3f skyboxAmbientLight;

	private Shader mainShader;
	private Shader skyboxShader;
	private Shader waterShader;
	private Shader pickingShader;
	private Shader terrainShader;

	private final HashMap<Integer, HashMap<Integer, UUID>> indexToUUIDMap = new HashMap<>();

	private WaterFrameBuffer waterFrameBuffer;
	private PickingFrameBuffer pickingFrameBuffer;

	private float waveSpeed = 0.0005f;
	private float moveFactor = 0;

	private final Vec4f reflectionClippingPlane = new Vec4f(0, 0, 1, -1f);
	private final Vec4f refractionClippingPlane = new Vec4f(0, 0, -1, 1f);

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
	             Vec3f ambientLight,
	             Vec3f skyboxAmbientLight) {

		this.name = name;

		this.mainShader = mainShader;
		this.waterShader = waterShader;
		this.skyboxShader = skyboxShader;
		this.pickingShader = pickingShader;
		this.terrainShader = terrainShader;

		this.fog = fog;
		this.ambientLight = ambientLight;
		this.skyboxAmbientLight = skyboxAmbientLight;

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

	public void render(Renderer renderer, RenderGraph renderGraph, TextureManager textureManager) {

		// update camera projection matrices if need be
		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : renderGraph.getCameras().entrySet()) {
			if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {
				// see if projection matrix needs updating
				if (updateProjectionMatrices) {
					cameraInstanceObjectEntry.getKey().updateProjectionMatrix(screenWidth, screenHeight);
					updateProjectionMatrices = false;
					break;
				}
			}
		}

		// Todo need sim time here
		moveFactor += waveSpeed; // * simTimeSinceLast;
		moveFactor %= 1;

		// render scene fbos
		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : renderGraph.getCameras().entrySet()) {
			if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.FBO_CAMERA)) {
				if (mainShader != null) {

					if (cameraInstanceObjectEntry.getKey().getSceneFrameBuffer() == null) {
						cameraInstanceObjectEntry.getKey().create();
					}

					cameraInstanceObjectEntry.getKey().getSceneFrameBuffer().bindFrameBuffer();
					renderSceneToBuffer(renderer, cameraInstanceObjectEntry, null, renderGraph.getSkybox(), renderGraph.getMeshes(), renderGraph.getTerrainMeshes(), renderGraph.getLights());
					cameraInstanceObjectEntry.getKey().getSceneFrameBuffer().unbindCurrentFrameBuffer();

					textureManager.addTexture(cameraInstanceObjectEntry.getKey().getName(), cameraInstanceObjectEntry.getKey().getSceneFrameBuffer().getTexture());

					GL11.glViewport(0, 0, screenWidth, screenHeight);

				}
				break;
			}

		}

		// update textures that are being rendered via fbos
		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry : renderGraph.getMeshes().entrySet()) {
			if (!meshObjectArrayListEntry.getKey().getFboTextureCameraName().isEmpty()) {
				meshObjectArrayListEntry.getKey().getMesh().getMaterial().setTexturePath(meshObjectArrayListEntry.getKey().getFboTextureCameraName());
			}
		}

		if (pickingShader != null && pickingFrameBuffer != null) {
			for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : renderGraph.getCameras().entrySet()) {
				if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {
					pickingFrameBuffer.bindFrameBuffer(cameraInstanceObjectEntry.getKey().getWidth(), cameraInstanceObjectEntry.getKey().getHeight());
					renderSceneToPickingBuffer(renderer, cameraInstanceObjectEntry, renderGraph.getMeshes());
					pickingFrameBuffer.unbindCurrentFrameBuffer();
				}
			}
		}

		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : renderGraph.getCameras().entrySet()) {
			if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {

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

				}
				if (skyboxShader != null && renderGraph.getSkybox() != null) {
					renderer.renderSkybox(renderGraph.getSkybox(), cameraInstanceObjectEntry, skyboxShader, skyboxAmbientLight);
				}
				if (waterShader != null && waterFrameBuffer != null) {
					renderer.renderWater(renderGraph.getWaterMeshes(),
							cameraInstanceObjectEntry,
							renderGraph.getLights(),
							waterShader,
							fog,
							waterFrameBuffer.getReflectionTexture(),
							waterFrameBuffer.getRefractionTexture(),
							moveFactor,
							skyboxAmbientLight);
				}

				if (terrainShader != null) {
					renderer.renderTerrain(renderGraph.getTerrainMeshes(), cameraInstanceObjectEntry, renderGraph.getLights(), terrainShader, ambientLight, fog, null);
				}
				if (mainShader != null) {
					GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
					renderer.renderScene(renderGraph.getMeshes(), cameraInstanceObjectEntry, renderGraph.getLights(), mainShader, ambientLight, fog, null);
				}
				break;
			}
		}

	}

	public void renderSceneToPickingBuffer(Renderer renderer, Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry, HashMap<MeshObject, ArrayList<InstanceObject>> meshes) {
		renderer.renderPickingScene(meshes, cameraInstanceObjectEntry, pickingShader, indexToUUIDMap);
	}

	private void renderSceneToBuffer(Renderer renderer,
	                                 Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry,
	                                 Vec4f clippingPlane,
	                                 MeshObject skybox,
	                                 HashMap<MeshObject, ArrayList<InstanceObject>> meshes,
	                                 HashMap<MeshObject, ArrayList<InstanceObject>> terrainMeshes,
	                                 HashMap<Light, InstanceObject> lights) {
		// enable clip planes
		// this clips everything under the water
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		if (skyboxShader != null && skybox != null) {
			// have to put back on skybox so the reflected camera that reverses triangle loop will render it
			GL11.glDisable(GL20.GL_CULL_FACE);
			renderer.renderSkybox(skybox, cameraInstanceObjectEntry, skyboxShader, skyboxAmbientLight);
			GL11.glEnable(GL20.GL_CULL_FACE);
			GL11.glCullFace(GL20.GL_BACK);
		}
		if (terrainShader != null) {
			renderer.renderTerrain(terrainMeshes, cameraInstanceObjectEntry, lights, terrainShader, ambientLight, fog, clippingPlane);
		}
		renderer.renderScene(meshes, cameraInstanceObjectEntry, lights, mainShader, ambientLight, fog, clippingPlane);
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
