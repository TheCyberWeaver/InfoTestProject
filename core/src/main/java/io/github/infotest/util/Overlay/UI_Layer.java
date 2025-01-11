package io.github.infotest.util.Overlay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.*;
import io.github.infotest.MainGameScreen;
import io.github.infotest.character.Player;
import io.github.infotest.item.Item;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.MyAssetManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class UI_Layer implements ApplicationListener {

    MyAssetManager assetManager;

    MainGameScreen mainScreen;
    GameRenderer gameRenderer;
    SpriteBatch batch;
    Camera uiCamera; // UI-specific camera
    Player player;
    Viewport viewport;
    ShapeRenderer shapeRenderer;
    Vector2 windowSize;

    private Texture[] healthbar;
    private Texture[] manabar;
    private Texture[] ausdauerbar;

    private float signTimer = 0;
    private boolean isRenderingSign = false;
    private float duration = 3;
    private float fadeDuration = 2;
    private float base;

    public UI_Layer(MainGameScreen mainScreen, MyAssetManager assetManager, GameRenderer renderer) {
        this.mainScreen = mainScreen;
        this.gameRenderer = renderer;
        this.assetManager = assetManager;
        this.uiCamera = new OrthographicCamera(); // Create a new OrthographicCamera for UI
        viewport = new ScreenViewport(uiCamera);
        windowSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.healthbar = assetManager.getHealthBarAssets();
        this.manabar = assetManager.getManaBarAssets();
        this.ausdauerbar = assetManager.getAusdauerBarAssets();

        create();
    }

    public void create() {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.update(); // Update UI camera on resize
    }

    public void render() {

        float screenScaleX = windowSize.x/Gdx.graphics.getWidth();
        float screenScaleY = windowSize.y/Gdx.graphics.getHeight();
        float nScale = 0.75f;

        if (mainScreen.hasSeedReceived()) {
            batch.begin();
            GameRenderer.renderBar(batch, healthbar, player.getHealthPoints(), player.getMaxHealthPoints(),
                1250,
                900,
                nScale * screenScaleX, nScale * screenScaleY);
            GameRenderer.renderBar(batch, manabar, player.getMana(), player.getMaxMana(),
                1250,
                850,
                nScale * screenScaleX, nScale * screenScaleY);
            GameRenderer.renderBar(batch, ausdauerbar, player.getAusdauer(), player.getMaxAusdauer(),
                1250,
                800,
                nScale * screenScaleX, screenScaleY);
            batch.end();
        }
    }

    float nScale = 3f;
    public float getNScale() {
        return nScale;
    }
    public Vector2 getWindowSize() {
        return windowSize;
    }

    public void renderMarket(Batch batch, Texture texture) {
        float screenScaleX = Gdx.graphics.getWidth()/windowSize.x * nScale;
        float screenX = player.getX()-50f*screenScaleX;
        float screenY = player.getY()-75f* screenScaleX;
        batch.draw(texture, screenX, screenY, texture.getWidth()*screenScaleX, texture.getHeight()* screenScaleX);
    }
    public void renderItems(Batch batch, Item[] items, ArrayList<Vector2> offset){
        float screenScaleX = Gdx.graphics.getWidth()/windowSize.x * nScale;
        for (int i=0; i<items.length; i++){
            Item item = items[i];
            Vector2 itemOffset = offset.get(i);
            if (item != null){
                batch.draw(item.getTexture(),
                    player.getX()+(itemOffset.x-51f)*screenScaleX,
                    player.getY()+(itemOffset.y-76f)*screenScaleX,
                    14f*screenScaleX, 14f*screenScaleX);
            }
        }
    }

    public void startSignRendering(float duration, float fadeDuration, float base) {
        isRenderingSign = true;
        this.duration = duration;
        this.fadeDuration = fadeDuration;
        this.signTimer = 0f;
        this.base = base;
    }
    public boolean isRenderingSign(){
        return isRenderingSign;
    }
    public void resetRenderingSign(){
        isRenderingSign = false;
    }
    public float getDuration(){
        return duration;
    }
    public float getFadeDuration(){
        return fadeDuration;
    }
    public float getBase(){
        return base;
    }
    public float getSignTimer(){
        return signTimer;
    }
    public void addSignTimer(float delta){
        signTimer += delta;
    }
    public void resetTimer(){
        signTimer = 0;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        for (Texture tex : healthbar) {
            tex.dispose();
        }
        batch.dispose();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}

