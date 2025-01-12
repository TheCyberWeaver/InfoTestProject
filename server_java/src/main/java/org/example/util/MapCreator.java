package org.example.util;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import org.example.character.NPC;

public class MapCreator {
    private int seed;
    private int[][] map;
    private int mapWidth;
    private Perlin perlinClass;
    private int numOfValidTextures;
    private Random rndm;

    public MapCreator(int pSeed, int width, int pNumOfValidTextures) {
        seed = 123;
        mapWidth = width;
        map = new int[width][width];
        perlinClass = new Perlin();
        numOfValidTextures = pNumOfValidTextures;
        rndm = new Random(seed);
    }

    public int[][] initializePerlinNoiseMap(){
        // generate perlin noise based on seed (see Perlin Class)
        float[][] whiteNoise = Perlin.GenerateWhiteNoise(mapWidth, mapWidth, seed);
        float[][] perlinNoise = perlinClass.GeneratePerlinNoise(whiteNoise, 6); //5

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
    /**
     * generate a bunch of NPCs on int[][] map
     * @return an ArrayList of NPCs
     */
    public ArrayList<NPC> spawnNPCs(){
        ArrayList<NPC> npcs = new ArrayList<>();
        //TODO:
        //Example:
        npcs.add(new NPC("Unknown",100,new Vector2(48000,48000),0,2));

        return npcs;
    }

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
    private boolean hasSameNeighborExceptPair(int x1, int y1, int x2, int y2) {
        int currentBlock = map[y1][x1];

        // Check neighbors of the first block
        if (CheckEveryNeighbour(x1, y1, x2, y2, currentBlock)) return true; // Down

        // Check neighbors of the second block
        if (CheckEveryNeighbour(x2, y2, x1, y1, currentBlock)) return true;

        return false;
    }
    private boolean CheckEveryNeighbour(int x1, int y1, int x2, int y2, int currentBlock) {
        if (x1 > 0 && map[y1][x1 - 1] == currentBlock && !(x1 - 1 == x2 && y1 == y2)) return true;
        if (x1 < mapWidth - 1 && map[y1][x1 + 1] == currentBlock && !(x1 + 1 == x2 && y1 == y2)) return true;
        if (y1 > 0 && map[y1 - 1][x1] == currentBlock && !(x1 == x2 && y1 - 1 == y2)) return true;
        if (y1 < mapWidth - 1 && map[y1 + 1][x1] == currentBlock && !(x1 == x2 && y1 + 1 == y2)) return true;
        return false;
    }

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

}
