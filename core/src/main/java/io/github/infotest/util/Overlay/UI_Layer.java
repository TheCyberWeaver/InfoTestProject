package io.github.infotest.util.Overlay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.*;
import io.github.infotest.MainGameScreen;
import io.github.infotest.character.Player;
import io.github.infotest.util.GameRenderer;

public class UI_Layer implements ApplicationListener {
    MainGameScreen mainScreen;
    SpriteBatch batch;
    Camera uiCamera; // UI-specific camera
    Player player;
    Viewport viewport;
    ShapeRenderer shapeRenderer;
    Vector2 windowSize;

    private Texture[] healthbar;
    private Texture testTexture;

    public UI_Layer(MainGameScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.uiCamera = new OrthographicCamera(); // Create a new OrthographicCamera for UI
        viewport = new ScreenViewport(uiCamera);
        windowSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1); // Red
        shapeRenderer.rect(0, 0, 32, 32); // Mark the origin
        shapeRenderer.end();

        float screenScaleX = windowSize.x/Gdx.graphics.getWidth();
        float screenScaleY = windowSize.y/Gdx.graphics.getHeight();
        float nScale = 0.75f;

        batch.begin();
        GameRenderer.renderBar(batch, healthbar, player.getHealthPoints() ,player.getMaxHealthPoints(),
            viewport.getWorldWidth()+1250,
            viewport.getWorldHeight()+800,
            nScale*screenScaleX, nScale*screenScaleY);
        batch.end();
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
    public void setHealthbar(Texture[] healthbar) {
        this.healthbar = healthbar;
    }
    public void setTestTexture(Texture texture) {
        this.testTexture = texture;
    }
}

