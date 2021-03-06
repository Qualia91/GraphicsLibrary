package com.boc_dev.graphics_library.objects.materials;

import java.io.IOException;

public interface Texture {

	void destroy();

	void create() throws IOException;

	int getId();

	void setId(int textureId);
}
