package com.boc_dev.graphics_library.input;

import com.boc_dev.event_bus.event_data.MoveEventData;
import com.boc_dev.event_bus.event_data.PickingResponseEventData;
import com.boc_dev.event_bus.event_data.PressEventData;
import com.boc_dev.event_bus.event_types.ControlEventType;
import com.boc_dev.event_bus.event_types.PickingEventType;
import com.boc_dev.event_bus.events.ControlEvent;
import com.boc_dev.event_bus.events.PickingEvent;
import com.boc_dev.event_bus.interfaces.Bus;
import com.boc_dev.event_bus.interfaces.Event;
import com.boc_dev.event_bus.interfaces.Subscribable;
import com.boc_dev.graphics_library.objects.Camera;
import com.boc_dev.graphics_library.objects.CameraType;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.objects.render_scene.RenderGraph;
import com.boc_dev.graphics_library.objects.render_scene.Scene;
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
	private final HashMap<String, RenderGraph> renderGraphs;
	private int x = 0;
	private int y = 0;

	public Picking(Bus bus, Scene scene, HashMap<String, RenderGraph> renderGraphs) {
		this.bus = bus;
		this.scene = scene;
		this.renderGraphs = renderGraphs;
		supported.add(ControlEvent.class);
	}

	public void actionPickingRequest() {

		RenderGraph renderGraph = renderGraphs.get(scene.getName());

		if (renderGraph != null) {

			if (scene.getPickingFrameBuffer() != null && scene.getPickingShader() != null) {
				for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : renderGraph.getCameras().entrySet()) {
					if (cameraInstanceObjectEntry.getKey().getCameraType().equals(CameraType.PRIMARY)) {
						glBindFramebuffer(GL_READ_FRAMEBUFFER, scene.getPickingFrameBuffer().getFrameBuffer());
						glReadBuffer(GL_COLOR_ATTACHMENT0);
						// y is height - y because open gl fbo's are rendered upside down
						GL11.glReadPixels(x, scene.getHeight() - y, 1, 1, GL12.GL_RGB, GL11.GL_FLOAT, rgb);
						if (scene.getIndexToUUIDMap().containsKey(Math.round(rgb.get(0)))) {
							if (scene.getIndexToUUIDMap().get(Math.round(rgb.get(0))).containsKey(Math.round(rgb.get(1)))) {
								bus.dispatch(
										new PickingEvent(
												new PickingResponseEventData(
														scene.getIndexToUUIDMap().get(Math.round(rgb.get(0))).get(Math.round(rgb.get(1))),
														scene.getName()),
												PickingEventType.RESPONSE));
							} else {
								// if nothing is picked, but picking still happened, send a message with a null uuid so selection can be cleared
								bus.dispatch(
										new PickingEvent(
												new PickingResponseEventData(
														PickingResponseEventData.NO_DATA_SELECTED,
														scene.getName()),
												PickingEventType.RESPONSE));
							}
						} else {
							// if nothing is picked, but picking still happened, send a message with a null uuid so selection can be cleared
							bus.dispatch(
									new PickingEvent(
											new PickingResponseEventData(
													PickingResponseEventData.NO_DATA_SELECTED,
													scene.getName()),
											PickingEventType.RESPONSE));
						}
						glReadBuffer(GL_NONE);
						glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);

					}
				}
			}
		}

	}

	@Override
	public void handle(Event<?> event) {

		if (event.getType().equals(ControlEventType.MOUSE)) {
			MoveEventData moveEventData = (MoveEventData) event.getData();
			this.x = (int) moveEventData.getXAxis();
			this.y = (int) moveEventData.getYAxis();
		} else if (event.getType().equals(ControlEventType.MOUSE_BUTTON)) {
			PressEventData pressEventData = (PressEventData) event.getData();
			if (pressEventData.getKey() == 0 && pressEventData.getAction() == 1) {
				actionPickingRequest();
			}
		}

	}

	@Override
	public boolean supports(Class<? extends Event> aClass) {
		return supported.contains(aClass);
	}
}
