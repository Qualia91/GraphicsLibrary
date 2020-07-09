package com.nick.wood.graphics_library;

import com.nick.wood.game_engine.event_bus.interfaces.EventData;
import com.nick.wood.graphics_library.objects.render_scene.RenderGraph;

public class RenderEventData implements EventData {

	private final long step;
	private final String layerName;
	private final RenderGraph renderGraph;

	public RenderEventData(long step, String layerName, RenderGraph renderGraph) {
		this.step = step;
		this.layerName = layerName;
		this.renderGraph = renderGraph;
	}

	public String getLayerName() {
		return layerName;
	}

	public RenderGraph getRenderGraph() {
		return renderGraph;
	}

	public long getStep() {
		return step;
	}
}
