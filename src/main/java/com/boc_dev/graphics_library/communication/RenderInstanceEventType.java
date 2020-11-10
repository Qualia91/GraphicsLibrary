package com.boc_dev.graphics_library.communication;

import com.boc_dev.event_bus.interfaces.EventType;

public enum RenderInstanceEventType implements EventType {
	TRANSFORM_UPDATE,
	TYPE_UPDATE,
	CREATE,
	DESTROY
}
