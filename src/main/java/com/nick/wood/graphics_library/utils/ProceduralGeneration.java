package com.nick.wood.graphics_library.utils;

import com.nick.wood.maths.noise.Perlin2D;

import java.util.function.Function;

public class ProceduralGeneration {

	public ProceduralGeneration() {
	}

	/**
	 *  @param randomNumberArraySize
	 * @param size
	 * @param octaves
	 * @param lacunarity controls increase in frequency of octaves (2)
	 * @param persistence controls decrease in amplitude of octaves (0.5)
	 * @param segmentSize
	 **/
	public double[][] generateHeightMapChunk(int randomNumberArraySize,
	                                         int size,
	                                         int octaves,
	                                         double lacunarity,
	                                         double persistence,
	                                         int segmentSize,
	                                         int startX,
	                                         int startY,
	                                         int amplitudeScale,
	                                         Function<Double, Double> amplitudeScalingFunction) {


		double[][] grid = new double[size][size];

		for (int octave = 0; octave < octaves; octave++) {

			double frequency = Math.pow(lacunarity, octave);
			double amplitude = Math.pow(persistence, octave);
			int currentSegmentSize = (int) (segmentSize / frequency);

			Perlin2D perlin2D = new Perlin2D(randomNumberArraySize, currentSegmentSize);

			for (int i = startX; i < size + startX; i++) {
				for (int j = startY; j < size + startY; j++) {
					grid[i - startX][j - startY] += perlin2D.getPoint(i, j) * amplitudeScalingFunction.apply(amplitude * amplitudeScale);
				}
			}
		}

		return grid;


	}

	/**
	 * @param size
	 * @param persistence controls decrease in amplitude of octaves (0.5)
	 **/
	public double[][] generateHeightMapChunk(
	                                         int size,
	                                         double persistence,
	                                         int startX,
	                                         int startY,
	                                         Perlin2D[] perlin2Ds,
	                                         int amplitudeScale,
	                                         Function<Double, Double> amplitudeScalingFunction) {

		double[][] grid = new double[size][size];

		for (int octave = 0; octave < perlin2Ds.length; octave++) {

			double amplitude = Math.pow(persistence, octave);

			for (int i = startX; i < size + startX; i++) {
				for (int j = startY; j < size + startY; j++) {
					grid[i - startX][j - startY] += perlin2Ds[octave].getPoint(Math.abs(i), Math.abs(j)) * amplitudeScalingFunction.apply(amplitude * amplitudeScale);
				}
			}
		}

		return grid;


	}

}
