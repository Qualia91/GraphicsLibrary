package com.nick.wood.graphics_library.objects.mesh_objects;

public class MeshDefinition {

	private Vertex[] vertices;
	private int[] indices;

	public MeshDefinition(Vertex[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public void setIndices(int[] indices) {
		this.indices = indices;
	}
}
