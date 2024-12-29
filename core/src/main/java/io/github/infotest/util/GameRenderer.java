package io.github.infotest.util;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.infotest.character.Character;

import java.util.HashMap;


public class GameRenderer {

    private final Texture normalBlock;
    private final Texture grassBlock;
    private final Texture rockBlock;

    private final int[][] map;
    private final int cellSize;

    public GameRenderer(Texture normalBlock, Texture grassBlock, Texture rockBlock,
                        int[][] map, int cellSize) {
        this.normalBlock = normalBlock;
        this.grassBlock = grassBlock;
        this.rockBlock = rockBlock;
        this.map = map;
        this.cellSize = cellSize;
    }

    //TODO not gernerate whole map
    public void renderMap(SpriteBatch batch) {
        if (map == null) return;
        int rows = map.length;
        int columns = map[0].length;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                Texture cellTexture;
                switch (map[y][x]) {
                    case 0:
                        cellTexture = normalBlock;
                        break;
                    case 1:
                        cellTexture = grassBlock;
                        break;
                    case 2:
                        cellTexture = rockBlock;
                        break;
                    default:
                        cellTexture = normalBlock;
                }
                batch.draw(cellTexture, x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }


    public void renderPlayers(SpriteBatch batch, HashMap<String, Character> players,float deltaTime) {
        if (players == null){
            System.out.println("players is null");
            return;
        }
        for (Character player : players.values()) {
            player.interpolatePosition(deltaTime);
            player.render(batch);
        }
    }



    public void dispose() {
        normalBlock.dispose();
        grassBlock.dispose();
        rockBlock.dispose();
    }
}
