package com.nick.wood.graphics_library.frame_buffers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;

public class PickingFrameBuffer {

	final int WIDTH;
	final int HEIGHT;

	private int frameBuffer;
	private int texture;
	private int depthBuffer;

	public PickingFrameBuffer(int WIDTH, int HEIGHT) {
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		frameBuffer = createFrameBuffer();
		texture = createTextureAttachment();
		depthBuffer = createDepthBufferAttachment();

		// create draw buffers (just colour this one)
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		// Verify that the FBO is correct
		int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);

		if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("FB error, status: " + status);
		}
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
		unbindCurrentFrameBuffer();

	}

	public void destroy() {
		GL30.glDeleteFramebuffers(frameBuffer);
		GL11.glDeleteTextures(texture);
		GL30.glDeleteRenderbuffers(depthBuffer);
	}

	public void unbindCurrentFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void bindFrameBuffer() {
		GL30.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	private int createFrameBuffer() {
		// create
		int frameBuffer = GL30.glGenFramebuffers();
		// bind
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		return frameBuffer;
	}

	private int createTextureAttachment() {
		// generate
		int texture = GL11.glGenTextures();
		// bind to 2d texture target
		GL11.glBindTexture(GL_TEXTURE_2D, texture);
		// specify 2d texture with following props
		// target if a 2d texture
		// level of detail number is 0
		// Internal format of RGB
		// wdith and WIDTH of texture
		// 0 border WIDTH
		// Basic texel info
		GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGB32F, WIDTH, HEIGHT, 0, GL11.GL_RGB, GL11.GL_FLOAT, (ByteBuffer) null);
		// attach frame buffer object to newly created texture
		GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
				GL30.GL_COLOR_ATTACHMENT0,
				GL_TEXTURE_2D,
				texture,
				0);
		return texture;
	}

	private int createDepthBufferAttachment() {
		// create
		int depthBuffer = GL30.glGenTextures();
		// bind
		GL30.glBindTexture(GL_TEXTURE_2D, depthBuffer);
		GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, WIDTH, HEIGHT,
				0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D,
				depthBuffer, 0);
		return depthBuffer;
	}

	public int getFrameBuffer() {
		return frameBuffer;
	}

	public int getTexture() {
		return texture;
	}

	public int getDepthBuffer() {
		return depthBuffer;
	}
}
