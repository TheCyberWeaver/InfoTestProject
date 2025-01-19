package io.github.infotest.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.infotest.GameSettings;
import io.github.infotest.MainGameScreen;
import io.github.infotest.character.Gegner;
import io.github.infotest.character.NPC;
import io.github.infotest.character.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static io.github.infotest.MainGameScreen.*;

public class GameRenderer {

    private final Texture[] textures;
    private final Texture[] fadeTexture;
    private final Texture[] decoTexture;
    private final Texture[] treeTexture;

    // Fireball animation-related fields
    private static ArrayList<AbilityInstance> activeFireballs;
    private static HashMap<Player, Vector3> activeArrows;
    private final float fireballFrameDuration = 0.1f;

    private Animation<TextureRegion>[] fireballAnimations;

    private final MainGameScreen mainGameScreen;
    private final MyAssetManager assetManager;

    private ShaderProgram nightEffectShader;


    public GameRenderer(MainGameScreen mainGameScreen, MyAssetManager assetManager) {
        this.mainGameScreen = mainGameScreen;
        this.assetManager = assetManager;
        this.textures = assetManager.getMapAssets();
        this.fadeTexture = assetManager.getMapFadeAssets();
        this.decoTexture = assetManager.getMapDecoAssets();
        this.treeTexture = assetManager.getMapTreeAssets();

        activeFireballs = new ArrayList<>();
        activeArrows = new HashMap<Player, Vector3>();

    }

