package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.input.GraphicsLibraryInput;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.game_objects.CameraType;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.FloatBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.lwjgl.opengl.GL30.*;

public class Picking {

	private final GraphicsLibraryInput graphicsLibraryInput;
	private final FloatBuffer rgb = BufferUtils.createFloatBuffer(3);

	public Picking(GraphicsLibraryInput graphicsLibraryInput) {
		this.graphicsLibraryInput = graphicsLibraryInput;
	}

	public Optional<UUID> iterate(Scene scene, int height) {
		if (graphicsLibraryInput.getButtons()[0]) {

			int x = (int) graphicsLibraryInput.getMouseX();
			int y = (int) graphicsLibraryInput.getMouseY();

			if (scene.getPickingFrameBuffer() != null && scene.getPickingShader() != null) {
				for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : scene.getCameras().entrySet()) {
					if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {
						glBindFramebuffer(GL_READ_FRAMEBUFFER, scene.getPickingFrameBuffer().getFrameBuffer());
						glReadBuffer(GL_COLOR_ATTACHMENT0);
						// y is height - y because open gl fbo's are rendered upside down
						GL11.glReadPixels(x, height - y, 1, 1, GL12.GL_RGB, GL11.GL_FLOAT, rgb);
						if (scene.getIndexToUUIDMap().containsKey(Math.round(rgb.get(0)))) {
							if (scene.getIndexToUUIDMap().get(Math.round(rgb.get(0))).containsKey(Math.round(rgb.get(1)))) {
								return Optional.of(scene.getIndexToUUIDMap().get(Math.round(rgb.get(0))).get(Math.round(rgb.get(1))));
							}
						}
						glReadBuffer(GL_NONE);
						glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);

					}
				}
			}
		}
		return Optional.empty();
	}
}
