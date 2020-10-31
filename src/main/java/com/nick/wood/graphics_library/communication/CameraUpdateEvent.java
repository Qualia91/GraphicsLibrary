package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.Camera;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;
import com.nick.wood.maths.objects.matrix.Matrix4f;

import java.util.Map;

public class CameraUpdateEvent implements RenderUpdateEvent<String> {

	private final String cameraName;
	private final String layerName;
	private final Matrix4f matrix4f;


	public CameraUpdateEvent(String cameraName, String layerName, Matrix4f matrix4f) {
		this.cameraName = cameraName;
		this.layerName = layerName;
		this.matrix4f = matrix4f;
	}

	@Override
	public String getData() {
		return cameraName;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		if (window.getRenderGraphs().get(layerName) == null) {
			System.err.println("Layer with name " + layerName + " not found, creating a new layer.");
			window.getRenderGraphs().put(layerName, new RenderGraph());
		}
		for (Map.Entry<Camera, InstanceObject> cameraInstanceObjectEntry : window.getRenderGraphs().get(layerName).getCameras().entrySet()) {
			if (cameraInstanceObjectEntry.getKey().getName().equals(cameraName)) {
				cameraInstanceObjectEntry.getValue().setTransformation(matrix4f);
				return;
			}
		}
		System.err.println("Camera with name " + cameraName + " not found, camera not updated.");
	}
}
