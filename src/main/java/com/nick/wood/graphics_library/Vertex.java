package com.nick.wood.graphics_library;


import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Vertex {

	private boolean hasNormalMapping;
	private Vec3f pos;
	private Vec2f textureCoord;
	private Vec3f normal;
	private Vec3f tangent;
	private Vec3f bitangent;

	public Vertex(Vec3f pos, Vec2f textureCoord, Vec3f normal) {
		this.pos = pos;
		this.textureCoord = textureCoord;
		this.normal = normal;
		this.hasNormalMapping = false;
	}

	public Vertex(Vec3f pos, Vec2f textureCoord, Vec3f normal, Vec3f tangent, Vec3f bitangent) {
		this.pos = pos;
		this.textureCoord = textureCoord;
		this.normal = normal;
		this.tangent = tangent;
		this.bitangent = bitangent;
		this.hasNormalMapping = true;
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

	public Vec3f getTangent() {
		return tangent;
	}

	public boolean isHasNormalMapping() {
		return hasNormalMapping;
	}

	public void setHasNormalMapping(boolean hasNormalMapping) {
		this.hasNormalMapping = hasNormalMapping;
	}

	public void setTangent(Vec3f tangent) {
		this.tangent = tangent;
	}

	public Vec3f getBitangent() {
		return bitangent;
	}

	public void setBitangent(Vec3f bitangent) {
		this.bitangent = bitangent;
	}
}
