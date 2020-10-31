package com.nick.wood.graphics_library.communication;

import com.nick.wood.game_engine.event_bus.interfaces.Event;
import com.nick.wood.graphics_library.Window;

public interface RenderUpdateEvent<T> extends Event<T> {
	void applyToGraphicsEngine(Window window);
}
