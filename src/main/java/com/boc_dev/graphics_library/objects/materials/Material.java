package com.boc_dev.graphics_library.objects.materials;

import com.boc_dev.graphics_library.objects.managers.TextureManager;
import com.boc_dev.graphics_library.Shader;

public interface Material {
	void initRender(TextureManager textureManager, Shader shader);

	void endRender();
}
