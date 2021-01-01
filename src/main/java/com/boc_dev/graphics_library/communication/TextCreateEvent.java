package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;
import com.boc_dev.graphics_library.objects.mesh_objects.Model;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.objects.render_scene.RenderGraph;
import com.boc_dev.graphics_library.objects.text.CharacterData;
import com.boc_dev.graphics_library.objects.text.FontAlignment;
import com.boc_dev.graphics_library.objects.text.TextInstance;
import com.boc_dev.maths.objects.matrix.Matrix4f;

import java.util.ArrayList;

public class TextCreateEvent implements RenderUpdateEvent<TextInstance> {

	private final TextInstance textInstance;
	private final String layerName;
	private final float fontSize;
	private final FontAlignment fontAlignment;
	private String fontName;

	public TextCreateEvent(TextInstance textInstance, String layerName, String fontName, float fontSize, String fontAlignment)  {
		this.textInstance = textInstance;
		this.fontName = fontName;
		this.layerName = layerName;
		this.fontSize = fontSize;
		this.fontAlignment = FontAlignment.valueOf(fontAlignment);
	}

	@Override
	public TextInstance getData() {
		return textInstance;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.CREATE;
	}

	public void applyToGraphicsEngine(Window window) {
		if (window.getRenderGraphs().get(layerName) == null) {
			window.getRenderGraphs().put(layerName, new RenderGraph());
		}

		// check if font exists, add text instance it to array
		if (window.getRenderGraphs().get(layerName).getTextMeshes().containsKey(fontName)) {
			window.getRenderGraphs().get(layerName).getTextMeshes().get(fontName).add(textInstance);
		}
		// if it does not exist, we need to font
		else {
			window.getFontManager().addFont(fontName);
			ArrayList<TextInstance> instanceObjects = new ArrayList<>();
			instanceObjects.add(textInstance);
			window.getRenderGraphs().get(layerName).getTextMeshes().put(fontName, instanceObjects);
		}

		window.getMeshManager().createText(
				textInstance.getUuid().toString(),
				textInstance.getText(),
				window.getFontManager().getFont(fontName).getCharacterData(),
				fontSize,
				fontAlignment);

	}
}
