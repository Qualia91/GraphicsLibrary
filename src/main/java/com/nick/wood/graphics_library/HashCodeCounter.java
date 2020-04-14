package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.maths.objects.Matrix4d;
import com.nick.wood.maths.objects.Matrix4f;

import java.util.ArrayList;
import java.util.Objects;

public class HashCodeCounter {

	private final String stringToCompare;
	private final MeshObject meshObject;
	private int amount;
	private final Matrix4d rotationOfModel;
	ArrayList<Matrix4d> transforms = new ArrayList<>();

	public HashCodeCounter(String stringToCompare, MeshObject meshObject, Matrix4d rotationOfModel, Matrix4d transform) {
		this.stringToCompare = stringToCompare;
		this.meshObject = meshObject;
		this.rotationOfModel = rotationOfModel;
		this.transforms.add(transform);
		this.amount = 1;
	}

	public MeshObject getMeshObject() {
		return meshObject;
	}

	public Matrix4d getRotationOfModel() {
		return rotationOfModel;
	}

	public ArrayList<Matrix4d> getTransforms() {
		return transforms;
	}

	public int getAmount() {
		return amount;
	}

	public String getStringToCompare() {
		return stringToCompare;
	}

	public void addInstance(Matrix4d transform) {
		amount++;
		this.transforms.add(transform);
	}
}
