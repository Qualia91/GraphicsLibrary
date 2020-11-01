package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.objects.mesh_objects.SingleMesh;

public class MeshInstanceCounter {

	private final SingleMesh singleMesh;
	private long stepLastSeen;

	public MeshInstanceCounter(SingleMesh singleMesh, long stepLastSeen) {
		this.singleMesh = singleMesh;
		singleMesh.create();
		this.stepLastSeen = stepLastSeen;
	}

	public void seen(long stepLastSeen) {
		this.stepLastSeen = stepLastSeen;
	}

	public SingleMesh getMesh() {
		return singleMesh;
	}

	public long getStepLastSeen() {
		return stepLastSeen;
	}
}
