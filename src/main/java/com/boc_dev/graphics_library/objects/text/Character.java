package com.boc_dev.graphics_library.objects.text;

public class Character {
	private int id;
	private float xTextureCoord;
	private float yTextureCoord;
	private float xMaxTextureCoord;
	private float yMaxTextureCoord;
	private int xOffset;
	private int yOffset;
	private int sizeX;
	private int sizeY;
	private int xAdvance;


	public Character(int id, float xTextureCoord, float yTextureCoord, float xTexSize, float yTexSize, int xOffset, int yOffset, int sizeX, int sizeY, int xAdvance) {
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

	public int getxOffset() {
		return xOffset;
	}

	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}

	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}

	public int getSizeX() {
		return sizeX;
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

	public int getxAdvance() {
		return xAdvance;
	}

	public void setxAdvance(int xAdvance) {
		this.xAdvance = xAdvance;
	}
}
