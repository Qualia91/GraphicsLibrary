package com.boc_dev.graphics_library.text;

public class Character {
	private int id;
	private int xTextureCoord;
	private int yTextureCoord;
	private int xMaxTextureCoord;
	private int yMaxTextureCoord;
	private int xOffset;
	private int yOffset;
	private int sizeX;
	private int sizeY;
	private int xAdvance;


	public Character(int id, int xTextureCoord, int yTextureCoord, int xMaxTextureCoord, int yMaxTextureCoord, int xOffset, int yOffset, int sizeX, int sizeY, int xAdvance) {
		this.id = id;
		this.xTextureCoord = xTextureCoord;
		this.yTextureCoord = yTextureCoord;
		this.xMaxTextureCoord = xMaxTextureCoord;
		this.yMaxTextureCoord = yMaxTextureCoord;
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

	public int getxTextureCoord() {
		return xTextureCoord;
	}

	public void setxTextureCoord(int xTextureCoord) {
		this.xTextureCoord = xTextureCoord;
	}

	public int getyTextureCoord() {
		return yTextureCoord;
	}

	public void setyTextureCoord(int yTextureCoord) {
		this.yTextureCoord = yTextureCoord;
	}

	public int getxMaxTextureCoord() {
		return xMaxTextureCoord;
	}

	public void setxMaxTextureCoord(int xMaxTextureCoord) {
		this.xMaxTextureCoord = xMaxTextureCoord;
	}

	public int getyMaxTextureCoord() {
		return yMaxTextureCoord;
	}

	public void setyMaxTextureCoord(int yMaxTextureCoord) {
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
