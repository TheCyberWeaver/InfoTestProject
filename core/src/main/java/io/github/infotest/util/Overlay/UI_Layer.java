package io.github.infotest.util.Overlay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.*;
import io.github.infotest.item.Item;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.MyAssetManager;

import java.util.ArrayList;

import static io.github.infotest.MainGameScreen.hasInitializedMap;
import static io.github.infotest.MainGameScreen.localPlayer;

public class UI_Layer implements ApplicationListener {

    MyAssetManager assetManager;

    SpriteBatch batch;
    Camera uiCamera; // UI-specific camera
    Viewport viewport;
    ShapeRenderer shapeRenderer;
    Vector2 windowSize;

    private final Texture[] healthbar;
    private final Texture[] manabar;
    private final Texture[] ausdauerbar;

    private float signTimer = 0;
    private boolean isRenderingSign = false;
    private float duration = 3;
    private float fadeDuration = 2;
    private float base;

    public UI_Layer( MyAssetManager assetManager) {
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

        if (hasInitializedMap) {
            batch.begin();
            GameRenderer.renderBar(batch, healthbar, localPlayer.getHealthPoints(), localPlayer.getMaxHealthPoints(),
                1250,
                900,
                nScale * screenScaleX, nScale * screenScaleY);
            GameRenderer.renderBar(batch, manabar, localPlayer.getMana(), localPlayer.getMaxMana(),
                1250,
                850,
                nScale * screenScaleX, nScale * screenScaleY);
            GameRenderer.renderBar(batch, ausdauerbar, localPlayer.getAusdauer(), localPlayer.getMaxAusdauer(),
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
        float screenX = localPlayer.getX()-50f*screenScaleX;
        float screenY = localPlayer.getY()-75f* screenScaleX;
        batch.draw(texture, screenX, screenY, texture.getWidth()*screenScaleX, texture.getHeight()* screenScaleX);
    }
    public void renderItems(Batch batch, Item[] items, ArrayList<Vector2> offset){
        float screenScaleX = Gdx.graphics.getWidth()/windowSize.x * nScale;
        for (int i=0; i<items.length; i++){
            Item item = items[i];
            Vector2 itemOffset = offset.get(i);
            if (item != null){
                batch.draw(item.getTexture(),
                    localPlayer.getX()+(itemOffset.x-51f)*screenScaleX,
                    localPlayer.getY()+(itemOffset.y-76f)*screenScaleX,
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

}

