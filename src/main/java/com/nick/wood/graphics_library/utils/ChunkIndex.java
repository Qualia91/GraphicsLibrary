package com.nick.wood.graphics_library.utils;

import java.util.Objects;

public class ChunkIndex {
	private final int x;
	private final int y;

	public ChunkIndex(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChunkIndex that = (ChunkIndex) o;
		return x == that.x &&
				y == that.y;
	}

	@Override
	public int hashCode() {
		int hashCode = 23;
		hashCode = hashCode * 31 + x;
		hashCode = hashCode * 31 + y;
		return hashCode;
	}
}
