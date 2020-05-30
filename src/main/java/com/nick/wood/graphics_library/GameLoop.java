package com.nick.wood.graphics_library;

public class GameLoop {

	public GameLoop() {

		double milliSecsPerUpdate = 1_000 / 30.0;
		long previous = System.currentTimeMillis();
		long steps = 0;

		while (true) {

			long loopStartTime = System.currentTimeMillis();
			long elapsed = loopStartTime - previous;
			previous = loopStartTime;
			steps += elapsed;

			handleInput();

			while (steps >= milliSecsPerUpdate) {
				updateGameState();
				steps -= milliSecsPerUpdate;
			}

			render();
		}

	}

	private void render() {

	}

	private void updateGameState() {

	}

	private void handleInput() {

	}
}
