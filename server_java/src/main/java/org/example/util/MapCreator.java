package org.example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.example.GameSocketServer.*;
import org.example.character.NPC;

public class MapCreator {
    private final int seed;
    private final Perlin perlinClass;
    private final Random rndm;

    public MapCreator(int pSeed) {
        seed = pSeed;
        perlinClass = new Perlin();
        rndm = new Random(seed);

    }

    public void initializePerlinNoiseMap(){
        // generate perlin noise based on seed (see Perlin Class)
        float[][] whiteNoise = Perlin.GenerateWhiteNoise(MAP_SIZE, MAP_SIZE, seed);
        float[][] perlinNoise = perlinClass.GeneratePerlinNoise(whiteNoise, 6); //5

        // convert perlin noise to valid GAME_MAP
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                GAME_MAP[y][x] = ((int) (perlinNoise[y][x]*(numOfValidTextures)));
            }
        }

        // Remove isolated blocks and pairs
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                if (isIsolatedBlock(x, y)) {
                    GAME_MAP[y][x] = getRandomNeighbor(x, y);
                } else if (isIsolatedBlockPair(x, y)) {
                    GAME_MAP[y][x] = getRandomNeighbor(x, y);
                }
            }
        }
    }
    /**
     * generate a bunch of NPCs on int[][] map
     * @return an ArrayList of NPCs
     */
    public ArrayList<NPC> spawnNPCs(){
        ArrayList<NPC> npcs = new ArrayList<>();
        //TODO: @alxmorozova
        //Example:
        int centralPointX = MAP_SIZE *CELL_SIZE / 2; // centralPointX=48000
        int centralPointY = MAP_SIZE *CELL_SIZE / 2; // centralPointY=48000
        npcs.add(new NPC("Unknown",100,new Vector2(centralPointX,centralPointY),0,2,0));


        return npcs;
    }

    private boolean isIsolatedBlock(int x, int y) {
        int currentBlock = GAME_MAP[y][x];
        boolean hasSameNeighbor = x > 0 && GAME_MAP[y][x - 1] == currentBlock;

        // Check the 4 direct neighbors (up, down, left, right)
        // Left
        if (x < MAP_SIZE - 1 && GAME_MAP[y][x + 1] == currentBlock) hasSameNeighbor = true; // Right
        if (y > 0 && GAME_MAP[y - 1][x] == currentBlock) hasSameNeighbor = true; // Up
        if (y < MAP_SIZE - 1 && GAME_MAP[y + 1][x] == currentBlock) hasSameNeighbor = true; // Down

        return !hasSameNeighbor;
    }
    private boolean isIsolatedBlockPair(int x, int y) {
        int currentBlock = GAME_MAP[y][x];

        // Check horizontal pairs
        if (x < MAP_SIZE - 1 && GAME_MAP[y][x + 1] == currentBlock) {
            return !hasSameNeighborExceptPair(x, y, x + 1, y);
        }
        // Check vertical pairs
        if (y < MAP_SIZE - 1 && GAME_MAP[y + 1][x] == currentBlock) {
            return !hasSameNeighborExceptPair(x, y, x, y + 1);
        }

        return false;
    }
    private boolean hasSameNeighborExceptPair(int x1, int y1, int x2, int y2) {
        int currentBlock = GAME_MAP[y1][x1];

        // Check neighbors of the first block
        if (CheckEveryNeighbour(x1, y1, x2, y2, currentBlock)) return true; // Down

        // Check neighbors of the second block
        return CheckEveryNeighbour(x2, y2, x1, y1, currentBlock);
    }
    private boolean CheckEveryNeighbour(int x1, int y1, int x2, int y2, int currentBlock) {
        if (x1 > 0 && GAME_MAP[y1][x1 - 1] == currentBlock && !(x1 - 1 == x2 && y1 == y2)) return true;
        if (x1 < MAP_SIZE - 1 && GAME_MAP[y1][x1 + 1] == currentBlock && !(x1 + 1 == x2 && y1 == y2)) return true;
        if (y1 > 0 && GAME_MAP[y1 - 1][x1] == currentBlock && !(x1 == x2 && y1 - 1 == y2)) return true;
        return y1 < MAP_SIZE - 1 && GAME_MAP[y1 + 1][x1] == currentBlock && !(x1 == x2 && y1 + 1 == y2);
    }

    private int getRandomNeighbor(int x, int y) {
        List<Integer> neighbors = new ArrayList<>();

        // Collect all valid neighbors
        if (x > 0) neighbors.add( GAME_MAP[y][x - 1]); // Left
        if (x < MAP_SIZE - 1) neighbors.add( GAME_MAP[y][x + 1]); // Right
        if (y > 0) neighbors.add( GAME_MAP[y - 1][x]); // Up
        if (y < MAP_SIZE - 1) neighbors.add( GAME_MAP[y + 1][x]); // Down

        // Return a random neighbor
        return neighbors.get((int) (rndm.nextFloat() * neighbors.size()));
    }

}
