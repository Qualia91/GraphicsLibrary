package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.input.Inputs;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class WindowTest {

	private double simHerts = 60;

	@Test
	public void test() {

			Window window = new Window(
					100,
					100,
					"",
					new HashMap<>(),
					new Inputs());

			window.init();

			long lastTime = System.nanoTime();

			double deltaSeconds = 0.0;

			while (!window.shouldClose()) {

				long now = System.nanoTime();

				deltaSeconds += (now - lastTime) / 1000000000.0;

				while (deltaSeconds >= 1 / simHerts) {

					window.setTitle("Iteration time: " + deltaSeconds);

					deltaSeconds = 0.0;

				}

				window.loop();

				lastTime = now;

			}

			window.destroy();

	}

}