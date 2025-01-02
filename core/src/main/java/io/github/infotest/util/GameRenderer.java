package io.github.infotest.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.classes.Mage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

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
        int frameCols = 29; // Number of columns in the animation sheet
        int frameRows = 1; // Number of rows in the animation sheet
        TextureRegion[][] tempFrames = TextureRegion.split(fireBallTexture,
            fireBallTexture.getWidth() / frameCols,
            fireBallTexture.getHeight() / frameRows);

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
    public void renderAnimations(SpriteBatch batch, float deltaTime, Camera camera) {
        time += deltaTime;
        renderFireballs(batch, deltaTime, camera);
    }





    /// ANIMATIONS
    // Fireball
    public static void fireball(float screenX, float screenY, float velocityX, float velocityY, Vector2 rotation, float scale, float speed) {
        System.out.println("Added Fireball to list");
        activeFireballs.add(new FireballInstance(screenX, screenY, velocityX, velocityY, rotation, scale, speed));
    }



    /// ANIMATION HELPER
    private void renderFireballs(SpriteBatch batch, float deltaTime, Camera camera) {
        ArrayList<FireballInstance> toRemove = new ArrayList<>();

        for (FireballInstance fireball : activeFireballs) {
            fireball.elapsedTime += deltaTime;
            fireball.updatePosition(deltaTime);


            if (fireball.elapsedTime > fireballAnimation.getAnimationDuration()) {
                toRemove.add(fireball); // Entferne abgeschlossene Animationen
                time = 0;
            } else {
                TextureRegion currentFrame = fireballAnimation.getKeyFrame(fireball.elapsedTime);

                float rotation = fireball.rotation.angleDeg();

                batch.draw(
                    currentFrame,
                    fireball.x - (310 * fireball.scale) / 2,
                    fireball.y - (128 * fireball.scale) / 2,
                    (345 * fireball.scale) / 2,
                    (158 * fireball.scale) / 2,
                    256,
                    256,
                    fireball.scale,
                    fireball.scale,
                    rotation
                );
            }
        }

        activeFireballs.removeAll(toRemove); // Entferne abgeschlossene Fireballs
    }

    public static Vector2 worldPosToScreenPos(Player player, float screenWidth, float screenHeight, float fX, float fY) {
        float pX = player.getX();
        float pY = player.getY();

        float diffX = fX - pX;
        float diffY = fY - pY;

        return new Vector2(diffX + screenWidth/2, diffY + screenHeight/2);
    }




    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
    }


    /// Helper class for tracking fireball instances
    private static class FireballInstance {
        private float x, y;
        float velocityX, velocityY;
        Vector2 rotation;
        float elapsedTime;
        float scale;

        private float speedFactor = 32f;

        FireballInstance(float x, float y, float velocityX, float velocityY, Vector2 rotation, float scale, float speed) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.rotation = rotation;
            this.elapsedTime = 0f;
            this.scale = scale;
            this.speedFactor = speedFactor * speed;
        }

        public void updatePosition(float deltaTime) {
            this.x += velocityX * deltaTime * speedFactor;
            this.y += velocityY * deltaTime * speedFactor;
        }

        public float getX(){
            return x - 155;
        }
        public float getY() {
            return y - 65;
        }
    }

}
