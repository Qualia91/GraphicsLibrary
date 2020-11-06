package com.nick.wood.graphics_library.communication;

import com.nick.wood.graphics_library.Window;
import com.nick.wood.graphics_library.objects.lighting.Light;
import com.nick.wood.graphics_library.objects.render_scene.InstanceObject;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

public class LightCreateEvent implements RenderUpdateEvent<Light> {

	private final InstanceObject instanceObject;
	private final Light light;
	private final String layerName;


	public LightCreateEvent(InstanceObject instanceObject, Light light, String layerName) {
		this.instanceObject = instanceObject;
		this.light = light;
		this.layerName = layerName;
	}

	public InstanceObject getInstanceObject() {
		return instanceObject;
	}

	@Override
	public Light getData() {
		return light;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		if (window.getRenderGraphs().get(layerName) == null) {
			window.getRenderGraphs().put(layerName, new RenderGraph());
		}
		// now add light to list of light
		window.getRenderGraphs().get(layerName).getLights().put(light, this.instanceObject);
	}
}
