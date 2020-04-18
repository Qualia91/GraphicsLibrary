package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;

public class Mesh {

	private MeshCommonData meshCommonData;

	public Mesh(Vertex[] vertices, int[] indices, Material material) {
		meshCommonData = new MeshCommonData(vertices, indices, material);
	}

	public void create() {
		meshCommonData.create();
	}

	public void destroy() {
		meshCommonData.destroy();
	}

	public Vertex[] getVertices() {
		return meshCommonData.getVertices();
	}

	public int[] getIndices() {
		return meshCommonData.getIndices();
	}

	public int getVao() {
		return meshCommonData.getVao();
	}

	public int getPbo() {
		return meshCommonData.getPbo();
	}

	public int getIbo() {
		return meshCommonData.getIbo();
	}

	public int getTbo() {
		return meshCommonData.getTbo();
	}

	public Material getMaterial() {
		return meshCommonData.getMaterial();
	}

	public int getNbo() {
		return meshCommonData.getNbo();
	}

	public void initRender() {
		meshCommonData.initRender();
	}

	public void endRender() {
		meshCommonData.endRender();
	}

	public int getVertexCount() {
		return meshCommonData.getVertexCount();
	}
}
