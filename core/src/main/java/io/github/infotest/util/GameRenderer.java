package io.github.infotest.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Gegner;
import io.github.infotest.character.NPC;
import io.github.infotest.character.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static io.github.infotest.MainGameScreen.CELL_SIZE;
import static io.github.infotest.MainGameScreen.GAME_MAP;

public class GameRenderer {

    private final Texture[] textures;

    // Fireball animation-related fields
    private static ArrayList<FireballInstance> activeFireballs;
    private final float fireballFrameDuration = 0.1f;

    private Animation<TextureRegion>[] fireballAnimations;

    private final MyAssetManager assetManager;


    public GameRenderer(MyAssetManager assetManager) {
        this.assetManager = assetManager;
        this.textures=assetManager.getMapAssets();

        activeFireballs = new ArrayList<>();

    }

    public void initAnimations(){
        Texture[] fireball_sheets = assetManager.getFireballAssets();
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
        int widthCell = (int) Math.ceil(Gdx.graphics.getWidth() * zoom / CELL_SIZE);
        int heightCell = (int) Math.ceil(Gdx.graphics.getHeight() * zoom / CELL_SIZE);

        int playerX = (int) (pos.x/CELL_SIZE);
        int playerY = (int) (pos.y/CELL_SIZE);

        for (int y =  -7; y < heightCell + 7; y++) {
            for (int x = -7; x < widthCell + 7; x++) {

                int worldX = playerX - widthCell/2 + x ;
                int worldY = playerY - heightCell/2 + y ;

                worldX = Math.max(0, Math.min(worldX, GAME_MAP[0].length - 1) );
                worldY = Math.max(0, Math.min(worldY, GAME_MAP.length - 1) );

                Texture cellTexture = textures[GAME_MAP[worldY][worldX]];
                batch.draw(cellTexture, worldX * CELL_SIZE, worldY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

    }

    public void renderPlayers(SpriteBatch batch, HashMap<String, Player> players, float deltaTime) {
        if (players == null){
            Logger.log("players is null");
            return;
        }
        for (Player player : players.values()) {
            player.interpolatePosition(deltaTime);
            player.render(batch);
        }
    }

    public void renderGegner(SpriteBatch batch, ArrayList<Gegner> allGegner, float deltaTime) {
        if (allGegner == null){
            Logger.log("players is null");
            return;
        }
        for (Gegner gegner : allGegner) {
            gegner.interpolatePosition(deltaTime);
            gegner.render(batch);
        }
    }

    public void renderNPCs(SpriteBatch batch, ArrayList<NPC> allNPCs, float deltaTime) {
        if (allNPCs == null){
            Logger.log("NPCs is null");
            return;
        }
        for (NPC npc : allNPCs) {
            npc.render(batch);
        }
    }

    float time = 0;
    public void renderAnimations(SpriteBatch batch, float deltaTime, ShapeRenderer shapeRenderer) {
        time += deltaTime;
        renderFireballs(batch, deltaTime, fireballAnimations, shapeRenderer);
    }

    public static void renderBar(SpriteBatch batch, Texture[] bar, float value, float maxValue, float x, float y, float scaleX, float scaleY) {
        Sprite spriteEnd;
        Sprite spriteMiddle;
        Sprite spriteStart;

        if (value >= maxValue-9) {
            spriteEnd = new Sprite(bar[0]);
        } else {
            spriteEnd = new Sprite(bar[1]);
        }
        spriteEnd.flip(true, false);
        spriteEnd.setPosition(x, y);
        spriteEnd.setScale(scaleX,scaleY);
        spriteEnd.draw(batch);

        int segments = (int) Math.ceil(maxValue/10)-2;
        float tempMax = maxValue;
        for (int i=0;i<segments;i++){
            tempMax -= 10;
            if (value >= tempMax-9) {
                spriteMiddle = new Sprite(bar[2]);
            } else {
                spriteMiddle = new Sprite(bar[3]);
            }
            spriteMiddle.setScale(scaleX,scaleY);
            spriteMiddle.setPosition(x - (spriteMiddle.getWidth()* (i + 1f))*scaleX+(scaleX-1)*(-6), y);
            spriteMiddle.draw(batch);
        }
        spriteMiddle = new Sprite(bar[2]);

        if (value >= 1) {
            spriteStart = new Sprite(bar[0]);
        } else {
            spriteStart = new Sprite(bar[1]);
        }
        spriteStart.setPosition(x-segments*spriteMiddle.getWidth()*scaleX-scaleX*20, y);
        spriteStart.setScale(scaleX,scaleY);
        spriteStart.draw(batch);
    }

    private float fadeTimer = 0f;
    public boolean fadeTextureOut(Batch batch, float delta, Texture texture , float worldX, float worldY, float duration, float basis) {
        float alpha = MyMath.getExpValue(basis, duration, fadeTimer);
        if(Float.isNaN(alpha)){
            fadeTimer = 0f;
            return false;
        }
        batch.setColor(1, 1, 1, alpha);
        batch.draw(texture, worldX, worldY);
        fadeTimer += delta;
        batch.setColor(1, 1, 1, 1);
        return true;
    }



    /// ANIMATIONS
    // Fireball
    public static void fireball(float pX, float pY, float velocityX, float velocityY, Vector2 rotation, float scale,float damage, float speed, float lt, Player player) {
        activeFireballs.add(new FireballInstance(pX, pY, velocityX, velocityY, rotation, scale, damage, speed, lt, player));
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
        Player owner;

        private float speedFactor = 32f;

        FireballInstance(float x, float y, float velocityX, float velocityY, Vector2 rotation, float scale, float damage, float speed, float lt, Player player) {
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
            this.owner = player;
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
        public Player getOwner(){
            return owner;
        }
    }

}
