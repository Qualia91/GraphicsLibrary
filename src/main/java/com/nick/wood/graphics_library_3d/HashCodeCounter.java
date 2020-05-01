package com.nick.wood.graphics_library_3d;

import com.nick.wood.graphics_library_3d.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.matrix.Matrix4f;

import java.util.ArrayList;

public class HashCodeCounter {

	private final String stringToCompare;
	private final MeshObject meshObject;
	private int amount;
	private final Matrix4f rotationOfModel;
	ArrayList<Matrix4f> transforms = new ArrayList<>();

	public HashCodeCounter(String stringToCompare, MeshObject meshObject, Matrix4f rotationOfModel, Matrix4f transform) {
		this.stringToCompare = stringToCompare;
		this.meshObject = meshObject;
		this.rotationOfModel = rotationOfModel;
		this.transforms.add(transform);
		this.amount = 1;
	}

	public MeshObject getMeshObject() {
		return meshObject;
	}

	public Matrix4f getRotationOfModel() {
		return rotationOfModel;
	}

	public ArrayList<Matrix4f> getTransforms() {
		return transforms;
	}

	public int getAmount() {
		return amount;
	}

	public String getStringToCompare() {
		return stringToCompare;
	}

	public void addInstance(Matrix4f transform) {
		amount++;
		this.transforms.add(transform);
	}
}
