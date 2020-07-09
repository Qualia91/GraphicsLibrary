package com.nick.wood.graphics_library;

import com.nick.wood.game_engine.event_bus.interfaces.EventData;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

public class RenderEventData implements EventData {

	private final String layerName;
	private final RenderGraph renderGraph;

	public RenderEventData(String layerName, RenderGraph renderGraph) {
		this.layerName = layerName;
		this.renderGraph = renderGraph;
	}

	public String getLayerName() {
		return layerName;
	}

	public RenderGraph getRenderGraph() {
		return renderGraph;
	}
}
