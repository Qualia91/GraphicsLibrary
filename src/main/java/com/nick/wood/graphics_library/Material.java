package com.nick.wood.graphics_library;

import com.nick.wood.maths.objects.vector.Vec3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.IOException;

public class Material {

	private Texture texture = null;
	private Texture normalMap = null;
	private float width, height;
	private String path;
	private String normalMapPath;
	private Vec3f diffuseColour;
	private Vec3f specularColour;
	private float shininess;
	private float reflectance;

	public Material(String path, Vec3f diffuseColour, Vec3f specularColour, float shininess, float reflectance) {
		this.path = path;
		this.diffuseColour = diffuseColour;
		this.specularColour = specularColour;
		this.shininess = shininess;
		this.reflectance = reflectance;
	}

	public Material(String path) {
		this.path = path;
		this.diffuseColour = Vec3f.ONE;
		this.specularColour = Vec3f.ONE;
		this.shininess = 1;
		this.reflectance = 1;
	}

	public void create() {
		try {
			if (texture == null) {
				texture = new Texture(path, GL11.GL_LINEAR);
			}
			if (normalMapPath != null && normalMap == null) {
				normalMap = new Texture(normalMapPath, GL11.GL_LINEAR);
			}
		} catch (IOException e) {
			System.err.println("Cant find texture at " + path);
		}
		width = texture.getWidth();
		height = texture.getHeight();

	}

	public void destroy() {
		if (texture != null) {
			texture.destroy();
			GL13.glDeleteTextures(texture.getId());
		}
		if (normalMap != null) {
			normalMap.destroy();
			GL13.glDeleteTextures(normalMap.getId());
		}
	}

	public boolean hasNormalMap() {
		return this.normalMap != null;
	}

	public Texture getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(String normalMapPath) {
		this.normalMapPath = normalMapPath;
	}

	public Texture getTexture() {
		return texture;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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
}
