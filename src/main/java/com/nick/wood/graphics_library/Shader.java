package com.nick.wood.graphics_library;

import com.nick.wood.graphics_library.utils.FileUtils;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3d;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

	public final String vertexFile;
	public final String fragmentFile;
	private int vertexId;
	private int fragmentId;
	private int programId;

	public Shader(String vertexFile, String fragmentFile) {
		this.vertexFile = FileUtils.loadAsString(vertexFile);
		this.fragmentFile = FileUtils.loadAsString(fragmentFile);
	}

	public void create() {

		programId = glCreateProgram();


		// create shader
		vertexId = glCreateShader(GL_VERTEX_SHADER);
		createShader(vertexId, vertexFile, "Vertex");
		fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
		createShader(fragmentId, fragmentFile, "Fragment");

		glAttachShader(programId, vertexId);
		glAttachShader(programId, fragmentId);

		glLinkProgram(programId);

		if (glGetProgrami(programId, GL_LINK_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("Program linking error: " + glGetProgramInfoLog(programId));
		}

		glValidateProgram(programId);

		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("Program validate error: " + glGetProgramInfoLog(programId));
		}

		// program now does shading so delete shaders
		glDeleteShader(vertexId);
		glDeleteShader(fragmentId);
	}

	public int getUniformLocation(String name) {
		return glGetUniformLocation(programId, name);
	}

	public void setUniform(String name, Matrix4f value) {
		FloatBuffer matrix = MemoryUtil.memAllocFloat(Matrix4f.SIZE * Matrix4f.SIZE);
		matrix.put(value.getValues()).flip();
		glUniformMatrix4fv(getUniformLocation(name), true, matrix);
	}

	public void setUniform(String name, Vec3f vec) {
		glUniform3f(getUniformLocation(name), vec.getX(), vec.getY(), vec.getZ());
	}

	public void setUniform(String name, float v) {
		glUniform1f(getUniformLocation(name), v);
	}

	public void bind() {
		glUseProgram(programId);
	}
	public void unbind() {
		glUseProgram(0);
	}
	public void destroy() {

		glDetachShader(programId, vertexId);
		glDetachShader(programId, fragmentId);
		glDeleteShader(vertexId);
		glDeleteShader(fragmentId);
		glDeleteProgram(programId);
	}

	private void createShader(int id, String file, String typeString) {

		// set source code of shader
		glShaderSource(id, file);

		// compile shader
		glCompileShader(id);

		// error checking
		if (glGetShaderi(id, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException(typeString + " shader not loaded: " + glGetShaderInfoLog(id));
		}
	}

	public String getVertexFile() {
		return vertexFile;
	}

	public String getFragmentFile() {
		return fragmentFile;
	}
}
