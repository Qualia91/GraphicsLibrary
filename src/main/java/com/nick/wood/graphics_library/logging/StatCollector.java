package com.nick.wood.graphics_library.logging;

public class StatCollector {
	private long startTime;
	private long beingGetRenderEvent;
	private long endGetRenderEvent;
	private long beginRender;
	private long endRender;
	private long finish;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getBeingGetRenderEvent() {
		return beingGetRenderEvent;
	}

	public void setBeingGetRenderEvent(long beingGetRenderEvent) {
		this.beingGetRenderEvent = beingGetRenderEvent;
	}

	public long getEndGetRenderEvent() {
		return endGetRenderEvent;
	}

	public void setEndGetRenderEvent(long endGetRenderEvent) {
		this.endGetRenderEvent = endGetRenderEvent;
	}

	public long getBeginRender() {
		return beginRender;
	}

	public void setBeginRender(long beginRender) {
		this.beginRender = beginRender;
	}

	public long getEndRender() {
		return endRender;
	}

	public void setEndRender(long endRender) {
		this.endRender = endRender;
	}

	public long getFinish() {
		return finish;
	}

	public void setFinish(long finish) {
		this.finish = finish;
	}

}
