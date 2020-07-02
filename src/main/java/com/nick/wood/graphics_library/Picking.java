package com.nick.wood.graphics_library;

import com.nick.wood.event_bus.events.PickingRequestEvent;
import com.nick.wood.event_bus.interfaces.Bus;
import com.nick.wood.event_bus.interfaces.Event;
import com.nick.wood.event_bus.interfaces.Subscribable;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.CameraType;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;
import com.nick.wood.graphics_library.objects.render_scene.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;

public class Picking implements Subscribable {

	private final Set<Class<?>> supported = new HashSet<>();
	private final Bus bus;
	private final FloatBuffer rgb = BufferUtils.createFloatBuffer(3);
	private final Scene scene;
	private final RenderGraph renderGraph;

	public Picking(Bus bus, Scene scene, RenderGraph renderGraph) {
		this.bus = bus;
		this.scene = scene;
		this.renderGraph = renderGraph;
		supported.add(PickingRequestEvent.class);
	}

	public void actionPickingRequest(int x, int y) {

			if (scene.getPickingFrameBuffer() != null && scene.getPickingShader() != null) {
				for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : renderGraph.getCameras().entrySet()) {
					if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {
						glBindFramebuffer(GL_READ_FRAMEBUFFER, scene.getPickingFrameBuffer().getFrameBuffer());
						glReadBuffer(GL_COLOR_ATTACHMENT0);
						// y is height - y because open gl fbo's are rendered upside down
						GL11.glReadPixels(x, scene.getHeight() - y, 1, 1, GL12.GL_RGB, GL11.GL_FLOAT, rgb);
						if (scene.getIndexToUUIDMap().containsKey(Math.round(rgb.get(0)))) {
							if (scene.getIndexToUUIDMap().get(Math.round(rgb.get(0))).containsKey(Math.round(rgb.get(1)))) {
								// send this: (scene.getIndexToUUIDMap().get(Math.round(rgb.get(0))).get(Math.round(rgb.get(1))));
							}
						}
						glReadBuffer(GL_NONE);
						glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);

					}
				}
			}
	}

	@Override
	public void handle(Event<?> event) {

	}

	@Override
	public boolean supports(Class<? extends Event> aClass) {
		return supported.contains(aClass);
	}
}
