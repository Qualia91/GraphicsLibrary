package com.nick.wood.graphics_library_3d.lighting;

import com.nick.wood.maths.objects.vector.Vec3f;

public class DirectionalLight implements Light {

	private Vec3f colour;
	private Vec3f direction;
	private float intensity;

	public DirectionalLight(Vec3f colour, Vec3f direction, float intensity) {
		this.colour = colour;
		this.direction = direction;
		this.intensity = intensity;
	}

	public Vec3f getColour() {
		return colour;
	}

	public void setColour(Vec3f colour) {
		this.colour = colour;
	}

	public Vec3f getDirection() {
		return direction;
	}

	public void setDirection(Vec3f direction) {
		this.direction = direction;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	@Override
	public LightType getType() {
		return LightType.DIRECTIONAL;
	}
}
