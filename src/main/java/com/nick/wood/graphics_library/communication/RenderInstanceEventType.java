package com.nick.wood.graphics_library.communication;

import com.nick.wood.game_engine.event_bus.interfaces.EventType;

public enum RenderInstanceEventType implements EventType {
	TRANSFORM_UPDATE,
	TYPE_UPDATE,
	CREATE,
	DESTROY
}
