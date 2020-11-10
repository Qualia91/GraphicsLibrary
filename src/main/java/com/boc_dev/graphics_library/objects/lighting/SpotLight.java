package com.boc_dev.graphics_library.objects.lighting;

import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.UUID;

public class SpotLight implements Light {

	private final UUID uuid;
	private PointLight pointLight;
	private Vec3f coneDirection;
	private float coneAngle;

	public SpotLight(UUID uuid, PointLight pointLight, Vec3f coneDirection, float coneAngle) {
		this.uuid = uuid;
		this.pointLight = pointLight;
		this.coneDirection = coneDirection;
		this.coneAngle = coneAngle;
	}

	public PointLight getPointLight() {
		return pointLight;
	}

	public void setPointLight(PointLight pointLight) {
		this.pointLight = pointLight;
	}

	public Vec3f getConeDirection() {
		return coneDirection;
	}

	public void setConeDirection(Vec3f coneDirection) {
		this.coneDirection = coneDirection;
	}

	public float getConeAngle() {
		return coneAngle;
	}

	public void setConeAngle(float coneAngle) {
		this.coneAngle = coneAngle;
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
		if (obj instanceof SpotLight) {
			return uuid.equals(((SpotLight) obj).getUuid());
		} else if (obj instanceof UUID) {
			return uuid.equals(obj);
		}
		return super.equals(obj);
	}

	@Override
	public LightType getType() {
		return LightType.SPOT;
	}
}
