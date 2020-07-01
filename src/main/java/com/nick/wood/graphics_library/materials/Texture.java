package com.nick.wood.graphics_library.materials;

import java.io.IOException;

public interface Texture {
	void setId(int textureId);

	void destroy();

	void create() throws IOException;

	int getId();
}
