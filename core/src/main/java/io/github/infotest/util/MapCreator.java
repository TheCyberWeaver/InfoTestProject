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
    Perlin perlinClass;

    public MapCreator(int pSeed, int width, MainGameScreen pGame) {
        seed = pSeed;
        mapWidth = width;
        map = new int[width][width];
        game = pGame;
        perlinClass = new Perlin();
    }

    public int[][] initializePerlinNoiseMap(){
        for (int y = 0; y < mapWidth; y++) {
            for (int x = 0; x < mapWidth; x++) {
                //map[y][x] = (int) (Math.random() * 3); // 随机生成三种cell类型 // Erzeuge 3 zufällige Zellen
                float[][] whiteNoise = perlinClass.GenerateWhiteNoise(mapWidth, mapWidth, seed);
                float[][] perlinNoise = perlinClass.GeneratePerlinNoise(whiteNoise, 8);



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
