package com.nick.wood.graphics_library;


import com.nick.wood.maths.objects.Vec2f;
import com.nick.wood.maths.objects.Vec3d;

public class Vertex {

	private Vec3d pos;
	private Vec3d normal;
	private Vec3d col;
	private Vec2f textureCoord;

	public Vertex(Vec3d pos, Vec3d normal, Vec3d col, Vec2f textureCoord) {
		this.pos = pos;
		this.normal = normal;
		this.col = col;
		this.textureCoord = textureCoord;
	}

	public Vec3d getPos() {
		return pos;
	}

	public void setPos(Vec3d pos) {
		this.pos = pos;
	}

	public Vec3d getNormal() {
		return normal;
	}

	public void setNormal(Vec3d normal) {
		this.normal = normal;
	}

	public Vec3d getCol() {
		return col;
	}

	public void setCol(Vec3d col) {
		this.col = col;
	}

	public Vec2f getTextureCoord() {
		return textureCoord;
	}

	public void setTextureCoord(Vec2f textureCoord) {
		this.textureCoord = textureCoord;
	}
}
