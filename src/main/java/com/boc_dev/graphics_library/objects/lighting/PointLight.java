package com.boc_dev.graphics_library.objects.lighting;

import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.UUID;

public class PointLight implements Light {

	private final UUID uuid;
	private Vec3f colour;
	private float intensity;
	private Attenuation attenuation;

	public PointLight(UUID uuid, Vec3f colour, float intensity, Attenuation attenuation) {
		this.uuid = uuid;
		this.colour = colour;
		this.intensity = intensity;
		this.attenuation = attenuation;
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

	public UUID getUuid() {
		return uuid;
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PointLight) {
			return uuid.equals(((PointLight) obj).getUuid());
		} else if (obj instanceof UUID) {
			return uuid.equals(obj);
		}
		return super.equals(obj);
	}

	@Override
	public LightType getType() {
		return LightType.POINT;
	}
}
