package com.nick.wood.graphics_library;


import com.nick.wood.maths.objects.Vec2f;
import com.nick.wood.maths.objects.Vec3d;
import com.nick.wood.maths.objects.Vec3f;

public class Vertex {

	private Vec3f pos;
	private Vec2f textureCoord;
	private Vec3f normal;

	public Vertex(Vec3f pos, Vec2f textureCoord, Vec3f normal) {
		this.pos = pos;
		this.textureCoord = textureCoord;
		this.normal = normal;
	}

	public Vec3f getPos() {
		return pos;
	}

	public void setPos(Vec3f pos) {
		this.pos = pos;
	}

	public Vec2f getTextureCoord() {
		return textureCoord;
	}

	public void setTextureCoord(Vec2f textureCoord) {
		this.textureCoord = textureCoord;
	}

	public Vec3f getNormal() {
		return normal;
	}

	public void setNormal(Vec3f normal) {
		this.normal = normal;
	}
}
