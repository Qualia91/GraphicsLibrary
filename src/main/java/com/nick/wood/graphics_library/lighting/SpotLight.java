package com.nick.wood.graphics_library.lighting;

import com.nick.wood.maths.objects.Vec3f;

public class SpotLight {

	private PointLight pointLight;
	private Vec3f coneDirection;
	private float coneAngle;

	public SpotLight(PointLight pointLight, Vec3f coneDirection, float coneAngle) {
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
}
