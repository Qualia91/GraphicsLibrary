package com.nick.wood.graphics_library.objects.render_scene;

import com.nick.wood.graphics_library.Renderer;
import com.nick.wood.graphics_library.Shader;
import com.nick.wood.graphics_library.frame_buffers.PickingFrameBuffer;
import com.nick.wood.graphics_library.frame_buffers.SceneFrameBuffer;
import com.nick.wood.graphics_library.frame_buffers.WaterFrameBuffer;
import com.nick.wood.graphics_library.lighting.Fog;
import com.nick.wood.graphics_library.lighting.Light;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.game_objects.CameraType;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;
import com.nick.wood.maths.objects.vector.Vec4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengles.GLES20;

import java.util.*;

public class Scene {

	private int screenWidth;
	private int screenHeight;
	private boolean updateProjectionMatrices = false;

	private Fog fog;
	private Vec3f ambientLight = new Vec3f(0.0529f, 0.0808f, 0.0922f);
	private Vec3f skyboxAmbientLight = new Vec3f(0.9f, 0.9f, 0.9f);
	private Shader shader;
	private Shader skyboxShader;
	private Shader waterShader;
	private Shader pickingShader;
	private final HashMap<Light, InstanceObject> lights;
	private final HashMap<MeshObject, ArrayList<InstanceObject>> meshes;
	private final HashMap<Integer, HashMap<Integer, UUID>> indexToUUIDMap = new HashMap<>();
	private final HashMap<MeshObject, ArrayList<InstanceObject>> waterMeshes;
	private final HashMap<Camera, InstanceObject> cameras;
	private MeshObject skybox;
	private WaterFrameBuffer waterFrameBuffer;
	private SceneFrameBuffer sceneFrameBuffer;
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
	private Matrix4f cameraTransform = Matrix4f.Identity;

	public Scene() {
		this.lights = new HashMap<>();
		this.meshes = new HashMap<>();
		this.waterMeshes = new HashMap<>();
		this.cameras = new HashMap<>();
		this.fog = new Fog(true, ambientLight, 0.0003f);

		this.waterCameraReflection = createReflectionMatrix(reflectionClippingPlane);
	}

	private Matrix4f createReflectionMatrix(Vec4f plane) {
		return new Matrix4f(
				1 - (2 * plane.getX() * plane.getX()), -2 * plane.getX() * plane.getY(), -2 * plane.getX() * plane.getZ(), 2 * plane.getX() * plane.getS(),
				-2 * plane.getX() * plane.getY(), 1 - (2 * plane.getY() * plane.getY()), -2 * plane.getY() * plane.getZ(), 2 * plane.getY() * plane.getS(),
				-2 * plane.getX() * plane.getZ(), -2 * plane.getY() * plane.getZ(), 1 - (2 * plane.getZ() * plane.getZ()), 2 * plane.getZ() * plane.getS(),
				0, 0, 0, 1);
	}

	public Fog getFog() {
		return fog;
	}

	public void setFog(Fog fog) {
		this.fog = fog;
	}

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public Shader getSkyboxShader() {
		return skyboxShader;
	}

	public void setSkyboxShader(Shader skyboxShader) {
		this.skyboxShader = skyboxShader;
	}

	public MeshObject getSkybox() {
		return skybox;
	}

	public HashMap<MeshObject, ArrayList<InstanceObject>> getMeshes() {
		return meshes;
	}

	public HashMap<MeshObject, ArrayList<InstanceObject>> getWaterMeshes() {
		return waterMeshes;
	}

	public HashMap<Light, InstanceObject> getLights() {
		return lights;
	}

	public HashMap<Camera, InstanceObject> getCameras() {
		return cameras;
	}

