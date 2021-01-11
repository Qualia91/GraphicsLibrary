package com.boc_dev.graphics_library.utils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

	public InputStream loadFile(String fileName) {

		InputStream inputStream = null;

		// look for file in jar
		try {

			// get paths from src/main/resources/json
			List<Path> result = getPathsFromResourceJAR(fileName);
			for (Path path : result) {

				String filePathInJAR = path.toString();
				// Windows will returns /json/file1.json, cut the first /
				// the correct path should be json/file1.json
				if (filePathInJAR.startsWith("/")) {
					filePathInJAR = filePathInJAR.substring(1).replace("\\", File.pathSeparator);
				}

				// read a file from resource folder
				inputStream = getFileFromResourceAsStream(filePathInJAR);

			}

		} catch (Exception e) {
			// do nothing
		}

		// look for file in resource folder in ide
		if (inputStream == null) {
			inputStream = getClass().getResourceAsStream(fileName);
		}

		// if still null, check in the user library
		if (inputStream == null) {
			// try to find it in the user input folder via environment variable
			File file = new File(System.getenv("GRAPHICS_LIB_DATA") + fileName);
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return inputStream;
	}

	public void copyInputStreamToFile(InputStream input, File file) {

		try (OutputStream output = new FileOutputStream(file)) {
			input.transferTo(output);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

	}

	// get a file from the resources folder
	// works everywhere, IDEA, unit test and JAR file.
	public URL getFileFromResource(String fileName) {

		// The class loader that loaded the class
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(fileName);

		// the stream holding the file content
		if (resource == null) {
			throw new IllegalArgumentException("file not found! " + fileName);
		} else {
			return resource;
		}

	}

	// get a file from the resources folder
	// works everywhere, IDEA
	private InputStream getFileFromResourceAsStream(String fileName) {

		InputStream resourceAsStream = getClass().getResourceAsStream(fileName);

		// the stream holding the file content
		if (resourceAsStream == null) {
			throw new IllegalArgumentException("file not found! " + fileName);
		} else {
			return resourceAsStream;
		}

	}

	// Get all paths from a folder that inside the JAR file
	private List<Path> getPathsFromResourceJAR(String folder) throws Exception {

		List<Path> result;

		// get path of the current running JAR
		String jarPath = getClass().getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI()
				.getPath();

		// file walks JAR
		URI uri = URI.create("jar:file:" + jarPath);
		try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
			result = Files.walk(fs.getPath(folder))
					.filter(Files::isRegularFile)
					.collect(Collectors.toList());
		}

		return result;

	}

	// print input stream
	private void printInputStream(InputStream is) {

		try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
		     BufferedReader reader = new BufferedReader(streamReader)) {

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
