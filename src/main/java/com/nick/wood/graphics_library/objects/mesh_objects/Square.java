package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Square implements MeshObject {

	private final Mesh mesh;
	private final Material material;
	private Matrix4f transformation;

	// package private so you have to use builder so builder can build mesh's when open gl is initialised
	Square(Material material, Matrix4f transformation) {
		this.transformation = transformation;
		mesh = new Mesh(new Vertex[] {
				new Vertex(new Vec3f(0.0f, -0.5f,  0.5f), new Vec2f(0.0f, 0.0f), Vec3f.X.neg()),
				new Vertex(new Vec3f(0.0f,  0.5f,  0.5f), new Vec2f(0.0f, 1.0f), Vec3f.X.neg()),
				new Vertex(new Vec3f(0.0f,  0.5f, -0.5f), new Vec2f(1.0f, 1.0f), Vec3f.X.neg()),
				new Vertex(new Vec3f(0.0f, -0.5f, -0.5f), new Vec2f(1.0f, 0.0f), Vec3f.X.neg())
		}, new int[]{
				0, 1, 2,
				3, 0, 2
		}, material, false);
		this.material = material;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setTransformation(Matrix4f transformation) {
		this.transformation = transformation;
	}

	@Override
	public Matrix4f getMeshTransformation() {
		return transformation;
	}

	@Override
	public String getStringToCompare() {
		return "SQUARE" + mesh.getMaterial().getPath();
	}
}