	public void render(Renderer renderer) {

		// update camera projection matrices if need be
		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : cameras.entrySet()) {
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
		if (sceneFrameBuffer != null) {
			for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : cameras.entrySet()) {
				if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.FBO_CAMERA)) {
					if (shader != null) {
						sceneFrameBuffer.bindFrameBuffer();
						renderSceneToBuffer(renderer, cameraInstanceObjectEntry, null);
						sceneFrameBuffer.unbindCurrentFrameBuffer();

						GL11.glViewport(0, 0, screenWidth, screenHeight);

						// now render the fbo textured objects that have the same index as this camera
						for (Map.Entry<MeshObject, ArrayList<InstanceObject>> meshObjectArrayListEntry : meshes.entrySet()) {
							if (meshObjectArrayListEntry.getKey().getFboTextureIndex() == cameraInstanceObjectEntry.getKey().getFboTextureIndex()) {
								meshObjectArrayListEntry.getKey().getMesh().getMaterial().getTexture().setId(sceneFrameBuffer.getTexture());
							}
						}
						;
					}
					break;
				}
			}

		}
		if (pickingShader != null && pickingFrameBuffer != null) {
			for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : cameras.entrySet()) {
				if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {
					pickingFrameBuffer.bindFrameBuffer(cameraInstanceObjectEntry.getKey().getWidth(), cameraInstanceObjectEntry.getKey().getHeight());
					renderSceneToPickingBuffer(renderer, cameraInstanceObjectEntry);
					pickingFrameBuffer.unbindCurrentFrameBuffer();
				}
			}
		}

		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : cameras.entrySet()) {
			if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {

				this.cameraTransform = cameraInstanceObjectEntry.getValue().getTransformation();

				if (waterFrameBuffer != null && waterShader != null && !waterMeshes.isEmpty()) {

					// move camera down by 2 * height to get reflection
					Matrix4f newCameraMatrix = backFaceCullFlip.multiply(cameraInstanceObjectEntry.getValue().getTransformation()).multiply(waterCameraReflection);
					Map.Entry<Camera, InstanceObject> reflectedCamera =
							new AbstractMap.SimpleEntry<>(cameraInstanceObjectEntry.getKey(),
									new InstanceObject(cameraInstanceObjectEntry.getValue().getUuid(), newCameraMatrix));
					waterFrameBuffer.bindReflectionFrameBuffer();
					renderSceneToBuffer(renderer, reflectedCamera, reflectionClippingPlane);
					waterFrameBuffer.bindRefractionFrameBuffer();
					renderSceneToBuffer(renderer, cameraInstanceObjectEntry, refractionClippingPlane);
					waterFrameBuffer.unbindCurrentFrameBuffer(screenWidth, screenHeight);

				}
				if (skyboxShader != null && skybox != null) {
					GL11.glDisable(GLES20.GL_CULL_FACE);
					renderer.renderSkybox(skybox, cameraInstanceObjectEntry, skyboxShader, skyboxAmbientLight);
					GL11.glEnable(GLES20.GL_CULL_FACE);
					GL11.glCullFace(GLES20.GL_BACK);
				}
				if (waterShader != null && waterFrameBuffer != null) {
					renderer.renderWater(waterMeshes,
							cameraInstanceObjectEntry,
							lights,
							waterShader,
							fog,
							waterFrameBuffer.getReflectionTexture(),
							waterFrameBuffer.getRefractionTexture(),
							moveFactor,
							skyboxAmbientLight);
				}
				if (shader != null) {
					GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
					renderer.renderScene(meshes, cameraInstanceObjectEntry, lights, shader, ambientLight, fog, null);
				}
				break;
			}
		}

		for (ArrayList<InstanceObject> value : meshes.values()) {
			value.clear();
		}
		for (ArrayList<InstanceObject> value : waterMeshes.values()) {
			value.clear();
		}

	}

	public void renderSceneToPickingBuffer(Renderer renderer, Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry) {
		renderer.renderPickingScene(meshes, cameraInstanceObjectEntry, pickingShader, indexToUUIDMap);
	}

	private void renderSceneToBuffer(Renderer renderer, Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry, Vec4f clippingPlane) {
		// enable clip planes
		// this clips everything under the water
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		if (skyboxShader != null && skybox != null) {
			GL11.glDisable(GLES20.GL_CULL_FACE);
			renderer.renderSkybox(skybox, cameraInstanceObjectEntry, skyboxShader, skyboxAmbientLight);
			GL11.glEnable(GLES20.GL_CULL_FACE);
			GL11.glCullFace(GLES20.GL_BACK);
		}
		renderer.renderScene(meshes, cameraInstanceObjectEntry, lights, shader, ambientLight, fog, clippingPlane);
	}

	public void removeLight(UUID uuid) {
		lights.entrySet().removeIf(next -> next.getValue().getUuid().equals(uuid));
	}

	public void removeCamera(UUID uuid) {
		cameras.entrySet().removeIf(next -> next.getValue().getUuid().equals(uuid));
	}

	public void removeMesh(UUID uuid) {

		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> next : meshes.entrySet()) {
			next.getValue().removeIf(instance -> instance.getUuid().equals(uuid));

		}
	}

	public void removeWater(UUID uuid) {

		for (Map.Entry<MeshObject, ArrayList<InstanceObject>> next : waterMeshes.entrySet()) {
			next.getValue().removeIf(instance -> instance.getUuid().equals(uuid));

		}
	}

	public void attachShader(Shader shader) {
		this.shader = shader;
	}

	public void attachSkyboxShader(Shader skyboxShader) {
		this.skyboxShader = skyboxShader;
	}

	public void setSkybox(MeshObject skybox) {
		this.skybox = skybox;
	}

	public void removeSkybox() {
		this.skybox = null;
	}

	public Vec3f getAmbientLight() {
		return ambientLight;
	}

	public void setAmbientLight(Vec3f ambientLight) {
		this.ambientLight = ambientLight;
	}

	public Vec3f getSkyboxAmbientLight() {
		return skyboxAmbientLight;
	}

	public void setSkyboxAmbientLight(Vec3f skyboxAmbientLight) {
		this.skyboxAmbientLight = skyboxAmbientLight;
	}

	public void attachWaterShader(Shader waterShader) {
		this.waterShader = waterShader;
	}

	public void setWaterFrameBufferObject(WaterFrameBuffer waterFrameBuffer) {
		this.waterFrameBuffer = waterFrameBuffer;
	}

	public void setSceneFrameBufferObject(SceneFrameBuffer sceneFrameBuffer) {
		this.sceneFrameBuffer = sceneFrameBuffer;
	}

	public void updateScreen(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		updateProjectionMatrices = true;
	}

	public Matrix4f getCameraTransform() {
		return cameraTransform;
	}

	public void attachPickingShader(Shader pickingShader) {
		this.pickingShader = pickingShader;
	}

	public void setPickingFrameBufferObject(PickingFrameBuffer pickingFrameBuffer) {
		this.pickingFrameBuffer = pickingFrameBuffer;
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
}
