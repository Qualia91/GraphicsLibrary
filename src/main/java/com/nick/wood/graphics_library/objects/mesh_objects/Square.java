package com.nick.wood.graphics_library.objects.mesh_objects;

import com.nick.wood.graphics_library.Material;
import com.nick.wood.graphics_library.Vertex;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Square implements MeshObject {

	private final Mesh mesh;
	private final Material material;
	private Transform transformation;
	private boolean textureViaFBOFlag = false;

	// package private so you have to use builder so builder can build mesh's when open gl is initialised
	Square(Material material, Transform transformation) {
		this.transformation = transformation;
		mesh = new Mesh(new Vertex[] {
				new Vertex(new Vec3f(0.0f, -0.5f,  0.5f), new Vec2f(0.0f, 1.0f), Vec3f.X.neg(), Vec3f.Y.neg(), Vec3f.Z.neg()),
				new Vertex(new Vec3f(0.0f,  0.5f,  0.5f), new Vec2f(0.0f, 0.0f), Vec3f.X.neg(), Vec3f.Y.neg(), Vec3f.Z.neg()),
				new Vertex(new Vec3f(0.0f,  0.5f, -0.5f), new Vec2f(1.0f, 0.0f), Vec3f.X.neg(), Vec3f.Y.neg(), Vec3f.Z.neg()),
				new Vertex(new Vec3f(0.0f, -0.5f, -0.5f), new Vec2f(1.0f, 1.0f), Vec3f.X.neg(), Vec3f.Y.neg(), Vec3f.Z.neg())
		}, new int[]{
				0, 1, 2,
				3, 0, 2
		}, material, false, true);
		this.material = material;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setTransformation(Transform transformation) {
		this.transformation = transformation;
	}

	@Override
	public Transform getMeshTransformation() {
		return transformation;
	}

	@Override
	public String getStringToCompare() {
		return "SQUARE" + material.getPath();
	}

	@Override
	public void setTextureViaFBO(boolean flag) {
		this.textureViaFBOFlag = flag;
	}

	@Override
	public boolean isTextureViaFBOFlag() {
		return textureViaFBOFlag;
	}
}
