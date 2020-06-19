package com.nick.wood.graphics_library.lighting;

import com.nick.wood.maths.objects.vector.Vec3f;

public class PointLight implements Light {

	private Vec3f colour;
	private float intensity;
	private Attenuation attenuation;

	public PointLight(Vec3f colour, float intensity, Attenuation attenuation) {
		this.colour = colour;
		this.intensity = intensity;
		this.attenuation = attenuation;
	}

	public PointLight(Vec3f colour, float intensity) {
		this(colour, intensity, new Attenuation(1f, 0.5f, 0.25f));
	}

	public Vec3f getColour() {
		return colour;
	}

	public float getIntensity() {
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
