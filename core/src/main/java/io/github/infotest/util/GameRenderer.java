package io.github.infotest.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Character;

import java.util.HashMap;
import java.util.Vector;


public class GameRenderer {

    private Texture[] textures;
    private final int[][] map;
    private final int cellSize;

    public GameRenderer(Texture[] pTextures, int[][] map, int cellSize) {
        textures = pTextures;
        this.map = map;
        this.cellSize = cellSize;
    }

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

    public void renderMap(SpriteBatch batch, float zoom, Vector2 pos) {
        int widthCell = (int) Math.ceil(Gdx.graphics.getWidth() * zoom / cellSize);
        int heightCell = (int) Math.ceil(Gdx.graphics.getHeight() * zoom / cellSize);

        int playerX = (int) (pos.x/cellSize);
        int playerY = (int) (pos.y/cellSize);

        for (int y =  -7; y < heightCell + 7; y++) {
            for (int x = -7; x < widthCell + 7; x++) {

                int worldX = playerX - widthCell/2 + x ;
                int worldY = playerY - heightCell/2 + y ;

                worldX = Math.max(0, Math.min(worldX, map[0].length - 1) );
                worldY = Math.max(0, Math.min(worldY, map.length - 1) );

                Texture cellTexture = textures[map[worldY][worldX]];
                batch.draw(cellTexture, worldX * cellSize, worldY * cellSize, cellSize, cellSize);
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
