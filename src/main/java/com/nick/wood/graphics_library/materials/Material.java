package com.nick.wood.graphics_library.materials;

import com.nick.wood.maths.objects.vector.Vec3f;

public class Material {

	private float width, height;
	private String texturePath = "";
	private String normalMapPath = "";
	private Vec3f diffuseColour;
	private Vec3f specularColour;
	private float shininess;
	private float reflectance;
	private String idString;

	public Material(String path, Vec3f diffuseColour, Vec3f specularColour, float shininess, float reflectance) {
		this.texturePath = path;
		this.diffuseColour = diffuseColour;
		this.specularColour = specularColour;
		this.shininess = shininess;
		this.reflectance = reflectance;
	}

	public Material(String path) {
		this(path, Vec3f.ONE, Vec3f.ONE, 1, 1);
	}

	public void setNormalMap(String normalMapPath) {
		this.normalMapPath = normalMapPath;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public Vec3f getDiffuseColour() {
		return diffuseColour;
	}

	public void setDiffuseColour(Vec3f diffuseColour) {
		this.diffuseColour = diffuseColour;
	}

	public Vec3f getSpecularColour() {
		return specularColour;
	}

	public void setSpecularColour(Vec3f specularColour) {
		this.specularColour = specularColour;
	}

	public float getShininess() {
		return shininess;
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public float getReflectance() {
		return reflectance;
	}

	public void setReflectance(float reflectance) {
		this.reflectance = reflectance;
	}

	public String getNormalMapPath() {
		return normalMapPath;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	public void setNormalMapPath(String normalMapPath) {
		this.normalMapPath = normalMapPath;
	}

	public boolean hasNormalMap() {
		return !normalMapPath.isEmpty();
	}

	public void setId(String idString) {
		this.idString = idString;
	}

	public String getIdString() {
		return idString;
	}
}
