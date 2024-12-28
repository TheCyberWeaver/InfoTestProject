package io.github.infotest.util;

import io.github.infotest.MainGameScreen;

public class MapCreator {
    private int seed;
    private MainGameScreen game;
    private int[][] map;
    private int width, height;

    public MapCreator(int seed, int height,int width) {
        this.seed = seed;
        this.width = width;
        this.height = height;
        map = new int[height][width];
    }

    public int[][] initializeRandomMap(){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = (int) (Math.random() * 3); // 随机生成三种cell类型
            }
        }
        return map;
    }
}
