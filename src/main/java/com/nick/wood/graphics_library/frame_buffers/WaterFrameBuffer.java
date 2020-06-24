package com.nick.wood.graphics_library.frame_buffers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class WaterFrameBuffer {

	protected static final int REFLECTION_WIDTH = 640;
	private static final int REFLECTION_HEIGHT = 360;

	protected static final int REFRACTION_WIDTH = 1280;
	private static final int REFRACTION_HEIGHT = 720;

	private int reflectionFrameBuffer;
	private int reflectionTexture;
	private int reflectionDepthBuffer;
	private int refractionFrameBuffer;
	private int refractionTexture;
	private int refractionDepthBuffer;

	public WaterFrameBuffer(int displayWidth, int displayHeight) {
		initReflectionFrameBuffer(displayWidth, displayHeight);
		initRefractionFrameBuffer(displayWidth, displayHeight);
	}

	private void initReflectionFrameBuffer(int displayWidth, int displayHeight) {
		reflectionFrameBuffer = createFrameBuffer();
		reflectionTexture = createTextureAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);
		reflectionDepthBuffer = createDepthBufferAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);
		unbindCurrentFrameBuffer(displayWidth, displayHeight);
	}

	private void initRefractionFrameBuffer(int displayWidth, int displayHeight) {
		refractionFrameBuffer = createFrameBuffer();
		refractionTexture = createTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);
		refractionDepthBuffer = createDepthBufferAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);
		unbindCurrentFrameBuffer(displayWidth, displayHeight);
	}

	public void destroy() {
		GL30.glDeleteFramebuffers(reflectionFrameBuffer);
		GL11.glDeleteTextures(reflectionTexture);
		GL30.glDeleteRenderbuffers(reflectionDepthBuffer);
		GL30.glDeleteFramebuffers(refractionFrameBuffer);
		GL11.glDeleteTextures(refractionTexture);
		GL30.glDeleteRenderbuffers(refractionDepthBuffer);
	}

	public void unbindCurrentFrameBuffer(int displayWidth, int displayHeight) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, displayWidth, displayHeight);
	}

	public void bindReflectionFrameBuffer() {
		bindFrameBuffer(reflectionFrameBuffer, REFLECTION_WIDTH, REFLECTION_HEIGHT);
	}

	public void bindRefractionFrameBuffer() {
		bindFrameBuffer(refractionFrameBuffer, REFRACTION_WIDTH, REFRACTION_HEIGHT);
	}

	// bind our own frame buffer using this
	private void bindFrameBuffer(int frameBuffer, int width, int height) {
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

	private int createTextureAttachment(int width, int height) {
		// generate
		int texture = GL11.glGenTextures();
		// bind to 2d texture target
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		// specify 2d texture with following props
		// target if a 2d texture
		// level of detail number is 0
		// Internal format of RGB
		// wdith and height of texture
		// 0 border width
		// Basic texel info
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		// attach frame buffer object to newly craeted texture
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture, 0);
		return texture;
	}

	private int createDepthBufferAttachment(int width, int height) {
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

	private int createDepthTextureAttachment(int width, int height) {
		// generate
		int texture = GL11.glGenTextures();
		// bind to 2d texture target
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		// specify 2d texture with following props
		// target if a 2d texture
		// level of detail number is 0
		// Internal format of RGB
		// wdith and height of texture
		// 0 border width
		// Basic texel info
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height,
				0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		// attach frame buffer object to newly created texture
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, texture, 0);
		return texture;
	}

	public int getReflectionFrameBuffer() {
		return reflectionFrameBuffer;
	}

	public int getReflectionTexture() {
		return reflectionTexture;
	}

	public int getReflectionDepthBuffer() {
		return reflectionDepthBuffer;
	}

	public int getRefractionFrameBuffer() {
		return refractionFrameBuffer;
	}

	public int getRefractionTexture() {
		return refractionTexture;
	}

	public int getRefractionDepthBuffer() {
		return refractionDepthBuffer;
	}
}
