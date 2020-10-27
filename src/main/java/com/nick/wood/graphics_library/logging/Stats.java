package com.nick.wood.graphics_library.logging;

public class Stats {
	private StatCollector statCollector;

	public Stats() {
		this.statCollector = new StatCollector();
	}

	public void start(long startTime) {
		this.statCollector.setStartTime(startTime);
	}

	public void beginGetRenderEvent(long beingGetRenderEvent) {
		this.statCollector.setBeingGetRenderEvent(beingGetRenderEvent);
	}

	public void endGetRenderEvent(long endGetRenderEvent) {
		this.statCollector.setEndGetRenderEvent(endGetRenderEvent);
	}

	public void beginRender(long beginRender) {
		this.statCollector.setBeginRender(beginRender);
	}

	public void endRender(long endRender) {
		this.statCollector.setEndRender(endRender);
	}

	public void finish(long finish) {
		this.statCollector.setFinish(finish);
	}

	public StatCollector getStats() {
		return statCollector;
	}
}
