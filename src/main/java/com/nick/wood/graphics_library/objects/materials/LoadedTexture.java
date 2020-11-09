package com.nick.wood.graphics_library.objects.materials;

import com.nick.wood.graphics_library.Window;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

public class LoadedTexture implements Texture {

	private final String texturePath;
	private final int parameter;
	private int id;

	public LoadedTexture(String path, int parameter) {
		this.texturePath = path;
		this.parameter = parameter;
	}

	/**
	 * Reads the specified resource and returns the raw data as a ByteBuffer.
	 *
	 * @param resource   the resource to read
	 * @param bufferSize the initial buffer size
	 *
	 * @return the resource data
	 *
	 * @throws IOException if an IO error occurs
	 */
	public ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize, InputStream resourceAsStream) throws IOException {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = createByteBuffer((int)fc.size() + 1);
				while (fc.read(buffer) != -1) {
					;
				}
			}
		} else {
			try (
					ReadableByteChannel rbc = Channels.newChannel(resourceAsStream)
			) {
				buffer = createByteBuffer(bufferSize);

				while (true) {
					int bytes = rbc.read(buffer);
					if (bytes == -1) {
						break;
					}
					if (buffer.remaining() == 0) {
						buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
					}
				}
			}
		}

		buffer.flip();
		return buffer;
	}

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

	public void create() throws IOException {
		String imagePath = texturePath.split("[.]")[1];

        InputStream resourceAsStream = getClass().getResourceAsStream(texturePath);

		if (resourceAsStream == null) {
			// try to find it in the user input folder via environment variable
			resourceAsStream = new FileInputStream(System.getenv("GRAPHICS_LIB_DATA") + "\\" + texturePath);
		}

		ByteBuffer imageData = ioResourceToByteBuffer(imagePath, 1024, resourceAsStream);

		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer components = stack.mallocInt(1);

			// Decode texture image into a byte buffer
			ByteBuffer decodedImage = stbi_load_from_memory(imageData, w, h, components, 4);

			// Create a new OpenGL texture
			this.id = glGenTextures();

			// Bind the texture
			glBindTexture(GL_TEXTURE_2D, this.id);

			// Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, parameter);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, parameter);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

			// Upload the texture data
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(), h.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, decodedImage);

			// Generate Mip Map
			glGenerateMipmap(GL_TEXTURE_2D);

			glBindTexture(GL_TEXTURE_2D, 0);

			MemoryUtil.memFree(decodedImage);
		}
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int textureId) {
		this.id = id;
	}

	public void destroy() {
		glBindTexture(GL_TEXTURE_2D, 0);
		glDeleteTextures(id);
	}
}
