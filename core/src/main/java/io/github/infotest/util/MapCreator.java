package io.github.infotest.util;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static io.github.infotest.MainGameScreen.*;

public class MapCreator {
    private final int seed;
    private final int stoneSeed;
    private final Perlin perlinClass;
    private final Random random;
    private final Random randomDeco;

    public MapCreator(int pSeed) {
        seed = pSeed;
        stoneSeed = pSeed+3;
        perlinClass = new Perlin();
        random = new Random(seed);
        randomDeco = new Random(stoneSeed);
    }

    public void initializePerlinNoiseMap(){
        // generate perlin noise based on seed (see Perlin Class)
        float[][] whiteNoise = Perlin.GenerateWhiteNoise(MAP_SIZE, MAP_SIZE, seed);
        float[][] perlinNoise = perlinClass.GeneratePerlinNoise(whiteNoise, 6);
        float[][] whiteNoiseStone = Perlin.GenerateWhiteNoise(MAP_SIZE, MAP_SIZE, stoneSeed);
        float[][] perlinNoiseStone = perlinClass.GeneratePerlinNoise(whiteNoiseStone, 4);


        // convert perlin noise to valid GAME_MAP
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                float perlinNoiseValue = perlinNoise[y][x];
                if (perlinNoiseValue>0.75){
                    GAME_MAP[y][x] = numOfValidTextures-1;
                } else if (perlinNoiseValue<0.75 && perlinNoiseValue>0.65){
                    GAME_MAP[y][x] = numOfValidTextures-2;
                } else if (perlinNoiseValue<0.65 && perlinNoiseValue>0.40){
                    GAME_MAP[y][x] = numOfValidTextures-5;
                } else {
                    GAME_MAP[y][x] = numOfValidTextures-6;
                }
                float perlinNoiseStoneValue = perlinNoiseStone[y][x];
                if (perlinNoiseStoneValue>0.65) {
                    if (GAME_MAP[y][x] == 4 || GAME_MAP[y][x] == 5) {
                        GAME_MAP[y][x] = numOfValidTextures-3;
                    }
                    if (GAME_MAP[y][x] == 0 || GAME_MAP[y][x] == 1) {
                        GAME_MAP[y][x] = numOfValidTextures-4;
                    }
                }
                if (perlinNoiseStoneValue<0.65 && perlinNoiseStoneValue>0.55 && GAME_MAP[y][x]==numOfValidTextures-4) {
                    GAME_MAP[y][x] = numOfValidTextures-5;
                }

                ROTATION_MAP[y][x] = (int)(random.nextFloat()*5);
            }
        }

        initFadeMap();
        initDecoMap();
//        for (int i = 0; i < DECO_MAP.length; i++) {
//            Logger.log(Arrays.toString(DECO_MAP[i]));
//        }

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

    private void initFadeMap(){
        for (int y2 = 0; y2 < MAP_SIZE; y2++) {
            for (int x2 = 0; x2 < MAP_SIZE; x2++) {
                String str = "";
                int thisCell = GAME_MAP[y2][x2];
                int topLeft;
                int top;
                int topRight;
                int right;
                int bottomRight;
                int bottom;
                int bottomLeft;
                int left;

                if (x2>0 && y2<MAP_SIZE-1) {topLeft = GAME_MAP[y2+1][x2-1];} else {topLeft = -1;}
                if (y2<MAP_SIZE-1) {top = GAME_MAP[y2+1][x2];} else {top = -1;}
                if (x2<MAP_SIZE-1 && y2<MAP_SIZE-1) {topRight = GAME_MAP[y2+1][x2+1];} else {topRight = -1;}
                if (x2<MAP_SIZE-1) {right = GAME_MAP[y2][x2+1];} else {right = -1;}
                if (x2<MAP_SIZE-1 && y2>0) {bottomRight = GAME_MAP[y2-1][x2+1];} else {bottomRight = -1;}
                if (y2>0) {bottom = GAME_MAP[y2-1][x2];} else {bottom = -1;}
                if (x2>0 && y2>0) {bottomLeft = GAME_MAP[y2-1][x2-1];} else {bottomLeft = -1;}
                if (x2>0) {left = GAME_MAP[y2][x2-1];} else {left = -1;}

                if (topLeft != thisCell && topLeft > 0) str+=topLeft+"c1"+";";
                if (top != thisCell && top > 0) str+=top+"t"+";";
                if (topRight != thisCell && topRight > 0) str+=topRight+"c2"+";";
                if (right != thisCell && right > 0) str+=right+"r"+";";
                if (bottomRight != thisCell && bottomRight > 0) str+=bottomRight+"c3"+";";
                if (bottom != thisCell && bottom > 0) str+=bottom+"b"+";";
                if (bottomLeft != thisCell && bottomLeft > 0) str+=bottomLeft+"c4"+";";
                if (left != thisCell && left > 0) str+=left+"l"+";";

                //Logger.log("x:"+x2+"; y:"+y2+"; str:"+str);
                str = "";

                FADE_MAP[y2][x2] = str;
            }
        }
    }
    private void initDecoMap(){
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                float r1 = random.nextFloat();
                if (r1 < 0.25) {
                    float r2 = random.nextFloat();
                    float p_old = 0;
                    for(int i = 0; i< numOfValidDeco; i++){
                        float p = p_old + DECO_PROB[GAME_MAP[y][x]][i];
                        if (p_old<r2 && r2<p) {
                            DECO_MAP[y][x] = i;
                            DECO_SCALE_MAP[y][x] = randomDeco.nextFloat()*0.5f+0.75f;
                            DECO_OFFSET_MAP[y][x] = new Vector2(randomDeco.nextFloat()*16, randomDeco.nextFloat()*16);
                        }
                        p_old = p;
                    }
                }
            }
        }
    }

    private boolean isIsolatedBlock(int x, int y) {
        int currentBlock = GAME_MAP[y][x];
        boolean hasSameNeighbor = false;

        // Check the 4 direct neighbors (up, down, left, right)
        if (x > 0 && GAME_MAP[y][x - 1] == currentBlock) hasSameNeighbor = true; // Left
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
        if (CheckEveryNeighbour(x2, y2, x1, y1, currentBlock)) return true;

        return false;
    }
    private boolean CheckEveryNeighbour(int x1, int y1, int x2, int y2, int currentBlock) {
        if (x1 > 0 && GAME_MAP[y1][x1 - 1] == currentBlock && !(x1 - 1 == x2 && y1 == y2)) return true;
        if (x1 < MAP_SIZE - 1 && GAME_MAP[y1][x1 + 1] == currentBlock && !(x1 + 1 == x2 && y1 == y2)) return true;
        if (y1 > 0 && GAME_MAP[y1 - 1][x1] == currentBlock && !(x1 == x2 && y1 - 1 == y2)) return true;
        if (y1 < MAP_SIZE - 1 && GAME_MAP[y1 + 1][x1] == currentBlock && !(x1 == x2 && y1 + 1 == y2)) return true;
        return false;
    }

    private int getRandomNeighbor(int x, int y) {
        List<Integer> neighbors = new ArrayList<>();

        // Collect all valid neighbors
        if (x > 0) neighbors.add( GAME_MAP[y][x - 1]); // Left
        if (x < MAP_SIZE - 1) neighbors.add( GAME_MAP[y][x + 1]); // Right
        if (y > 0) neighbors.add( GAME_MAP[y - 1][x]); // Up
        if (y < MAP_SIZE - 1) neighbors.add( GAME_MAP[y + 1][x]); // Down

        // Return a random neighbor
        return neighbors.get((int) (random.nextFloat() * neighbors.size()));
    }

}
