package io.github.infotest.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.infotest.MainGameScreen;
import io.github.infotest.character.Gegner;
import io.github.infotest.character.Player;
import io.github.infotest.classes.Mage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class GameRenderer {

    private Texture[] textures;
    private final int[][] map;
    private final int cellSize;

    // Fireball animation-related fields
    private static ArrayList<FireballInstance> activeFireballs;
    private float fireballFrameDuration = 0.1f;

    private Animation<TextureRegion>[] fireballAnimations;

    private MainGameScreen game;


    public GameRenderer(Texture[] pTextures, int[][] map, int cellSize) {
        textures = pTextures;
        this.map = map;
        this.cellSize = cellSize;

        activeFireballs = new ArrayList<FireballInstance>();

    }

    public void initAnimations(Texture[] fireball_sheets){
        fireballAnimations = new Animation[fireball_sheets.length];
        int frameCols;
        int frameRows = 1;

        Texture fireball_sheet_start = fireball_sheets[0];
        Texture fireball_sheet_fly = fireball_sheets[1];
        Texture fireball_sheet_endTime = fireball_sheets[2];
        Texture fireball_sheet_endHit = fireball_sheets[3];

        // init fireball_sheet_start
        frameCols = 5;
        fireballAnimations[0] = sheetsToAnimation(frameCols, frameRows, fireball_sheet_start, fireballFrameDuration);
        fireballAnimations[0].setPlayMode(Animation.PlayMode.NORMAL);

        //init fireball_sheet_fly
        frameCols = 9;
        fireballAnimations[1] = sheetsToAnimation(frameCols, frameRows, fireball_sheet_fly, fireballFrameDuration);
        fireballAnimations[1].setPlayMode(Animation.PlayMode.LOOP);

        //init fireball_sheets_endTime
        frameCols = 8;
        fireballAnimations[2] = sheetsToAnimation(frameCols, frameRows, fireball_sheet_endTime, fireballFrameDuration);
        fireballAnimations[2].setPlayMode(Animation.PlayMode.NORMAL);

        //init fireball_sheets_endHit
        frameCols = 7;
        fireballAnimations[3] = sheetsToAnimation(frameCols, frameRows, fireball_sheet_endHit, fireballFrameDuration);
        fireballAnimations[3].setPlayMode(Animation.PlayMode.NORMAL);
    }

    private static Animation<TextureRegion> sheetsToAnimation(int frameCols, int frameRows, Texture fireball_sheet, float frameDuration) {
        TextureRegion[][] tempFrames2 = TextureRegion.split(fireball_sheet,
            fireball_sheet.getWidth() / frameCols,
            fireball_sheet.getHeight() / frameRows);

        TextureRegion[] temp_fireball_sheet = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                temp_fireball_sheet[index++] = tempFrames2[i][j];
            }
        }
        Animation<TextureRegion> fireballAnimation = new Animation<>(frameDuration, temp_fireball_sheet);
        return fireballAnimation;
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

    public void renderGegner(SpriteBatch batch, ArrayList<Gegner> allGegner, float deltaTime) {
        if (allGegner == null){
            System.out.println("players is null");
            return;
        }
        for (Gegner gegner : allGegner) {
            gegner.interpolatePosition(deltaTime);
            gegner.render(batch);
        }
    }

    float time = 0;
    public void renderAnimations(SpriteBatch batch, float deltaTime, ShapeRenderer shapeRenderer) {
        time += deltaTime;
        renderFireballs(batch, deltaTime, fireballAnimations, shapeRenderer);
    }

    public void renderBar(SpriteBatch batch, Camera cam, Texture[] bar, float maxValue, float y) {
        float endX = cam.viewportWidth - 100; // Position of the rightmost part of the bar
        float segmentWidth = 32; // Width of a single bar segment

        // Calculate number of middle segments based on value
        int numSegments = (int)(maxValue / 10);

        // Convert positions to world coordinates
        Vector3 endCoords = cam.unproject(new Vector3(endX, y, 0));
        Vector3 startCoords = cam.unproject(new Vector3(endX - segmentWidth * (numSegments + 1), y, 0));

        // Draw end of the bar
        batch.draw(bar[4], endCoords.x, endCoords.y);

        // Draw middle segments
        for (int i = 0; i < numSegments; i++) {
            Vector3 middleCoords = cam.unproject(new Vector3(endX - segmentWidth * (i + 1), y, 0));
            batch.draw(bar[2], middleCoords.x, middleCoords.y);
        }

        // Draw start of the bar
        batch.draw(bar[0], startCoords.x, startCoords.y);
    }

    //TODO fix fill bar

    public void fillBar(SpriteBatch batch, Camera cam, Texture[] bar, float value, float maxValue, float y) {
        float segmentWidth = bar[3].getWidth(); // Breite eines Mittelsegments
        float endX = cam.viewportWidth - 100 + 11; // Position des rechten Endes der Leiste

        // Berechnung der gefüllten Teile
        int fullSlots = (int)(value / 2)-2; // Ganze Teile, die in die Segmente passen
        float remainingWidth = (value % 2) * (segmentWidth / 2); // Breite eines halben Segments, falls nötig

        // Zeichne den End-Slot (bar[5])
        Vector3 endCoords = cam.unproject(new Vector3(endX, y, 0));
        batch.draw(bar[5], endCoords.x, endCoords.y);

        // Zeichne die mittleren Slots (bar[3])
        for (int i = 0; i < fullSlots; i++) {
            Vector3 middleCoords = cam.unproject(new Vector3(endX - segmentWidth * (i + 1), y, 0));
            batch.draw(bar[3], middleCoords.x, middleCoords.y);
        }

        // Zeichne den Start-Slot (bar[1])
        Vector3 startCoords = cam.unproject(new Vector3(endX - segmentWidth * (fullSlots + 1), y, 0));
        batch.draw(bar[1], startCoords.x, startCoords.y);

        // Zeichne den Rest, falls nur ein halbes Segment gefüllt ist
        if (remainingWidth > 0) {
            Vector3 partialCoords = cam.unproject(new Vector3(endX - segmentWidth * (fullSlots + 1), y, 0));
            batch.draw(bar[3], partialCoords.x, partialCoords.y, remainingWidth, segmentWidth);
        }
    }








    /// ANIMATIONS
    // Fireball
    public static void fireball(float pX, float pY, float velocityX, float velocityY, Vector2 rotation, float scale,float damage, float speed, float lt) {
        activeFireballs.add(new FireballInstance(pX, pY, velocityX, velocityY, rotation, scale, damage, speed, lt));
    }



    /// ANIMATION HELPER
    private void renderFireballs(SpriteBatch batch, float deltaTime, Animation<TextureRegion>[] fireballAnimations, ShapeRenderer shapeRenderer) {
        ArrayList<FireballInstance> toRemove = new ArrayList<>();

        Animation<TextureRegion> fireballAnimation_start = fireballAnimations[0];
        Animation<TextureRegion> fireballAnimation_fly = fireballAnimations[1];
        Animation<TextureRegion> fireballAnimation_endTime = fireballAnimations[2];
        Animation<TextureRegion> fireballAnimation_endHit = fireballAnimations[3];

        for (FireballInstance fireball : activeFireballs) {
            fireball.elapsedTime += deltaTime;
            fireball.updatePosition(deltaTime);
            float rotation = fireball.rotation.angleDeg();

            if (fireball.hasHit){
                fireball.endTimer += deltaTime;
                TextureRegion currentFrame = fireballAnimation_endHit.getKeyFrame(fireball.endTimer);
                drawFrame(batch, currentFrame, fireball, rotation);
                if (fireball.endTimer > fireballAnimation_endHit.getAnimationDuration()) {
                    toRemove.add(fireball);
                }
            } else if (fireball.elapsedTime <= fireballAnimation_start.getAnimationDuration()){
                TextureRegion currentFrame = fireballAnimation_start.getKeyFrame(fireball.elapsedTime);
                drawFrame(batch, currentFrame, fireball, rotation);
            } else if (fireball.elapsedTime < fireball.lt) {
                TextureRegion currentFrame = fireballAnimation_fly.getKeyFrame(fireball.elapsedTime-fireballAnimation_start.getAnimationDuration());
                drawFrame(batch, currentFrame, fireball, rotation);
            } else if (fireball.elapsedTime > fireball.lt) {
                TextureRegion currentFrame = fireballAnimation_endTime.getKeyFrame(fireball.endTimer);
                drawFrame(batch, currentFrame, fireball, rotation);
                fireball.endTimer += deltaTime;
                if (fireball.endTimer > fireballAnimation_endTime.getAnimationDuration()) {
                    toRemove.add(fireball);
                }
            }
        }
        activeFireballs.removeAll(toRemove);
    }

    private void drawFrame(SpriteBatch batch, TextureRegion currentFrame, FireballInstance fireball, float rotation) {
        float dX = fireball.x - 46f;
        float dY = fireball.y - 51f;
        batch.draw(
            currentFrame,
            dX,
            dY,
            64,
            64,
            currentFrame.getRegionWidth(),
            currentFrame.getRegionWidth(),
            fireball.scale,
            fireball.scale,
            rotation
        );
    }

    public static Vector2 worldPosToScreenPos(Vector2 worldCords, float screenWidth, float screenHeight, float fX, float fY) {
        float pX = worldCords.x;
        float pY = worldCords.y;

        float diffX = fX - pX;
        float diffY = fY - pY;

        return new Vector2(diffX + screenWidth/2, diffY + screenHeight/2);
    }

    public ArrayList<FireballInstance> getActiveFireballs() {
        return activeFireballs;
    }

    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
    }


    /// Helper class for tracking fireball instances
    public static class FireballInstance {
        private float x, y;
        float velocityX, velocityY;
        Vector2 rotation;
        float elapsedTime;
        float scale;
        float damage;
        float lt;
        float endTimer;
        boolean hasHit;

        private float speedFactor = 32f;

        FireballInstance(float x, float y, float velocityX, float velocityY, Vector2 rotation, float scale, float damage, float speed, float lt) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.rotation = rotation;
            this.elapsedTime = 0f;
            this.scale = scale;
            this.speedFactor = speedFactor * speed;
            this.damage = damage;
            this.lt = lt;
            this.endTimer = 0f;
            this.hasHit = false;
        }

        public void updatePosition(float deltaTime) {
            if (!hasHit){
                this.x += velocityX * deltaTime * speedFactor;
                this.y += velocityY * deltaTime * speedFactor;
            }
        }

        public float getX(){
            return x;
        }
        public float getY() {
            return y;
        }
        public float getDamage(){
            return damage;
        }
        public void setHit(){
            this.hasHit = true;
        }
        public boolean hasHit(){
            return hasHit;
        }
    }

}
