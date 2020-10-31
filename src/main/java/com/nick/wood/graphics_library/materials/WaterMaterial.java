package com.nick.wood.graphics_library.materials;

import com.nick.wood.graphics_library.Shader;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.UUID;

public class WaterMaterial implements Material {

	private UUID materialID;
	private String texture;
	private String normalMap;
	private Vec3f diffuseColour;
	private Vec3f specularColour;
	private float shininess;
	private float reflectance;

	public WaterMaterial(UUID materialID, String texture, String normalMap, Vec3f diffuseColour, Vec3f specularColour, float shininess, float reflectance) {
		this.materialID = materialID;
		this.texture = texture;
		this.normalMap = normalMap;
		this.diffuseColour = diffuseColour;
		this.specularColour = specularColour;
		this.shininess = shininess;
		this.reflectance = reflectance;
	}

	public WaterMaterial(UUID materialID, String texture, String normalMap) {
		this(materialID, texture, normalMap, Vec3f.ONE, Vec3f.ONE, 1, 1);
	}

	@Override
	public void render(TextureManager textureManager, Shader shader) {
		// bind texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId("REFLECTION_TEXTURE"));
		shader.setUniform("reflectionTexture", 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId("REFRACTION_TEXTURE"));
		shader.setUniform("refractionTexture", 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(texture));
		shader.setUniform("dudvmap", 2);

		shader.setUniform("material.diffuse", diffuseColour);
		shader.setUniform("material.specular", specularColour);
		shader.setUniform("material.shininess", shininess);
		shader.setUniform("material.reflectance", reflectance);

		// bind normal map if available
		if (normalMap != null) {
			GL13.glActiveTexture(GL13.GL_TEXTURE3);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(normalMap));
			shader.setUniform("normal_text_sampler", 3);
			shader.setUniform("material.hasNormalMap", 1);
		} else {
			shader.setUniform("material.hasNormalMap", 0);
		}
	}
}
