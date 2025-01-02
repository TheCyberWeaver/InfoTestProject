package io.github.infotest.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private static ArrayList<FireballInstance> activeFireballs;
    private float fireballFrameDuration = 0.1f;

    private Animation<TextureRegion>[] fireballAnimations;


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

    float time = 0;
    public void renderAnimations(SpriteBatch batch, float deltaTime, ShapeRenderer shapeRenderer) {
        time += deltaTime;
        renderFireballs(batch, deltaTime, fireballAnimations, shapeRenderer);
    }

    //TODO: Scale and Rotation fix
    //TODO: Animation fix => erste Animation ist Fireball_fly und andere Animation Fireball_Destroy


    /// ANIMATIONS
    // Fireball
    public static void fireball(float pX, float pY, float velocityX, float velocityY, Vector2 rotation, float scale, float speed, float lt) {
        activeFireballs.add(new FireballInstance(pX, pY, velocityX, velocityY, rotation, scale, speed, lt));
        System.out.println(activeFireballs.size());
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

            if (fireball.elapsedTime <= fireballAnimation_start.getAnimationDuration()){
                TextureRegion currentFrame = fireballAnimation_start.getKeyFrame(fireball.elapsedTime);
                drawFrame(batch, currentFrame, fireball, rotation);
            } else if (fireball.elapsedTime < fireball.lt) {
                TextureRegion currentFrame = fireballAnimation_fly.getKeyFrame(fireball.elapsedTime-fireballAnimation_start.getAnimationDuration());
                drawFrame(batch, currentFrame, fireball, rotation);
            } else if (fireball.elapsedTime > fireball.lt) {
                TextureRegion currentFrame = fireballAnimation_endTime.getKeyFrame(fireball.endTimer);
                drawFrame(batch, currentFrame, fireball, rotation);
                fireball.endTimer += deltaTime;

                if (fireball.elapsedTime > fireball.lt + fireballAnimation_endTime.getAnimationDuration()) {
                    toRemove.add(fireball);
                }
            }
        }
        activeFireballs.removeAll(toRemove);
    }

    private void drawFrame(SpriteBatch batch, TextureRegion currentFrame, FireballInstance fireball, float rotation) {
        //float dX = fireball.x - (128 * fireball.scale) / 2 + 128*(fireball.scale/2 -0.5f)*fireball.scale+16-64*(float)(Math.pow(fireball.scale-1, 2));
        //float dY = fireball.y - (128 * fireball.scale) / 2 + 128*(fireball.scale/2 -0.5f)*fireball.scale+16-64*(float)(Math.pow(fireball.scale-1, 2));
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
        float lt;
        float endTimer;

        private float speedFactor = 32f;

        FireballInstance(float x, float y, float velocityX, float velocityY, Vector2 rotation, float scale, float speed, float lt) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.rotation = rotation;
            this.elapsedTime = 0f;
            this.scale = scale;
            this.speedFactor = speedFactor * speed;
            this.lt = lt;
            this.endTimer = 0f;
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
