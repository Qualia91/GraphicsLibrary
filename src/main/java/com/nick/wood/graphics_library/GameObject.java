package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.mesh_objects.MeshGroup;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Vec3d;

public interface GameObject {

	void update();
	boolean isPlayer();
	Vec3d getPosition();
	void setPosition(Vec3d position);
	Matrix4d getRotation();
	void setRotation(Matrix4d rotation);
	Vec3d getScale();
	void setScale(Vec3d scale);
	MeshGroup getMeshGroup();
	void rotateLeft();
	void rotateRight();

}
