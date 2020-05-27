package com.nick.wood.graphics_library.utils;

import java.io.*;

public class FileUtils {

	public static String loadAsString(String path) {
		StringBuilder stringBuilder = new StringBuilder();

		try (InputStreamReader inputStreamReader = new InputStreamReader(FileUtils.class.getResourceAsStream(path));
		     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}

}
