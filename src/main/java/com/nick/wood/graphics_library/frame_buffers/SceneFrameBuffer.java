package com.nick.wood.graphics_library.frame_buffers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.nio.ByteBuffer;

public class SceneFrameBuffer {

	final int width;
	final int height;

	private int frameBuffer;
	private int texture;
	private int depthBuffer;

	public SceneFrameBuffer(int width, int height) {
		this.width = width;
		this.height = height;
		frameBuffer = createFrameBuffer();
		texture = createTextureAttachment();
		depthBuffer = createDepthBufferAttachment();
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
		bindFrameBuffer(frameBuffer);
	}

	// bind our own frame buffer using this
	private void bindFrameBuffer(int frameBuffer) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	private int createFrameBuffer() {
		// create
		int frameBuffer = GL30.glGenFramebuffers();
		// bind
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		// create draw buffers (just colour this one)
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		return frameBuffer;
	}

	private int createTextureAttachment() {
		// generate
		int texture = GL11.glGenTextures();
		// bind to 2d texture target
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		// specify 2d texture with following props
		// target if a 2d texture
		// level of detail number is 0
		// Internal format of RGB
		// wdith and WIDTH of texture
		// 0 border WIDTH
		// Basic texel info
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		// attach frame buffer object to newly craeted texture
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture, 0);
		return texture;
	}

	private int createDepthBufferAttachment() {
		// create
		int depthBuffer = GL30.glGenRenderbuffers();
		// bind
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		// tell opengl what we are going to store in the buffer
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT32, width, height);
		// attach to frame buffer as depth buffer
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
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
