package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;

public class MeshInstanceCounter {

	private final Mesh mesh;
	private long stepLastSeen;

	public MeshInstanceCounter(Mesh mesh, long stepLastSeen) {
		this.mesh = mesh;
		mesh.create();
		this.stepLastSeen = stepLastSeen;
	}

	public void seen(long stepLastSeen) {
		this.stepLastSeen = stepLastSeen;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public long getStepLastSeen() {
		return stepLastSeen;
	}
}
