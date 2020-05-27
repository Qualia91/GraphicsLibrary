package com.nick.wood.graphics_library;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {
	public static final int SHADOW_MAP_WIDTH = 1024;

	public static final int SHADOW_MAP_HEIGHT = 1024;

	private final int depthMapFBO;

	private final Texture depthMap;

	public ShadowMap() throws Exception {
		// Create a FBO to render the depth map
		depthMapFBO = GL30.glGenFramebuffers();

		// Create the depth map texture
		depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL11.GL_DEPTH_COMPONENT);

		// Attach the the depth map texture to the FBO
		GL30.glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
		GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0);
		// Set only depth
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			throw new Exception("Could not create FrameBuffer");
		}

		// Unbind
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public Texture getDepthMapTexture() {
		return depthMap;
	}

	public int getDepthMapFBO() {
		return depthMapFBO;
	}

	public void destroy() {
		glDeleteFramebuffers(depthMapFBO);
		depthMap.destroy();
	}
}
