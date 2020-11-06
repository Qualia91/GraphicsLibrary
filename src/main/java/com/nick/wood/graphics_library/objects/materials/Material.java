package com.nick.wood.graphics_library.objects.materials;

import com.nick.wood.graphics_library.Shader;
import com.nick.wood.graphics_library.objects.managers.TextureManager;

public interface Material {
	void initRender(TextureManager textureManager, Shader shader);

	void endRender();
}
