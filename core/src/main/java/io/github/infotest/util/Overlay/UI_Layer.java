package io.github.infotest.util.Overlay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.*;
import io.github.infotest.MainGameScreen;
import io.github.infotest.character.Player;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.MyAssetManager;

public class UI_Layer implements ApplicationListener {

    MyAssetManager assetManager;

    MainGameScreen mainScreen;
    SpriteBatch batch;
    Camera uiCamera; // UI-specific camera
    Player player;
    Viewport viewport;
    ShapeRenderer shapeRenderer;
    Vector2 windowSize;

    private Texture[] healthbar;
    private Texture[] manabar;

    public UI_Layer(MainGameScreen mainScreen, MyAssetManager assetManager) {
        this.mainScreen = mainScreen;
        this.assetManager = assetManager;
        this.uiCamera = new OrthographicCamera(); // Create a new OrthographicCamera for UI
        viewport = new ScreenViewport(uiCamera);
        windowSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.healthbar = assetManager.getHealthBarAssets();
        this.manabar = assetManager.getManaBarAssets();

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
            batch.end();
        }
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

