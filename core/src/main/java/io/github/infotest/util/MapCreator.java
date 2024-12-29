package io.github.infotest.util;

import io.github.infotest.MainGameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;

public class MapCreator {
    private int seed;
    private MainGameScreen game;
    private int[][] map;
    private int mapWidth;
    private Perlin perlinClass;
    private int numOfValidTextures;

    public MapCreator(int pSeed, int width, MainGameScreen pGame, int pNumOfValidTextures) {
        seed = 98272654;
        mapWidth = width;
        map = new int[width][width];
        game = pGame;
        perlinClass = new Perlin();
        numOfValidTextures = pNumOfValidTextures;
    }

    public int[][] initializePerlinNoiseMap(){
        // generate perlin noise based on seed (see Perlin Class)
        float[][] whiteNoise = perlinClass.GenerateWhiteNoise(mapWidth, mapWidth, seed);
        float[][] perlinNoise = perlinClass.GeneratePerlinNoise(whiteNoise, 8);

        // convert perlin noise to valid map
        for (int y = 0; y < mapWidth; y++) {
            for (int x = 0; x < mapWidth; x++) {
                map[y][x] = ((int) (perlinNoise[y][x]*(numOfValidTextures)));
            }
        }
        return map;
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
