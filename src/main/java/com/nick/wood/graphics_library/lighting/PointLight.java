package com.nick.wood.graphics_library.lighting;

import com.nick.wood.maths.objects.Vec3d;

public class PointLight {

	private Vec3d colour;
	private Vec3d position;
	private double intensity;
	private Attenuation attenuation;

	public PointLight(Vec3d colour, Vec3d position, double intensity, Attenuation attenuation) {
		this.colour = colour;
		this.position = position;
		this.intensity = intensity;
		this.attenuation = attenuation;
	}

	public PointLight(Vec3d colour, Vec3d position, double intensity) {
		this(colour, position, intensity, new Attenuation(1f, 0.5f, 0.25f));
	}

	public Vec3d getColour() {
		return colour;
	}

	public Vec3d getPosition() {
		return position;
	}

	public double getIntensity() {
		return intensity;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setPosition(Vec3d newPos) {
		this.position = newPos;
	}
}
