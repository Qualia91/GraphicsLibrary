package com.nick.wood.graphics_library.logging;

public class Logger {

	private final StringBuilder stringBuilder = new StringBuilder();

	public StringBuilder getStringBuilder() {
		return stringBuilder;
	}

	public void finish() {
		System.out.println(stringBuilder.toString());
		stringBuilder.setLength(0);
	}
}
