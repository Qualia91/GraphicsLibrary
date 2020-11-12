package com.boc_dev.graphics_library.objects.mesh_objects;

import com.boc_dev.graphics_library.objects.DrawVisitor;
import com.boc_dev.graphics_library.objects.mesh_objects.renderer_objects.OpenGlMesh;
import com.boc_dev.graphics_library.objects.render_scene.InstanceObject;
import com.boc_dev.graphics_library.objects.mesh_objects.renderer_objects.RendererObject;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

public class SingleMesh implements Mesh {

	private final Vertex[] vertices;
	private final int[] indices;
	private RendererObject rendererObject;


	public SingleMesh(Vertex[] vertices, int[] indices, RendererObject rendererObject) {
		this.vertices = vertices;
		this.indices = indices;
		this.rendererObject = rendererObject;
	}

	public void create() {

		rendererObject.create(vertices, indices);

	}

	public void initRender() {
		rendererObject.initRender();
	}

	public void endRender() {
		rendererObject.endRender();
	}

	public void destroy() {
		rendererObject.destroy();
	}

	public void draw(DrawVisitor drawVisitor, ArrayList<InstanceObject> value) {

		drawVisitor.draw(this, value);

	}

	@Override
	public int size() {
		return indices.length;
	}

	@Override
	public MeshType getType() {
		return MeshType.SINGLE;
	}


	public RendererObject getRendererObject() {
		return rendererObject;
	}
}
