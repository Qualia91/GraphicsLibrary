package com.nick.wood.graphics_library.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Mesh;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.Vec2f;
import com.nick.wood.maths.objects.Vec3d;

public class Cube implements MeshObject {

	private MeshTransform meshTransform;

	private final Mesh mesh = new Mesh(new Vertex[] {
			//Back face
			new Vertex(new Vec3d(-0.5,  0.5, -0.5), Vec3d.X, new Vec3d(1.0, 0.0, 0.0), new Vec2f(0.0f, 0.0f)),
			new Vertex(new Vec3d(-0.5, -0.5, -0.5), Vec3d.X, new Vec3d(0.0, 1.0, 0.0), new Vec2f(0.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5, -0.5, -0.5), Vec3d.X, new Vec3d(0.0, 0.0, 1.0), new Vec2f(1.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5,  0.5, -0.5), Vec3d.X, new Vec3d(1.0, 1.0, 0.0), new Vec2f(1.0f, 0.0f)),

			//Front face
			new Vertex(new Vec3d(-0.5,  0.5,  0.5), Vec3d.X.neg(), new Vec3d(1.0, 0.0, 0.0), new Vec2f(0.0f, 0.0f)),
			new Vertex(new Vec3d(-0.5, -0.5,  0.5), Vec3d.X.neg(), new Vec3d(0.0, 1.0, 0.0), new Vec2f(0.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5, -0.5,  0.5), Vec3d.X.neg(), new Vec3d(0.0, 0.0, 1.0), new Vec2f(1.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5,  0.5,  0.5), Vec3d.X.neg(), new Vec3d(1.0, 1.0, 0.0), new Vec2f(1.0f, 0.0f)),

			//Right face
			new Vertex(new Vec3d( 0.5,  0.5, -0.5), Vec3d.Y.neg(), new Vec3d(1.0, 0.0, 0.0), new Vec2f(0.0f, 0.0f)),
			new Vertex(new Vec3d( 0.5, -0.5, -0.5), Vec3d.Y.neg(), new Vec3d(0.0, 1.0, 0.0), new Vec2f(0.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5, -0.5,  0.5), Vec3d.Y.neg(), new Vec3d(0.0, 0.0, 1.0), new Vec2f(1.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5,  0.5,  0.5), Vec3d.Y.neg(), new Vec3d(1.0, 1.0, 0.0), new Vec2f(1.0f, 0.0f)),

			//Left face
			new Vertex(new Vec3d(-0.5,  0.5, -0.5), Vec3d.Y, new Vec3d(1.0, 0.0, 0.0), new Vec2f(0.0f, 0.0f)),
			new Vertex(new Vec3d(-0.5, -0.5, -0.5), Vec3d.Y, new Vec3d(0.0, 1.0, 0.0), new Vec2f(0.0f, 1.0f)),
			new Vertex(new Vec3d(-0.5, -0.5,  0.5), Vec3d.Y, new Vec3d(0.0, 0.0, 1.0), new Vec2f(1.0f, 1.0f)),
			new Vertex(new Vec3d(-0.5,  0.5,  0.5), Vec3d.Y, new Vec3d(1.0, 1.0, 0.0), new Vec2f(1.0f, 0.0f)),

			//Top face
			new Vertex(new Vec3d(-0.5f,  0.5f,  0.5f), Vec3d.Z, new Vec3d(1.0, 0.0, 0.0), new Vec2f(0.0f, 0.0f)),
			new Vertex(new Vec3d(-0.5f,  0.5f, -0.5f), Vec3d.Z, new Vec3d(0.0, 1.0, 0.0), new Vec2f(0.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5f,  0.5f, -0.5f), Vec3d.Z, new Vec3d(0.0, 0.0, 1.0), new Vec2f(1.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5f,  0.5f,  0.5f), Vec3d.Z, new Vec3d(1.0, 1.0, 0.0), new Vec2f(1.0f, 0.0f)),

			//Bottom face
			new Vertex(new Vec3d(-0.5f, -0.5f,  0.5f), Vec3d.Z.neg(), new Vec3d(1.0, 0.0, 0.0), new Vec2f(0.0f, 0.0f)),
			new Vertex(new Vec3d(-0.5f, -0.5f, -0.5f), Vec3d.Z.neg(), new Vec3d(0.0, 1.0, 0.0), new Vec2f(0.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5f, -0.5f, -0.5f), Vec3d.Z.neg(), new Vec3d(0.0, 0.0, 1.0), new Vec2f(1.0f, 1.0f)),
			new Vertex(new Vec3d( 0.5f, -0.5f,  0.5f), Vec3d.Z.neg(), new Vec3d(1.0, 1.0, 0.0), new Vec2f(1.0f, 0.0f)),
	},
			new int[] {
					//Back face
					0, 1, 3,
					3, 1, 2,

					//Front face
					4, 5, 7,
					7, 5, 6,

					//Right face
					8, 9, 11,
					11, 9, 10,

					//Left face
					12, 13, 15,
					15, 13, 14,

					//Top face
					16, 17, 19,
					19, 17, 18,

					//Bottom face
					20, 21, 23,
					23, 21, 22
			},
			new Material("/textures/texture.png"));

	public Cube(MeshTransform meshTransform) {
		this.meshTransform = meshTransform;
	}

	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public MeshTransform getModelTransform() {
		return meshTransform;
	}
}
