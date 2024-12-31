package io.github.infotest.util;

import io.github.infotest.MainGameScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapCreator {
    private int seed;
    private MainGameScreen game;
    private int[][] map;
    private int mapWidth;
    private Perlin perlinClass;
    private int numOfValidTextures;
    private Random rndm;

    public MapCreator(int pSeed, int width, MainGameScreen pGame, int pNumOfValidTextures) {
        seed = pSeed;
        mapWidth = width;
        map = new int[width][width];
        game = pGame;
        perlinClass = new Perlin();
        numOfValidTextures = pNumOfValidTextures;
        rndm = new Random(seed);
    }

    public int[][] initializePerlinNoiseMap(){
        // generate perlin noise based on seed (see Perlin Class)
        float[][] whiteNoise = perlinClass.GenerateWhiteNoise(mapWidth, mapWidth, seed);
        float[][] perlinNoise = perlinClass.GeneratePerlinNoise(whiteNoise, 5);

        // convert perlin noise to valid map
        for (int y = 0; y < mapWidth; y++) {
            for (int x = 0; x < mapWidth; x++) {
                map[y][x] = ((int) (perlinNoise[y][x]*(numOfValidTextures)));
            }
        }

        // Remove isolated blocks and pairs
        for (int y = 0; y < mapWidth; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (isIsolatedBlock(x, y)) {
                    map[y][x] = getRandomNeighbor(x, y);
                } else if (isIsolatedBlockPair(x, y)) {
                    map[y][x] = getRandomNeighbor(x, y);
                }
            }
        }
        return map;
    }

    // Check if a block is isolated (no similar neighbors)
    private boolean isIsolatedBlock(int x, int y) {
        int currentBlock = map[y][x];
        boolean hasSameNeighbor = false;

        // Check the 4 direct neighbors (up, down, left, right)
        if (x > 0 && map[y][x - 1] == currentBlock) hasSameNeighbor = true; // Left
        if (x < mapWidth - 1 && map[y][x + 1] == currentBlock) hasSameNeighbor = true; // Right
        if (y > 0 && map[y - 1][x] == currentBlock) hasSameNeighbor = true; // Up
        if (y < mapWidth - 1 && map[y + 1][x] == currentBlock) hasSameNeighbor = true; // Down

        return !hasSameNeighbor;
    }

    // Check if a block is part of an isolated pair
    private boolean isIsolatedBlockPair(int x, int y) {
        int currentBlock = map[y][x];

        // Check horizontal pairs
        if (x < mapWidth - 1 && map[y][x + 1] == currentBlock) {
            return !hasSameNeighborExceptPair(x, y, x + 1, y);
        }
        // Check vertical pairs
        if (y < mapWidth - 1 && map[y + 1][x] == currentBlock) {
            return !hasSameNeighborExceptPair(x, y, x, y + 1);
        }

        return false;
    }

    // Check for neighbors except the pair itself
    private boolean hasSameNeighborExceptPair(int x1, int y1, int x2, int y2) {
        int currentBlock = map[y1][x1];

        // Check neighbors of the first block
        if (x1 > 0 && map[y1][x1 - 1] == currentBlock && !(x1 - 1 == x2 && y1 == y2)) return true; // Left
        if (x1 < mapWidth - 1 && map[y1][x1 + 1] == currentBlock && !(x1 + 1 == x2 && y1 == y2)) return true; // Right
        if (y1 > 0 && map[y1 - 1][x1] == currentBlock && !(x1 == x2 && y1 - 1 == y2)) return true; // Up
        if (y1 < mapWidth - 1 && map[y1 + 1][x1] == currentBlock && !(x1 == x2 && y1 + 1 == y2)) return true; // Down

        // Check neighbors of the second block
        if (x2 > 0 && map[y2][x2 - 1] == currentBlock && !(x2 - 1 == x1 && y2 == y1)) return true; // Left
        if (x2 < mapWidth - 1 && map[y2][x2 + 1] == currentBlock && !(x2 + 1 == x1 && y2 == y1)) return true; // Right
        if (y2 > 0 && map[y2 - 1][x2] == currentBlock && !(x2 == x1 && y2 - 1 == y1)) return true; // Up
        if (y2 < mapWidth - 1 && map[y2 + 1][x2] == currentBlock && !(x2 == x1 && y2 + 1 == y1)) return true; // Down

        return false;
    }

    // Get a random block from one of the 4 neighbors
    private int getRandomNeighbor(int x, int y) {
        List<Integer> neighbors = new ArrayList<>();

        // Collect all valid neighbors
        if (x > 0) neighbors.add(map[y][x - 1]); // Left
        if (x < mapWidth - 1) neighbors.add(map[y][x + 1]); // Right
        if (y > 0) neighbors.add(map[y - 1][x]); // Up
        if (y < mapWidth - 1) neighbors.add(map[y + 1][x]); // Down

        // Return a random neighbor
        return neighbors.get((int) (rndm.nextFloat() * neighbors.size()));
    }




    public int[][] initializeRandomMap(){
        for (int y = 0; y < mapWidth; y++) {
            for (int x = 0; x < mapWidth; x++) {
                map[y][x] = (int) (Math.random() * 3); // 随机生成三种cell类型 // Erzeuge 3 zufällige Zellen
            }
        }
        return map;
    }
}
