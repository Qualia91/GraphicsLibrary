package com.nick.wood.graphics_library.materials;

import com.nick.wood.graphics_library.Shader;
import com.nick.wood.maths.objects.vector.Vec3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.ArrayList;
import java.util.UUID;

public class TerrainMaterial implements Material {

	private UUID materialID;
	private Vec3f diffuseColour;
	private Vec3f specularColour;
	private float shininess;
	private float reflectance;
	private final ArrayList<TerrainTextureObject> terrainTextureObjects;

	public TerrainMaterial(UUID materialID, Vec3f diffuseColour, Vec3f specularColour, float shininess, float reflectance, ArrayList<TerrainTextureObject> terrainTextureObjects) {
		this.materialID = materialID;
		this.diffuseColour = diffuseColour;
		this.specularColour = specularColour;
		this.shininess = shininess;
		this.reflectance = reflectance;
		this.terrainTextureObjects = terrainTextureObjects;
	}

	public TerrainMaterial(UUID materialID, ArrayList<TerrainTextureObject> terrainTextureObjects) {
		this(materialID, Vec3f.ONE, Vec3f.ONE, 1, 1, terrainTextureObjects);
	}

	@Override
	public void initRender(TextureManager textureManager, Shader shader) {
		for (int i = 0; i < terrainTextureObjects.size(); i++) {
			// bind texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(terrainTextureObjects.get(i).getTexturePath()));
			shader.setUniform("texture_array[" + i + "]", i);

			// bind normal
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + terrainTextureObjects.size() + i);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.getTextureId(terrainTextureObjects.get(i).getNormalPath()));
			shader.setUniform("normal_array[" + i + "]", terrainTextureObjects.size() + i);

			// bind heights
			shader.setUniform("heights[" + i + "]", terrainTextureObjects.get(i).getHeight());

			// bind transition width
			shader.setUniform("transitionWidths[" + i + "]", terrainTextureObjects.get(i).getTransitionWidth());
		}

		shader.setUniform("material.diffuse", diffuseColour);
		shader.setUniform("material.specular", specularColour);
		shader.setUniform("material.shininess", shininess);
		shader.setUniform("material.reflectance", reflectance);
		shader.setUniform("material.hasNormalMap", 1);

	}

	@Override
	public void endRender() {
		// do something
	}
}
