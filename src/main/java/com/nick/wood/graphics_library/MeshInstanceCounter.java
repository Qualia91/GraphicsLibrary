package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.objects.mesh_objects.Mesh;

public class MeshInstanceCounter {

	private final Mesh mesh;
	private int counter;

	public MeshInstanceCounter(Mesh mesh) {
		this.mesh = mesh;
		mesh.create();
		counter = 1;
	}

	public void increment() {
		counter++;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public int getCounter() {
		return counter;
	}

	public void resetCounter() {
		counter = 0;
	}
}
