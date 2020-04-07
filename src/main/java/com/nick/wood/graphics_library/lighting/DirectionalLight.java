package com.nick.wood.graphics_library.lighting;

import com.nick.wood.maths.objects.Vec3d;

public class DirectionalLight {

	private Vec3d colour;
	private Vec3d direction;
	private float intensity;

	public DirectionalLight(Vec3d colour, Vec3d direction, float intensity) {
		this.colour = colour;
		this.direction = direction;
		this.intensity = intensity;
	}

	public Vec3d getColour() {
		return colour;
	}

	public void setColour(Vec3d colour) {
		this.colour = colour;
	}

	public Vec3d getDirection() {
		return direction;
	}

	public void setDirection(Vec3d direction) {
		this.direction = direction;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}
