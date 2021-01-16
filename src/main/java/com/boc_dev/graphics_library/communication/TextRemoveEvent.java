package com.boc_dev.graphics_library.communication;

import com.boc_dev.graphics_library.Window;

import java.util.UUID;

public class TextRemoveEvent implements RenderUpdateEvent<String> {

	private final String fontName;
	private final String text;
	private final String layerName;
	private final UUID uuid;

	public TextRemoveEvent(UUID uuid, String fontName, String text, String layerName) {
		this.uuid = uuid;
		this.fontName = fontName;
		this.text = text;
		this.layerName = layerName;
	}

	@Override
	public String getData() {
		return text;
	}

	@Override
	public RenderInstanceEventType getType() {
		return RenderInstanceEventType.DESTROY;
	}

	public void applyToGraphicsEngine(Window window) {
		// if the layer does not exist, just ignore this as its clearly not being rendered (or something has gone wrong)
		if (window.getRenderGraphs().get(layerName) == null) {
			System.err.println("Text " + text + " with font " + fontName + "cannot be removed as layer " + layerName + " does not exist");
			return;
		}

		// check if font exists
		if (window.getRenderGraphs().get(layerName).getTextMeshes().containsKey(fontName)) {
			// find text instance via uuid as hash code and equals is overridden in text instance
			if (!window.getRenderGraphs().get(layerName).getTextMeshes().get(fontName).removeIf(tIns -> tIns.getUuid().equals(uuid))) {
				System.out.println("Cant find model to delete");
			}
			// now destroy text mesh
			window.getMeshManager().removeTextMesh(uuid.toString());
		} else {
			System.out.println("Font doesn't exits, cant delete: " + layerName + " " + text + " " + fontName);
		}


		// keep font loaded, its pretty small and i doubt people will load up millions of fonts (famous last words?)

	}
}
