package com.nick.wood.graphics_library.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Mesh;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec2f;
import com.nick.wood.maths.objects.Vec3d;

public class Square implements MeshObject{

	private Vec3d position, scale;
	private Matrix4d rotation;

	private final Mesh mesh = new Mesh(new Vertex[] {
			new Vertex(new Vec3d(-0.5,  0.5, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec2f(0.0f, 0.0f)),
			new Vertex(new Vec3d( 0.5,  0.5, 0.0), new Vec3d(0.0, 1.0, 0.0), new Vec2f(0.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5, -0.5, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec2f(1.0f, 1.0f)),
			new Vertex(new Vec3d(-0.5, -0.5, 0.0), new Vec3d(1.0, 1.0, 0.0), new Vec2f(1.0f, 0.0f))
	}, new int[]{
			0, 1, 2,
			0, 3, 2
	},
			new Material("/textures/texture.png"));

	public Square() {
	}

	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Matrix4d getTransformation() {
		return Matrix4d.Identity;
	}
}
