package com.nick.wood.graphics_library.utils;

import com.nick.wood.graphics_library.objects.Transform;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshBuilder;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshObject;
import com.nick.wood.graphics_library.objects.mesh_objects.MeshType;
import com.nick.wood.graphics_library.objects.scene_graph_objects.MeshSceneGraph;
import com.nick.wood.graphics_library.objects.scene_graph_objects.SceneGraph;
import com.nick.wood.graphics_library.objects.scene_graph_objects.SceneGraphNode;
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

	private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	private final SceneGraph sceneGraph;

	private AtomicBoolean buildingActive = new AtomicBoolean(false);

	private final HashMap<UUID, SceneGraph> gameObjects;
	private final int chunkSize = 100;
	private final int segmentSize = 2000;
	private final ArrayList<ChunkIndex> activeChunkIndices = new ArrayList<>();
	private final ArrayList<ChunkIndex> loadedChunkIndices = new ArrayList<>();
	private final ConcurrentHashMap<ChunkIndex, MeshObject> chunkIndexSceneGraphHashMap = new ConcurrentHashMap<>();
	private final Perlin2Df[] perlin2Ds;

	ArrayList<ChunkIndex> newListOfChunkIndexes = new ArrayList<>();

	public ChunkLoader(HashMap<UUID, SceneGraph> gameObjects, int octaves, int lacunarity) {
		this.gameObjects = gameObjects;
		perlin2Ds = new Perlin2Df[octaves];
		for (int i = 0; i < octaves; i++) {
			double frequency = Math.pow(lacunarity, i);
			int currentSegmentSize = (int) (segmentSize / frequency);
			perlin2Ds[i] = new Perlin2Df(100000, currentSegmentSize);
		}

		this.sceneGraph = new SceneGraph();
		gameObjects.put(sceneGraph.getSceneGraphNodeData().getUuid(), sceneGraph);
	}

	public void loadChunk(Vec3f currentPlayerPosition) {

		if (!buildingActive.get()) {

			executorService.submit(() -> {

				try {

					buildingActive.set(true);

					newListOfChunkIndexes.clear();

					// work out what index the player would be in
					int xIndex = (int) (currentPlayerPosition.getX() / (double) chunkSize);
					int yIndex = (int) (currentPlayerPosition.getY() / (double) chunkSize);


					// load all 16 chunks around it
					for (int x = xIndex - 20; x <= xIndex + 20; x++) {
						for (int y = yIndex - 20; y <= yIndex + 20; y++) {

							ChunkIndex chunkIndex = new ChunkIndex(x, y);
							newListOfChunkIndexes.add(chunkIndex);

							// see if the chunk hasn't already been loaded
							if (!loadedChunkIndices.contains(chunkIndex)) {
								// add chunk to new list
								// and load it
								MeshObject meshObject = createChunk(x * chunkSize, y * chunkSize);
								chunkIndexSceneGraphHashMap.put(chunkIndex, meshObject);
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

		}
		// work out what index the player would be in
		int xIndex = (int) (currentPlayerPosition.getX() / (double) chunkSize);
		int yIndex = (int) (currentPlayerPosition.getY() / (double) chunkSize);

		// load all 8 chunks around it
		for (int x = xIndex - 15; x <= xIndex + 15; x++) {
			for (int y = yIndex - 15; y <= yIndex + 15; y++) {

				ChunkIndex chunkIndex = new ChunkIndex(x, y);

				// see if the chunk has been loaded
				if (loadedChunkIndices.contains(chunkIndex)) {

					// see if it isnt active already
					if (!activeChunkIndices.contains(chunkIndex)) {
						// add chunk to new list
						// then display it
						MeshObject meshObject = chunkIndexSceneGraphHashMap.get(chunkIndex);

						addToScene(meshObject, chunkIndex);

						activeChunkIndices.add(chunkIndex);

					}
				}
			}
		}

		// no go through and unload the chunks that shouldn't be active
		activeChunkIndices.removeIf(activeChunk -> {
			if (Math.abs(activeChunk.getX() - xIndex) > 15 || Math.abs(activeChunk.getY() - yIndex) > 15) {
				MeshObject meshObject = chunkIndexSceneGraphHashMap.get(activeChunk);
				removeFromScene(meshObject);

				return true;
			}
			return false;
		});


	}

	private void removeFromScene(MeshObject meshObject) {

		for (SceneGraphNode child : sceneGraph.getSceneGraphNodeData().getChildren()) {

			TransformSceneGraph transformSceneGraph = (TransformSceneGraph) child;

			for (SceneGraphNode sceneGraphNode : transformSceneGraph.getSceneGraphNodeData().getChildren()) {

				MeshSceneGraph meshSceneGraph = (MeshSceneGraph) sceneGraphNode;

				if (meshSceneGraph.getMeshObject().equals(meshObject)) {

					child.getSceneGraphNodeData().delete();

				}

			}

		}

	}

	private void addToScene(MeshObject meshObject, ChunkIndex chunkIndex) {

		Transform transform = new Transform(
				new Vec3f(chunkIndex.getX() * chunkSize, chunkIndex.getY() * chunkSize, 0),
				Vec3f.ONE,
				Matrix4f.Identity
		);

		TransformSceneGraph transformSceneGraph = new TransformSceneGraph(sceneGraph, transform);

		MeshSceneGraph meshSceneGraph = new MeshSceneGraph(transformSceneGraph, meshObject);

	}

	private MeshObject createChunk(float chunkPositionX, float chunkPositionY) {

		ProceduralGeneration proceduralGeneration = new ProceduralGeneration();
		float[][] grid = proceduralGeneration.generateHeightMapChunk(
				chunkSize + 1,
				0.7,
				(int) chunkPositionX,
				(int) chunkPositionY,
				perlin2Ds,
				200,
				amp -> (amp > 150) ? amp * 4 : amp
		);

		return new MeshBuilder()
				.setMeshType(MeshType.TERRAIN)
				.setTerrainHeightMap(grid)
				.setTexture("/textures/terrain.png")
				.build();
	}
}
