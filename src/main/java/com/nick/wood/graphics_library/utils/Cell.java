package com.nick.wood.graphics_library.utils;

import com.nick.wood.maths.objects.vector.Vec2i;

public class Cell {
	private final Vec2i position;
	private boolean southPath;
	private boolean eastPath;
	private boolean northPath;
	private boolean westPath;

	public Cell(Vec2i position) {
		this.position = position;
		this.southPath = false;
		this.eastPath = false;
		this.northPath = false;
		this.westPath = false;
	}

	public Vec2i getPosition() {
		return position;
	}

	public boolean isSouthPath() {
		return southPath;
	}

	public void setSouthPath(boolean southPath) {
		this.southPath = southPath;
	}

	public boolean isEastPath() {
		return eastPath;
	}

	public void setEastPath(boolean eastPath) {
		this.eastPath = eastPath;
	}

	public boolean isNorthPath() {
		return northPath;
	}

	public void setNorthPath(boolean northPath) {
		this.northPath = northPath;
	}

	public boolean isWestPath() {
		return westPath;
	}

	public void setWestPath(boolean westPath) {
		this.westPath = westPath;
	}
}
