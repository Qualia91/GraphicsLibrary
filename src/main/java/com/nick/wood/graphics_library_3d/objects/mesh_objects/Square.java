package com.nick.wood.graphics_library_3d.objects.mesh_objects;

import com.nick.wood.graphics_library_3d.Material;
import com.nick.wood.graphics_library_3d.Vertex;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Square implements MeshObject {

	private final Mesh mesh;
	private final Material material;

	public Square(Material material) {
		mesh = new Mesh(new Vertex[] {
				new Vertex(new Vec3f(0.0f, -0.5f,  0.5f), new Vec2f(0.0f, 0.0f), Vec3f.X.neg()),
				new Vertex(new Vec3f(0.0f,  0.5f,  0.5f), new Vec2f(0.0f, 1.0f), Vec3f.X.neg()),
				new Vertex(new Vec3f(0.0f,  0.5f, -0.5f), new Vec2f(1.0f, 1.0f), Vec3f.X.neg()),
				new Vertex(new Vec3f(0.0f, -0.5f, -0.5f), new Vec2f(1.0f, 0.0f), Vec3f.X.neg())
		}, new int[]{
				0, 1, 2,
				3, 0, 2
		}, material);
		this.material = material;

	}

	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Matrix4f getRotationOfModel() {
		return Matrix4f.Identity;
	}

	@Override
	public String getStringToCompare() {
		return "SQUARE" + mesh.getMaterial().getPath();
	}
}
