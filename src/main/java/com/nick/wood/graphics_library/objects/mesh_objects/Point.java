package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Point implements MeshObject {

	private final Matrix4f transformation;
	private final Mesh mesh;
	private final Material material;

	public Point(Matrix4f transformation, Material material) {
		this.transformation = transformation;
		mesh = new Mesh(
				new Vertex[] {new Vertex(new Vec3f(0.0f, -0.5f,  0.5f), new Vec2f(0.0f, 0.0f), Vec3f.ONE)}, new int[]{0},
				material,
				false);
		this.material = material;
	}

	@Override
	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Matrix4f getMeshTransformation() {
		return transformation;
	}

	@Override
	public String getStringToCompare() {
		return "POINT" + mesh.getMaterial().getPath();
	}
}
