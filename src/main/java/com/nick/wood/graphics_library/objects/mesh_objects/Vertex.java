package com.nick.wood.graphics_library.objects.mesh_objects;


import com.nick.wood.maths.objects.vector.Vec2f;
import com.nick.wood.maths.objects.vector.Vec3f;

public class Vertex {

	public static final int NUM_OF_FLOATS = 14;

	public static final int POSITION_FLOAT_COUNT = 3;
	public static final int TEXTURE_COORD_FLOAT_COUNT = 2;
	public static final int NORMAL_FLOAT_COUNT = 3;
	public static final int TANGENT_FLOAT_COUNT = 3;
	public static final int BITANGENT_FLOAT_COUNT = 3;

	public static final int POSITION_OFFSET = 0;
	public static final int TEXTURE_COORD_OFFSET = 3;
	public static final int NORMAL_OFFSET = 5;
	public static final int TANGENT_OFFSET = 8;
	public static final int BITANGENT_OFFSET = 11;

	private Vec3f pos;
	private Vec2f textureCoord;
	private Vec3f normal;
	private Vec3f tangent;
	private Vec3f bitangent;

	public Vertex(Vec3f pos, Vec2f textureCoord, Vec3f normal, Vec3f tangent, Vec3f bitangent) {
		this.pos = pos;
		this.textureCoord = textureCoord;
		this.normal = normal;
		this.tangent = tangent;
		this.bitangent = bitangent;
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
