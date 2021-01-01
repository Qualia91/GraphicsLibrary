package com.boc_dev.graphics_library.objects.managers;

import com.boc_dev.graphics_library.objects.text.Font;

import java.io.IOException;
import java.util.HashMap;

public class FontManager {

	private final HashMap<String, Font> nameToFontMap = new HashMap<>();
	private Font defaultFont;

	public Font getFont(String fontName) {

		// see if font in available
		if (nameToFontMap.containsKey(fontName)) {
			return nameToFontMap.get(fontName);
		}

		// if not, try to make it
		try {
			Font font = new Font(fontName);
			font.create();
			nameToFontMap.put(fontName, font);
			return font;
		} catch (Exception e) {
			// if failed, return default
			return defaultFont;
		}

	}

	public void create(String defaultFontName) throws IOException {
		this.defaultFont = new Font(defaultFontName);
		defaultFont.create();
	}

	public void destroy() {
		for (Font value : nameToFontMap.values()) {
			value.destroy();
		}
	}

	public HashMap<String, Font> getNameToFontMap() {
		return nameToFontMap;
	}

	public void addFont(String fontName) {
		try {
			Font font = new Font(fontName);
			font.create();
			nameToFontMap.put(fontName, font);
		} catch (Exception e) {
			System.out.println("Cant make font " + fontName);
		}
	}

}
