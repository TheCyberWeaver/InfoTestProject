/**
 * The {@code Perlin} class provides methods for generating Perlin noise,
 * which is commonly used in computer graphics for procedural texture generation
 * and natural-looking randomness.
 *
 * <p>The class includes methods for generating white noise, smoothing it,
 * and combining it into Perlin noise across multiple octaves.</p>
 */
package io.github.infotest.util;

import java.util.Random;

public class Perlin {

    /**
     * Generates a 2D array of white noise values.
     *
     * @param width  the width of the noise array
     * @param height the height of the noise array
     * @return a 2D array of random float values between 0 and 1
     */
    public static float[][] GenerateWhiteNoise(int width, int height, long seed) {
        Random random = new Random(seed); // Random seed
        float[][] noise = new float[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                noise[i][j] = (float) (Math.random() % 1);
            }
        }

        return noise;
    }

    /**
     * Generates smooth noise by interpolating values from a base noise array.
     *
     * @param baseNoise the base noise array
     * @param octave    the level of detail (higher octaves result in finer details)
     * @return a 2D array of smoothed noise values
     */
    float[][] GenerateSmoothNoise(float[][] baseNoise, int octave) {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][] smoothNoise = new float[width][height];

        int samplePeriod = 1 << octave; // 2^octave
        float sampleFrequency = 1.0f / samplePeriod;

        for (int i = 0; i < width; i++) {
            int sample_i0 = (i / samplePeriod) * samplePeriod;
            int sample_i1 = (sample_i0 + samplePeriod) % width; // Wrap around
            float horizontal_blend = (i - sample_i0) * sampleFrequency;

            for (int j = 0; j < height; j++) {
                int sample_j0 = (j / samplePeriod) * samplePeriod;
                int sample_j1 = (sample_j0 + samplePeriod) % height; // Wrap around
                float vertical_blend = (j - sample_j0) * sampleFrequency;

                float top = Interpolate(baseNoise[sample_i0][sample_j0],
                    baseNoise[sample_i1][sample_j0], horizontal_blend);
                float bottom = Interpolate(baseNoise[sample_i0][sample_j1],
                    baseNoise[sample_i1][sample_j1], horizontal_blend);

                smoothNoise[i][j] = Interpolate(top, bottom, vertical_blend);
            }
        }

        return smoothNoise;
    }

    /**
     * Interpolates between two values based on a blend factor.
     *
     * @param x0    the first value
     * @param x1    the second value
     * @param alpha the blend factor (0.0 to 1.0)
     * @return the interpolated value
     */
    float Interpolate(float x0, float x1, float alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }

    /**
     * Generates Perlin noise by blending multiple octaves of smooth noise.
     *
     * @param baseNoise   the base noise array
     * @param octaveCount the number of octaves to generate
     * @return a 2D array of Perlin noise values
     */
    float[][] GeneratePerlinNoise(float[][] baseNoise, int octaveCount) {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][][] smoothNoise = new float[octaveCount][][]; // Array of smooth noise layers
        float persistence = 0.5f; // Determines the influence of each octave

        // Generate smooth noise for each octave
        for (int i = 0; i < octaveCount; i++) {
            smoothNoise[i] = GenerateSmoothNoise(baseNoise, i);
        }

        float[][] perlinNoise = new float[width][height];
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        // Blend smooth noise layers
        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= persistence;
            totalAmplitude += amplitude;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
                }
            }
        }

        // Normalize the result
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                perlinNoise[i][j] /= totalAmplitude;
            }
        }

        return perlinNoise;
    }
}
