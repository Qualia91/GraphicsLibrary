package com.boc_dev.graphics_library.objects.text;

public class Character {

	private int id;
	private float xTextureCoord;
	private float yTextureCoord;
	private float xMaxTextureCoord;
	private float yMaxTextureCoord;
	private float xOffset;
	private float yOffset;
	private float sizeX;
	private float sizeY;
	private float xAdvance;
	private final float base;

	public Character(int id,
	                 float xTextureCoord,
	                 float yTextureCoord,
	                 float xTexSize,
	                 float yTexSize,
	                 float xOffset,
	                 float yOffset,
	                 float sizeX,
	                 float sizeY,
	                 float xAdvance,
	                 float base) {
		this.id = id;
		this.xTextureCoord = xTextureCoord;
		this.yTextureCoord = yTextureCoord;
		this.xMaxTextureCoord = xTextureCoord + xTexSize;
		this.yMaxTextureCoord = yTextureCoord + yTexSize;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.xAdvance = xAdvance;
		this.base = base;
	}

	public float getBase() {
		return base;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getxTextureCoord() {
		return xTextureCoord;
	}

	public void setxTextureCoord(float xTextureCoord) {
		this.xTextureCoord = xTextureCoord;
	}

	public float getyTextureCoord() {
		return yTextureCoord;
	}

	public void setyTextureCoord(float yTextureCoord) {
		this.yTextureCoord = yTextureCoord;
	}

	public float getxMaxTextureCoord() {
		return xMaxTextureCoord;
	}

	public void setxMaxTextureCoord(float xMaxTextureCoord) {
		this.xMaxTextureCoord = xMaxTextureCoord;
	}

	public float getyMaxTextureCoord() {
		return yMaxTextureCoord;
	}

	public void setyMaxTextureCoord(float yMaxTextureCoord) {
		this.yMaxTextureCoord = yMaxTextureCoord;
	}

	public float getxOffset() {
		return xOffset;
	}

	public void setxOffset(float xOffset) {
		this.xOffset = xOffset;
	}

	public float getyOffset() {
		return yOffset;
	}

	public void setyOffset(float yOffset) {
		this.yOffset = yOffset;
	}

	public float getSizeX() {
		return sizeX;
	}

	public void setSizeX(float sizeX) {
		this.sizeX = sizeX;
	}

	public float getSizeY() {
		return sizeY;
	}

	public void setSizeY(float sizeY) {
		this.sizeY = sizeY;
	}

	public float getxAdvance() {
		return xAdvance;
	}

	public void setxAdvance(float xAdvance) {
		this.xAdvance = xAdvance;
	}
}
