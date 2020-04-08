package com.nick.wood.graphics_library.lighting;

import com.nick.wood.maths.objects.Vec3d;

public class PointLight implements Light {

	private Vec3d colour;
	private double intensity;
	private Attenuation attenuation;

	public PointLight(Vec3d colour, double intensity, Attenuation attenuation) {
		this.colour = colour;
		this.intensity = intensity;
		this.attenuation = attenuation;
	}

	public PointLight(Vec3d colour, double intensity) {
		this(colour, intensity, new Attenuation(1f, 0.5f, 0.25f));
	}

	public Vec3d getColour() {
		return colour;
	}

	public double getIntensity() {
		return intensity;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	@Override
	public LightType getType() {
		return LightType.POINT;
	}
}
