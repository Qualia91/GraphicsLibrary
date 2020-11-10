package com.boc_dev.graphics_library.objects.lighting;

import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.UUID;

public class DirectionalLight implements Light {

	private final UUID uuid;
	private Vec3f colour;
	private Vec3f direction;
	private float intensity;

	public DirectionalLight(UUID uuid, Vec3f colour, Vec3f direction, float intensity) {
		this.uuid = uuid;
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
	public int hashCode() {
		return uuid.hashCode();
	}

	public UUID getUuid() {
		return uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DirectionalLight) {
			return uuid.equals(((DirectionalLight) obj).getUuid());
		} else if (obj instanceof UUID) {
			return uuid.equals(obj);
		}
		return super.equals(obj);
	}

	@Override
	public LightType getType() {
		return LightType.DIRECTIONAL;
	}
}
