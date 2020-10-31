package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.mesh_objects.Model;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

import java.util.ArrayList;

public class CameraCreateEvent implements RenderUpdateEvent<Camera> {

	private final InstanceObject instanceObject;
	private final Camera camera;
	private final String layerName;


	public CameraCreateEvent(InstanceObject instanceObject, Camera camera, String layerName) {
		this.instanceObject = instanceObject;
		this.camera = camera;
		this.layerName = layerName;
	}

	public InstanceObject getInstanceObject() {
		return instanceObject;
	}

	@Override
	public Camera getData() {
		return camera;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		if (window.getRenderGraphs().get(layerName) == null) {
			window.getRenderGraphs().put(layerName, new RenderGraph());
		}
		window.getRenderGraphs().get(layerName).getCameras().put(camera, instanceObject);
	}
}
