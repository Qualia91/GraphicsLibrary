package com.boc_dev.graphics_library.communication;

import com.boc_dev.event_bus.interfaces.Event;
import com.boc_dev.graphics_library.Window;

public interface RenderUpdateEvent<T> extends Event<T> {
	void applyToGraphicsEngine(Window window);
}
