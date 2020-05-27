package com.nick.wood.graphics_library.utils;

import com.nick.wood.maths.noise.Perlin2D;

public class ProceduralGeneration {

	public ProceduralGeneration() {
	}

	/**
	 *  @param randomNumberArraySize
	 * @param size
	 * @param octaves
	 * @param lacunarity controls increase in frequency of octaves (2)
	 * @param persistence controls decrese in amplitude of octaves (0.5)
	 * @param segmentSize
	 **/
	public double[][] generateHeightMap(int randomNumberArraySize,
	                                    int size,
	                                    int octaves,
	                                    double lacunarity,
	                                    double persistence,
	                                    int segmentSize) {


		double[][] grid = new double[size][size];

		for (int octave = 0; octave < octaves; octave++) {

			double frequency = Math.pow(lacunarity, octave);
			double amplitude = Math.pow(persistence, octave);
			int currentSegmentSize = (int) (segmentSize / frequency);

			Perlin2D perlin2D = new Perlin2D(randomNumberArraySize, currentSegmentSize);

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					grid[i][j] += perlin2D.getPoint(i, j) * amplitude * currentSegmentSize;
				}
			}
		}

		return grid;


	}

	/**
	 *  @param randomNumberArraySize
	 * @param size
	 * @param octaves
	 * @param lacunarity controls increase in frequency of octaves (2)
	 * @param persistence controls decrese in amplitude of octaves (0.5)
	 * @param segmentSize
	 **/
	public double[][] generateNoiseCube(int randomNumberArraySize,
	                                    int size,
	                                    int octaves,
	                                    double lacunarity,
	                                    double persistence,
	                                    int segmentSize) {


		double[][] grid = new double[size][size];

		for (int octave = 0; octave < octaves; octave++) {

			double frequency = Math.pow(lacunarity, octave);
			double amplitude = Math.pow(persistence, octave);
			int currentSegmentSize = (int) (segmentSize / frequency);

			Perlin2D perlin2D = new Perlin2D(randomNumberArraySize, currentSegmentSize);

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					grid[i][j] += perlin2D.getPoint(i, j) * amplitude * currentSegmentSize;
				}
			}
		}

		return grid;


	}
}
