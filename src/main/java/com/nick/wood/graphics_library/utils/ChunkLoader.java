package com.nick.wood.graphics_library.utils;

import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.graphics_library.objects.scene_graph_objects.MeshSceneGraph;
import com.nick.wood.graphics_library.objects.scene_graph_objects.SceneGraph;
import com.nick.wood.graphics_library.objects.scene_graph_objects.TransformSceneGraph;
import com.nick.wood.maths.noise.Perlin2Df;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChunkLoader {

	private final ExecutorService executorService = Executors.newFixedThreadPool(4);

	private AtomicBoolean buildingActive = new AtomicBoolean(false);

	private final HashMap<UUID, SceneGraph> gameObjects;
	private final int chunkSize = 100;
	private final int segmentSize = 1000;
	private final ArrayList<ChunkIndex> activeChunkIndices = new ArrayList<>();
	private final ArrayList<ChunkIndex> loadedChunkIndices = new ArrayList<>();
	private final ConcurrentHashMap<ChunkIndex, SceneGraph> chunkIndexSceneGraphHashMap = new ConcurrentHashMap<>();
	private final Perlin2Df[] perlin2Ds;

	public ChunkLoader(HashMap<UUID, SceneGraph> gameObjects, int octaves, int lacunarity) {
		this.gameObjects = gameObjects;
		perlin2Ds = new Perlin2Df[octaves];
		for (int i = 0; i < octaves; i++) {
			double frequency = Math.pow(lacunarity, i);
			int currentSegmentSize = (int) (segmentSize / frequency);
			perlin2Ds[i] = new Perlin2Df(100000, currentSegmentSize);
		}
	}

	public void loadChunk(Vec3f currentPlayerPosition) {

		if (!buildingActive.get()) {

			executorService.submit(() -> {

				try {

					buildingActive.set(true);

					// work out what index the player would be in
					int xIndex = (int) (currentPlayerPosition.getX() / (double) chunkSize);
					int yIndex = (int) (currentPlayerPosition.getY() / (double) chunkSize);

					ArrayList<ChunkIndex> newListOfChunkIndexes = new ArrayList<>();

					// load all 16 chunks around it
					for (int x = xIndex - 20; x <= xIndex + 20; x++) {
						for (int y = yIndex - 20; y <= yIndex + 20; y++) {

							ChunkIndex chunkIndex = new ChunkIndex(x, y);
							newListOfChunkIndexes.add(chunkIndex);

							// see if the chunk hasn't already been loaded
							if (!loadedChunkIndices.contains(chunkIndex)) {
								// add chunk to new list
								// and load it
								SceneGraph chunkObject = createChunk(x * chunkSize, y * chunkSize);
								chunkIndexSceneGraphHashMap.put(chunkIndex, chunkObject);
								loadedChunkIndices.add(chunkIndex);

							}
						}
					}

					// no go through and unload the chunks that shouldnt be loaded
					loadedChunkIndices.removeIf(loadedChunk -> {
						if (!newListOfChunkIndexes.contains(loadedChunk)) {
							chunkIndexSceneGraphHashMap.remove(loadedChunk);
							return true;
						}
						return false;
					});

					buildingActive.set(false);

				} catch (Exception e) {
					e.printStackTrace();
				}

			});

			// work out what index the player would be in
			int xIndex = (int) (currentPlayerPosition.getX() / (double) chunkSize);
			int yIndex = (int) (currentPlayerPosition.getY() / (double) chunkSize);

			ArrayList<ChunkIndex> newListOfChunkIndexes = new ArrayList<>();

			// load all 8 chunks around it
			for (int x = xIndex - 10; x <= xIndex + 10; x++) {
				for (int y = yIndex - 10; y <= yIndex + 10; y++) {

					ChunkIndex chunkIndex = new ChunkIndex(x, y);

					newListOfChunkIndexes.add(chunkIndex);
					// see if the chunk has been loaded
					if (loadedChunkIndices.contains(chunkIndex)) {

						// see if it isnt active already
						if (!activeChunkIndices.contains(chunkIndex)) {
							// add chunk to new list
							// then display it
							SceneGraph sceneGraph = chunkIndexSceneGraphHashMap.get(chunkIndex);
							sceneGraph.getSceneGraphNodeData().undelete();
							activeChunkIndices.add(chunkIndex);
							gameObjects.put(sceneGraph.getSceneGraphNodeData().getUuid(), sceneGraph);

						}
					}
				}
			}

			// no go through and unload the chunks that shouldn't be active
			activeChunkIndices.removeIf(activeChunk -> {
				if (!newListOfChunkIndexes.contains(activeChunk)) {
					SceneGraph sceneGraph = chunkIndexSceneGraphHashMap.get(activeChunk);
					sceneGraph.getSceneGraphNodeData().remove();
					return true;
				}
				return false;
			});
		}

	}

	private SceneGraph createChunk(float chunkPositionX, float chunkPositionY) {

		ProceduralGeneration proceduralGeneration = new ProceduralGeneration();
		float[][] grid = proceduralGeneration.generateHeightMapChunk(
				chunkSize,
				0.7,
				(int) chunkPositionX,
				(int) chunkPositionY,
				perlin2Ds,
				7,
				amp -> amp * amp * amp
		);

		MeshObject terrain = new MeshBuilder()
				.setMeshType(MeshType.TERRAIN)
				.setTerrainHeightMap(grid)
				.setTexture("/textures/terrain.png")
				.build();

		SceneGraph sceneGraph = new SceneGraph();

		Transform transform = new Transform(
				new Vec3f(chunkPositionX, chunkPositionY, 0),
				Vec3f.ONE,
				Matrix4f.Identity
		);

		TransformSceneGraph transformSceneGraph = new TransformSceneGraph(sceneGraph, transform);

		MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, terrain);

		return sceneGraph;
	}
}
