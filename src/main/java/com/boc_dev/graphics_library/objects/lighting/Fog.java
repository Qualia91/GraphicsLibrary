package com.boc_dev.graphics_library.objects.lighting;

import com.boc_dev.maths.objects.vector.Vec3f;

public class Fog {

	public static final Fog NOFOG = new Fog();
	private boolean active;
	private Vec3f colour;
	private float density;

	public Fog() {
		this.active = false;
		this.colour = Vec3f.ZERO;
		this.density = 0;
	}

	public Fog(boolean active, Vec3f colour, float density) {
		this.active = active;
		this.colour = colour;
		this.density = density;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Vec3f getColour() {
		return colour;
	}

	public void setColour(Vec3f colour) {
		this.colour = colour;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}
}
