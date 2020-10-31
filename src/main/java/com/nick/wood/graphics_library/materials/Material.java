package com.nick.wood.graphics_library.materials;

import com.nick.wood.graphics_library.Shader;

public interface Material {
	void render(TextureManager textureManager, Shader shader);
}
