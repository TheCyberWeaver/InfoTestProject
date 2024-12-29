package io.github.infotest.util;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.infotest.character.Character;

import java.util.HashMap;


public class GameRenderer {

    private Texture[] textures;
    private final int[][] map;
    private final int cellSize;

    public GameRenderer(Texture[] textures, int[][] map, int cellSize) {
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
                Texture cellTexture = textures[map[y][x]];
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
        for (Texture texture : textures) {
            texture.dispose();
        }
    }
}
