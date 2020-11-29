package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Renderer;
import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.lighting.Light;
import com.boc_dev.graphics_library.objects.mesh_objects.MeshType;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

public class LightRemoveEvent implements RenderUpdateEvent<Light> {

	private final HashSet<UUID> uuids;
	private final Light light;
	private final String layerName;


	public LightRemoveEvent(HashSet<UUID> uuids, Light light, String layerName) {
		this.uuids = uuids;
		this.light = light;
		this.layerName = layerName;
	}

	@Override
	public Light getData() {
		return light;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.DESTROY;
	}

	public void applyToGraphicsEngine(Window window) {
		// if the layer does not exist, just ignore this as its clearly not being rendered (or something has gone wrong)
		if (window.getRenderGraphs().get(layerName) == null) {
			System.err.println("Light " + light + " cannot ben removed as layer " + layerName + " does not exist");
			return;
		}

		window.getRenderGraphs().get(layerName).getLights().remove(light);

	}
}
