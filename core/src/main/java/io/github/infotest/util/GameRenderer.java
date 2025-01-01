package io.github.infotest.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class GameRenderer {

    private Texture[] textures;
    private final int[][] map;
    private final int cellSize;

    // Fireball animation-related fields
    private Animation<TextureRegion> fireballAnimation;
    private static ArrayList<FireballInstance> activeFireballs;
    private float fireballFrameDuration = 0.1f;


    public GameRenderer(Texture[] pTextures, int[][] map, int cellSize, Texture fireBallTexture) {
        textures = pTextures;
        this.map = map;
        this.cellSize = cellSize;

        // Initialize the fireball animation
        Texture fireballSheet = fireBallTexture;
        int frameCols = 29; // Number of columns in the animation sheet
        int frameRows = 1; // Number of rows in the animation sheet
        TextureRegion[][] tempFrames = TextureRegion.split(fireballSheet,
            fireballSheet.getWidth() / frameCols,
            fireballSheet.getHeight() / frameRows);

        TextureRegion[] fireballFrames = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                fireballFrames[index++] = tempFrames[i][j];
            }
        }

        fireballAnimation = new Animation<>(fireballFrameDuration, fireballFrames);
        fireballAnimation.setPlayMode(Animation.PlayMode.LOOP);

        activeFireballs = new ArrayList<FireballInstance>();

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

    public void renderPlayers(SpriteBatch batch, HashMap<String, Player> players, float deltaTime) {
        if (players == null){
            System.out.println("players is null");
            return;
        }
        for (Player player : players.values()) {
            player.interpolatePosition(deltaTime);
            player.render(batch);
        }
    }

    float time = 0;
    public void renderAnimations(SpriteBatch batch, float deltaTime, Texture texture) {
        time += deltaTime;
        renderFireballs(batch, deltaTime, texture);
    }





    /// ANIMATIONS
    // Fireball
    public static void fireball(float startX, float startY, float velocityX, float velocityY, Vector2 rotation) {
        activeFireballs.add(new FireballInstance(startX, startY, velocityX, velocityY, rotation));
    }



    /// ANIMATION HELPER
    private void renderFireballs(SpriteBatch batch, float deltaTime, Texture texture) {
        ArrayList<FireballInstance> toRemove = new ArrayList<>();

        for (FireballInstance fireball : activeFireballs) {
            fireball.elapsedTime += deltaTime;
            fireball.updatePosition(deltaTime);  // Bewege den Feuerball

            if (fireball.elapsedTime > fireballAnimation.getAnimationDuration()) {
                toRemove.add(fireball); // Entferne abgeschlossene Animationen
            } else {
                TextureRegion currentFrame = fireballAnimation.getKeyFrame(fireball.elapsedTime);

                float rotation = 0; //TODO

                batch.draw(
                    currentFrame,
                    fireball.x+1, fireball.y+1,
                    16,
                    16,
                    currentFrame.getRegionWidth(),
                    currentFrame.getRegionHeight(),
                    cellSize*8, cellSize*8,
                    rotation
                );
                System.out.print("Fireball drawn at "+fireball.x+", "+fireball.y);
            }
        }

        activeFireballs.removeAll(toRemove); // Entferne abgeschlossene Fireballs
    }











    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
    }





    /// Helper class for tracking fireball instances
    private static class FireballInstance {
        float x, y;
        float velocityX, velocityY;
        Vector2 rotation;
        float elapsedTime;

        FireballInstance(float x, float y, float velocityX, float velocityY, Vector2 rotation) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.rotation = rotation;
            this.elapsedTime = 0f;
        }

        public void updatePosition(float deltaTime) {
            this.x += velocityX * deltaTime;
            this.y += velocityY * deltaTime;
            System.out.println("Updated position: (" + this.x + ", " + this.y + ")");
        }
    }

}