    public void initAnimations() {
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

    public static Animation<TextureRegion> sheetsToAnimation(int frameCols, int frameRows, Texture fireball_sheet, float frameDuration) {
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
        return new Animation<>(frameDuration, temp_fireball_sheet);
    }


    public void renderMap(SpriteBatch batch, float zoom, Vector2 pos) {
        // Define fade textures for different tiles
        Texture[] tile1FADE = new Texture[5];
        System.arraycopy(fadeTexture, 0, tile1FADE, 0, 5);

        Texture[] tile2FADE = new Texture[5];
        System.arraycopy(fadeTexture, 5, tile2FADE, 0, 5);

        Texture[] tile4FADE = new Texture[5];
        System.arraycopy(fadeTexture, 10, tile4FADE, 0, 5);

        Texture[] tile5FADE = new Texture[5];
        System.arraycopy(fadeTexture, 15, tile5FADE, 0, 5);

        // Calculate visible tiles
        int widthCell = (int) Math.ceil(Gdx.graphics.getWidth() * zoom / CELL_SIZE);
        int heightCell = (int) Math.ceil(Gdx.graphics.getHeight() * zoom / CELL_SIZE);

        int playerX = (int) (pos.x / CELL_SIZE);
        int playerY = (int) (pos.y / CELL_SIZE);

        for (int y = -7; y < heightCell + 7; y++) {
            for (int x = -7; x < widthCell + 7; x++) {

                int worldX = playerX - widthCell / 2 + x;
                int worldY = playerY - heightCell / 2 + y;

                // Clamp coordinates to map bounds
                worldX = Math.max(0, Math.min(worldX, GAME_MAP[0].length - 1));
                worldY = Math.max(0, Math.min(worldY, GAME_MAP.length - 1));

                int rotation = 90 * ROTATION_MAP[worldY][worldX];
                Sprite cellTexture = new Sprite(textures[GAME_MAP[worldY][worldX]]);
                cellTexture.setPosition(worldX * CELL_SIZE, worldY * CELL_SIZE);
                cellTexture.setRotation(rotation);
                cellTexture.setRegionWidth(CELL_SIZE);
                cellTexture.setRegionHeight(CELL_SIZE);
                cellTexture.draw(batch);

                // Render high graphics (fade transitions)
                if (GameSettings.highGrafik) {
                    String str = FADE_MAP[worldY][worldX];
                    int topLeft = -1, top = -1, topRight = -1, right = -1;
                    int bottomRight = -1, bottom = -1, bottomLeft = -1, left = -1;

                    // Parse fade map string
                    int altIndex = 0;
                    for (int i = 0; i < 8; i++) {
                        int index = str.indexOf(';', altIndex);
                        if (index == -1) break;

                        String s = str.substring(altIndex, index);
                        altIndex = index + 1;

                        if (s.isEmpty()) continue;

                        int tileID = Integer.parseInt(String.valueOf(s.charAt(0)));
                        if (s.contains("c")) {
                            switch (Integer.parseInt(String.valueOf(s.charAt(s.length() - 1)))) {
                                case 1:
                                    topLeft = tileID;
                                    break;
                                case 2:
                                    topRight = tileID;
                                    break;
                                case 3:
                                    bottomRight = tileID;
                                    break;
                                case 4:
                                    bottomLeft = tileID;
                                    break;
                            }
                        }
                        if (s.contains("t")) top = tileID;
                        if (s.contains("r")) right = tileID;
                        if (s.contains("b")) bottom = tileID;
                        if (s.contains("l")) left = tileID;
                    }

                    // Render transitions
                    drawTransition(batch, worldX, worldY, tile1FADE, tile2FADE, tile4FADE, tile5FADE, topLeft, 180);
                    drawTransition(batch, worldX, worldY, tile1FADE, tile2FADE, tile4FADE, tile5FADE, top, 0);
                    drawTransition(batch, worldX, worldY, tile1FADE, tile2FADE, tile4FADE, tile5FADE, topRight, 90);
                    drawTransition(batch, worldX, worldY, tile1FADE, tile2FADE, tile4FADE, tile5FADE, right, 0);
                    drawTransition(batch, worldX, worldY, tile1FADE, tile2FADE, tile4FADE, tile5FADE, bottomRight, 0);
                    drawTransition(batch, worldX, worldY, tile1FADE, tile2FADE, tile4FADE, tile5FADE, bottom, 0);
                    drawTransition(batch, worldX, worldY, tile1FADE, tile2FADE, tile4FADE, tile5FADE, bottomLeft, -90);
                    drawTransition(batch, worldX, worldY, tile1FADE, tile2FADE, tile4FADE, tile5FADE, left, 0);
                }
            }
        }
        if (GameSettings.highGrafik) {
            for (int y = -7; y < heightCell + 7; y++) {
                for (int x = -7; x < widthCell + 7; x++) {

                    int worldX = playerX - widthCell / 2 + x;
                    int worldY = playerY - heightCell / 2 + y;

                    // Clamp coordinates to map bounds
                    worldX = Math.max(0, Math.min(worldX, GAME_MAP[0].length - 1));
                    worldY = Math.max(0, Math.min(worldY, GAME_MAP.length - 1));

                    int decoValue = DECO_MAP[worldY][worldX];
                    if (decoValue > 0 && decoValue < decoTexture.length) {
                        float decoX = worldX * CELL_SIZE;
                        float decoY = worldY * CELL_SIZE;

                        // Adjust position for larger decoration
                        if (decoValue == 10) {
                            decoX -= CELL_SIZE/2.0f; // Offset for larger decoration
                        }

                        Vector2 offset = DECO_OFFSET_MAP[worldY][worldX];
                        float offsetX = offset.x;
                        float offsetY = offset.y;

                        float scale = DECO_SCALE_MAP[worldY][worldX];

                        batch.draw(new TextureRegion(decoTexture[decoValue]),
                            decoX+offsetX, decoY+offsetY, 0, 0,
                            CELL_SIZE * scale, CELL_SIZE * scale, scale, scale, 0);
                    }
                }
            }
        }
    }

    private void drawTransition(SpriteBatch batch, int worldX, int worldY, Texture[] tile1FADE, Texture[] tile2FADE, Texture[] tile4FADE, Texture[] tile5FADE, int tileType, float rotation) {
        Sprite transitionSprite = null;

        switch (tileType) {
            case 1:
                transitionSprite = new Sprite(tile1FADE[0]);
                break;
            case 2:
                transitionSprite = new Sprite(tile2FADE[0]);
                break;
            case 4:
                transitionSprite = new Sprite(tile4FADE[0]);
                break;
            case 5:
                transitionSprite = new Sprite(tile5FADE[0]);
                break;
        }

        if (transitionSprite != null) {
            transitionSprite.setOrigin(transitionSprite.getWidth() / 2, transitionSprite.getHeight() / 2);
            transitionSprite.setRotation(rotation);
            transitionSprite.setPosition(worldX * CELL_SIZE, worldY * CELL_SIZE);
            transitionSprite.draw(batch);
        }
    }

    public void renderTrees(Batch batch, float zoom, Vector2 pos){
        int widthCell = (int) Math.ceil(Gdx.graphics.getWidth() * zoom / CELL_SIZE);
        int heightCell = (int) Math.ceil(Gdx.graphics.getHeight() * zoom / CELL_SIZE);

        int playerX = (int) (pos.x / CELL_SIZE);
        int playerY = (int) (pos.y / CELL_SIZE);
        for (int y = -7; y < heightCell + 7; y++) {
            for (int x = -7; x < widthCell + 7; x++) {
                int worldX = playerX - widthCell / 2 + x;
                int worldY = playerY - heightCell / 2 + y;

                // Clamp coordinates to map bounds
                worldX = Math.max(0, Math.min(worldX, GAME_MAP[0].length - 1));
                worldY = Math.max(0, Math.min(worldY, GAME_MAP.length - 1));


                if (worldY > 0) {
                    if (GAME_MAP[worldY-1][worldX] == 2){
                        batch.draw(treeTexture[2], worldX*32, worldY*32);
                    }
                }
                if (GAME_MAP[worldY][worldX] == 2) {
                    if (worldY > 0 && GAME_MAP[worldY-1][worldX] == 2) {
                        batch.draw(treeTexture[1], worldX * 32, worldY * 32);
                    } else if (worldY == 0) {
                        batch.draw(treeTexture[0], worldX * 32, 0);
                    } else if (worldY == MAP_SIZE) {
                        batch.draw(treeTexture[2], worldX * 32, (worldY + 1) * 32);
                    } else {
                        batch.draw(treeTexture[0], worldX * 32, worldY * 32);
                    }
                }

                if (worldY > 0) {
                    if (GAME_MAP[worldY-1][worldX] == 3){
                        batch.draw(treeTexture[5], worldX*32, worldY*32);
                    }
                }
                if (GAME_MAP[worldY][worldX] == 3) {
                    if (worldY > 0 && GAME_MAP[worldY-1][worldX] == 3) {
                        batch.draw(treeTexture[4], worldX * 32, worldY * 32);
                    } else if (worldY == 0) {
                        batch.draw(treeTexture[3], worldX * 32, 0);
                    } else if (worldY == MAP_SIZE) {
                        batch.draw(treeTexture[5], worldX * 32, (worldY + 1) * 32);
                    } else {
                        batch.draw(treeTexture[3], worldX * 32, worldY * 32);
                    }
                }
            }

        }
    }

    public void renderPlayers(SpriteBatch batch, HashMap<String, Player> players, float deltaTime) {
        if (players == null) {
            Logger.log("players is null");
            return;
        }
        for (Player player : players.values()) {
            player.interpolatePosition(deltaTime);
            player.render(batch, deltaTime);
        }
    }

    public void renderGegner(SpriteBatch batch, ArrayList<Gegner> allGegner, float deltaTime) {
        if (allGegner == null) {
            Logger.log("players is null");
            return;
        }
        for (Gegner gegner : allGegner) {
            gegner.interpolatePosition(deltaTime);
            gegner.render(batch, deltaTime);
        }
    }

    public void renderNPCs(SpriteBatch batch, float deltaTime) {
        if (allNPCs == null) {
            Logger.log("NPCs is null");
            return;
        }
        for (int i=0; i<allNPCs.size(); i++) {
            NPC npc = allNPCs.get(i);
            npc.render(batch, deltaTime);
        }
    }

    float time = 0;

    public void renderAnimations(SpriteBatch batch, float deltaTime, ShapeRenderer shapeRenderer) {
        time += deltaTime;
        renderFireballs(batch, deltaTime, fireballAnimations, shapeRenderer);
        renderArrows(batch, deltaTime);
    }

    public static void renderBar(SpriteBatch batch, Texture[] bar, float value, float maxValue, float x, float y, float scaleX, float scaleY) {
        Sprite spriteEnd;
        Sprite spriteMiddle;
        Sprite spriteStart;

        if (value >= maxValue - 1) {
            spriteEnd = new Sprite(bar[0]);
        } else {
            spriteEnd = new Sprite(bar[1]);
        }
        spriteEnd.flip(true, false);
        spriteEnd.setPosition(x, y);
        spriteEnd.setScale(scaleX, scaleY);
        spriteEnd.draw(batch);

        int segments = (int) Math.ceil(maxValue / 2) - 2;
        float tempMax = maxValue;
        for (int i = 0; i < segments; i++) {
            tempMax -= 2;
            if (value >= tempMax - 1) {
                spriteMiddle = new Sprite(bar[2]);
            } else {
                spriteMiddle = new Sprite(bar[3]);
            }
            spriteMiddle.setScale(scaleX, scaleY);
            spriteMiddle.setPosition(x - (spriteMiddle.getWidth() * (i + 1f)) * scaleX + (scaleX - 1) * (-6), y);
            spriteMiddle.draw(batch);
        }
        spriteMiddle = new Sprite(bar[2]);

        if (value >= 1) {
            spriteStart = new Sprite(bar[0]);
        } else {
            spriteStart = new Sprite(bar[1]);
        }
        spriteStart.setPosition(x - segments * spriteMiddle.getWidth() * scaleX - scaleX * 20, y);
        spriteStart.setScale(scaleX, scaleY);
        spriteStart.draw(batch);
    }

    private float fadeTimer = 0f;

    public boolean fadeTextureOut(Batch batch, float delta, Texture texture, float worldX, float worldY, float duration, float basis) {
        float alpha = MyMath.getExpValue(basis, duration, fadeTimer);
        if (Float.isNaN(alpha)) {
            fadeTimer = 0f;
            return false;
        }
        batch.setColor(1, 1, 1, alpha);
        batch.draw(texture, worldX, worldY);
        fadeTimer += delta;
        batch.setColor(1, 1, 1, 1);
        return true;
    }


    /// ANIMATION HELPER
    private void renderFireballs(SpriteBatch batch, float deltaTime, Animation<TextureRegion>[] fireballAnimations, ShapeRenderer shapeRenderer) {
        ArrayList<AbilityInstance> toRemove = new ArrayList<>();

        Animation<TextureRegion> fireballAnimation_start = fireballAnimations[0];
        Animation<TextureRegion> fireballAnimation_fly = fireballAnimations[1];
        Animation<TextureRegion> fireballAnimation_endTime = fireballAnimations[2];
        Animation<TextureRegion> fireballAnimation_endHit = fireballAnimations[3];

        for (AbilityInstance fireball : activeFireballs) {
            fireball.elapsedTime += deltaTime;
            fireball.updatePosition(deltaTime);
            float rotation = fireball.rotation.angleDeg();

            if (fireball.hasHit) {
                fireball.endTimer += deltaTime;
                TextureRegion currentFrame = fireballAnimation_endHit.getKeyFrame(fireball.endTimer);
                drawFrame(batch, currentFrame, fireball, rotation);
                if (fireball.endTimer > fireballAnimation_endHit.getAnimationDuration()) {
                    toRemove.add(fireball);
                }
            } else if (fireball.elapsedTime <= fireballAnimation_start.getAnimationDuration()) {
                TextureRegion currentFrame = fireballAnimation_start.getKeyFrame(fireball.elapsedTime);
                drawFrame(batch, currentFrame, fireball, rotation);
            } else if (fireball.elapsedTime < fireball.lt) {
                TextureRegion currentFrame = fireballAnimation_fly.getKeyFrame(fireball.elapsedTime - fireballAnimation_start.getAnimationDuration());
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

    ShapeRenderer shapeRenderer = MainGameScreen.shapeRenderer;
    private void renderArrows(SpriteBatch batch, float deltaTime) {
        float r = 100f; // Radius des Kreises um den Spieler

        for (Vector3 arrow : activeArrows.values()) {
            Vector2 v = new Vector2(arrow.x, arrow.y);
            Vector2 OA = new Vector2(localPlayer.getX(), localPlayer.getY());
            float tWidth = assetManager.getArrowAssets().getWidth();

            Vector2 middlePoint = new Vector2(v.x*r+OA.x,v.y*r+OA.y);
            Vector2 addVector = new Vector2(v.y*tWidth/2, -(v.x*tWidth/2));

            OrthographicCamera camera = new OrthographicCamera();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
            shapeRenderer.setColor(Color.GREEN);
            Vector3 a = camera.project(new Vector3(middlePoint.x, middlePoint.y, 0));
            shapeRenderer.point(a.x+100, a.y+100, a.z);
            shapeRenderer.end();
            //Logger.log("localPlayer: (" + localPlayer.getX() + ", " + localPlayer.getY() + ")");

            //TODO arrow rendering

            Vector2 drawPos = new Vector2(middlePoint.x+addVector.x, middlePoint.y+addVector.y);

            // Sprite vorbereiten
            Sprite arrowTexture = new Sprite(assetManager.getArrowAssets());
            //arrowTexture.setOrigin(arrowTexture.getWidth() / 2, arrowTexture.getHeight() / 2);
            //arrowTexture.setScale(arrowTexture.getWidth() / arrow.z + 0.5f, arrowTexture.getHeight() / arrow.z);
            arrowTexture.setRotation(v.angleDeg()+90); // Pfeil in Richtung des Ziels ausrichten
            arrowTexture.setPosition(drawPos.x, drawPos.y);



            //arrowTexture.draw(batch);
        }
        updateAllArrows();
    }


    /**
     * Calculates the Distance and the normalized direction vector.
     *
     * @param p1 The 1. Player
     * @param p2 The 2. Player
     * @return Returns the normalized Vector in x and y and the distance as z in the Vector3
     */
    public static Vector3 calcDistAndDirection(Player p1, Player p2) {
        Vector2 direction = new Vector2(p1.getX() - p2.getX(), p1.getY() - p2.getY());
        direction.nor();
        return new Vector3(
            -direction.x,
            -direction.y,
            Vector2.dst(p1.getX(), p1.getY(), p2.getX(), p2.getY())
        );
    }
    @Deprecated
    public static boolean checkPlayerOnScreen(Player p) {
        float dX = Math.abs(p.getX() - localPlayer.getX());
        float dY = Math.abs(p.getY() - localPlayer.getY());
        return dX < (float) Gdx.graphics.getWidth() / 2 && dY < (float) Gdx.graphics.getHeight() / 2;
    }
    /**
     * Checks if the player is rendered on the Screen of the local Player
     *
     * @param p Player that should be checked
     * @return True - Player on Screen; False - Player not on Screen
     */
    public static boolean checkPlayerOnScreen(Player p, float zoom) {
        float dX = Math.abs(p.getX() - localPlayer.getX());
        float dY = Math.abs(p.getY() - localPlayer.getY());
        return dX < (float) Gdx.graphics.getWidth() * zoom / 2 && dY < (float) Gdx.graphics.getHeight() * zoom / 2;
    }


    private void drawFrame(SpriteBatch batch, TextureRegion currentFrame, AbilityInstance fireball, float rotation) {
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
    public ArrayList<AbilityInstance> getActiveFireballs() {
        return activeFireballs;
    }


    /// ABILITY HELPER
    public static void fireball(float pX, float pY, float velocityX, float velocityY, Vector2 rotation, float scale, float damage, float speed, float lt, Player player) {
        activeFireballs.add(new AbilityInstance(pX, pY, velocityX, velocityY, rotation, scale, damage, speed, lt, player));
    }

    private void updateAllArrows() {
        if (localPlayer.isSeeAllActive()) {
            for (Player p : allPlayers.values()) {
                if (!activeArrows.containsKey(p) && !checkPlayerOnScreen(p, mainGameScreen.getZoom())) {
                    activeArrows.put(p, calcDistAndDirection(localPlayer, p));
                }
            }
        }
        for (Player p : activeArrows.keySet()) {
            if (checkPlayerOnScreen(p, mainGameScreen.getZoom())) {
                activeArrows.remove(p);
                return;
            } else {
                Vector3 arrow = activeArrows.get(p);
                Vector3 directAndDist = calcDistAndDirection(localPlayer, p);
                activeArrows.replace(p, arrow, directAndDist);
            }
        }
    }
    public static void initArrows(Vector2 richtung, float distance, Player player) {
        Vector3 temp = new Vector3(
            richtung.x,
            richtung.y,
            distance
        );
        activeArrows.put(player, temp);
    }


    /// SHADER LOGIC
    public void initShaders() {
        Gdx.app.postRunnable(() -> {
            nightEffectShader = new ShaderProgram(
                Gdx.files.internal("shaders/night_effect.vert"),
                Gdx.files.internal("shaders/night_shader.frag")
            );

            if (!nightEffectShader.isCompiled()) {
                throw new IllegalStateException("Shader compilation failed: " + nightEffectShader.getLog());
            }
        });
    }

    private float lightRadius = 250.0f; // Radius des beleuchteten Bereichs
    private float fadeStart = 600.0f;  // Fade-Distanz
    private final Vector2 playerScreenPos = new Vector2(); // Position des Spielers auf dem Bildschirm
    private float altZOOM = 1;

    public void updateShaderUniforms(Vector2 playerWorldPos, OrthographicCamera camera) {
        // Berechne Spielerposition in Bildschirmkoordinaten
        Vector3 screenPos = camera.project(new Vector3(playerWorldPos.x, playerWorldPos.y, 0));
        playerScreenPos.set(screenPos.x, screenPos.y);

        // Zoomfaktor berücksichtigen und Werte anpassen
        if (camera.zoom != altZOOM) {
            float zoomFactor = altZOOM / camera.zoom;
            Logger.log(zoomFactor + "");
            lightRadius *= zoomFactor;
            fadeStart *= zoomFactor;
            altZOOM = camera.zoom;
        }

        // Setze Uniform-Werte
        nightEffectShader.bind();
        // Übergebe die Spielerposition in Bildschirmkoordinaten an den Shader
        nightEffectShader.setUniformf("u_playerPos", playerScreenPos.x, playerScreenPos.y);
        // Radius und Fade-Werte setzen
        nightEffectShader.setUniformf("u_lightRadius", lightRadius);
        nightEffectShader.setUniformf("u_fadeStart", fadeStart);
    }

    public void activateNightShader(Batch batch, OrthographicCamera camera) {
        // Hole die Spielerposition und aktualisiere die Uniforms
        Vector2 playerWorldPos = localPlayer.getPosition();
        updateShaderUniforms(playerWorldPos, camera);

        // Shader für das Batch setzen
        batch.setShader(nightEffectShader);
    }


    /// Helper class for tracking ability instances
    public static class AbilityInstance {
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

        AbilityInstance(float x, float y, float velocityX, float velocityY, Vector2 rotation, float scale, float damage, float speed, float lt, Player player) {
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
            if (!hasHit) {
                this.x += velocityX * deltaTime * speedFactor;
                this.y += velocityY * deltaTime * speedFactor;
            }
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getDamage() {
            return damage;
        }

        public void setHit() {
            this.hasHit = true;
        }

        public boolean hasHit() {
            return hasHit;
        }

        public Player getOwner() {
            return owner;
        }
    }
}
