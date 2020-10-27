package com.nick.wood.graphics_library.logging;

import java.util.ArrayList;

public class StatsCalc {

	public StatsCalc(ArrayList<Stats> statsArrayList) {

		long startTime = 0;
		long beingGetRenderEvent = 0;
		long endGetRenderEvent = 0;
		long beginRender = 0;
		long endRender = 0;
		long finish = 0;

		float counter = 0;

		for (Stats stats : statsArrayList) {
			startTime += stats.getStats().getStartTime();
			beingGetRenderEvent += (stats.getStats().getBeingGetRenderEvent() - stats.getStats().getStartTime());
			endGetRenderEvent += (stats.getStats().getEndGetRenderEvent() - stats.getStats().getBeingGetRenderEvent());
			beginRender += (stats.getStats().getBeginRender() - stats.getStats().getEndGetRenderEvent());
			endRender += (stats.getStats().getEndRender() - stats.getStats().getBeginRender());
			finish += (stats.getStats().getFinish() - stats.getStats().getEndRender());
			counter++;
		}

		startTime/=counter;
		beingGetRenderEvent/=counter;
		endGetRenderEvent/=counter;
		beginRender/=counter;
		endRender/=counter;
		finish/=counter;

		System.out.println("Start to begin render get: " + beingGetRenderEvent);
		System.out.println("Being to end render get: " + endGetRenderEvent);
		System.out.println("End render get to begin render: " + beginRender);
		System.out.println("begin render to end render: " + endRender);
		System.out.println("end render to finish: " + finish);

	}
}
