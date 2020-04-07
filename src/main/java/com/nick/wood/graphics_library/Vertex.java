package com.nick.wood.graphics_library;


import com.nick.wood.maths.objects.Vec2f;
import com.nick.wood.maths.objects.Vec3d;

public class Vertex {

	private Vec3d pos;
	private Vec2f textureCoord;
	private Vec3d normal;

	public Vertex(Vec3d pos, Vec2f textureCoord, Vec3d normal) {
		this.pos = pos;
		this.textureCoord = textureCoord;
		this.normal = normal;
	}

	public Vec3d getPos() {
		return pos;
	}

	public void setPos(Vec3d pos) {
		this.pos = pos;
	}

	public Vec2f getTextureCoord() {
		return textureCoord;
	}

	public void setTextureCoord(Vec2f textureCoord) {
		this.textureCoord = textureCoord;
	}

	public Vec3d getNormal() {
		return normal;
	}

	public void setNormal(Vec3d normal) {
		this.normal = normal;
	}
}
