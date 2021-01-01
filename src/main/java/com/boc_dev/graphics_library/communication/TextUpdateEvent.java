package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.Camera;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.objects.render_scene.RenderGraph;
import com.boc_dev.graphics_library.objects.text.TextInstance;
import com.boc_dev.maths.objects.matrix.Matrix4f;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class TextUpdateEvent implements RenderUpdateEvent<String> {

	private final String fontName;
	private final String layerName;
	private final Matrix4f matrix4f;
	private final UUID uuid;


	public TextUpdateEvent(String fontName, UUID uuid, String layerName, Matrix4f matrix4f) {
		this.fontName = fontName;
		this.layerName = layerName;
		this.matrix4f = matrix4f;
		this.uuid = uuid;
	}

	@Override
	public String getData() {
		return fontName;
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

		if (window.getRenderGraphs().get(layerName).getTextMeshes().containsKey(fontName)) {
			ArrayList<TextInstance> textInstances = window.getRenderGraphs().get(layerName).getTextMeshes().get(fontName);
			for (TextInstance textInstance : textInstances) {
				if (textInstance.getUuid().equals(uuid)) {
					textInstance.setTransform(matrix4f);
				}
			}
		}

	}
}
