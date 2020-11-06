package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.lighting.Light;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;
import com.nick.wood.maths.objects.matrix.Matrix4f;

import java.util.Map;
import java.util.UUID;

public class LightUpdateEvent implements RenderUpdateEvent<UUID> {

	private final UUID uuid;
	private final String layerName;
	private final Matrix4f matrix4f;

	public LightUpdateEvent(UUID uuid, String layerName, Matrix4f matrix4f) {
		this.uuid = uuid;
		this.layerName = layerName;
		this.matrix4f = matrix4f;
	}

	@Override
	public UUID getData() {
		return uuid;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		if (window.getRenderGraphs().get(layerName) == null) {
			window.getRenderGraphs().put(layerName, new RenderGraph());
		}
		// this should find the correct light as the hash code and equals in light are overridden to only look at uuid's
		for (Map.Entry<Light, InstanceObject> lightInstanceObjectEntry : window.getRenderGraphs().get(layerName).getLights().entrySet()) {
			if (lightInstanceObjectEntry.getKey().equals(uuid)) {
				lightInstanceObjectEntry.getValue().setTransformation(matrix4f);
			}
		}
	}
}
