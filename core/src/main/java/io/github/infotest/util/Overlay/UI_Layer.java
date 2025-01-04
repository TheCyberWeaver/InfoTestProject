package io.github.infotest.util.Overlay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.infotest.MainGameScreen;
import io.github.infotest.character.Player;
import io.github.infotest.util.GameRenderer;

public class UI_Layer implements ApplicationListener {
    MainGameScreen mainScreen;
    SpriteBatch batch;
    Camera uiCamera; // UI-specific camera
    Player player;

    private Texture[] healthbar;

    public UI_Layer(MainGameScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.uiCamera = new OrthographicCamera(); // Create a new OrthographicCamera for UI
        create();
    }

    public void create() {
        this.batch = new SpriteBatch();

        healthbar = new Texture[6];
        healthbar[0] = new Texture(Gdx.files.internal("healthbar_start.png"));
        healthbar[1] = new Texture(Gdx.files.internal("healthbar_start_full.png"));
        healthbar[2] = new Texture(Gdx.files.internal("healthbar_middle.png"));
        healthbar[3] = new Texture(Gdx.files.internal("healthbar_middle_full.png"));
        healthbar[4] = new Texture(Gdx.files.internal("healthbar_ende.png"));
        healthbar[5] = new Texture(Gdx.files.internal("healthbar_ende_full.png"));
    }

    @Override
    public void resize(int width, int height) {
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.update(); // Update UI camera on resize
    }

    public void render() {
        // Apply UI camera and render
        batch.setProjectionMatrix(uiCamera.combined);

        batch.begin();
        // Health bar at the top-right of the screen
        GameRenderer.renderBar(batch, uiCamera, healthbar, player.getMaxHealthPoints(),
            uiCamera.viewportWidth - 200,
            uiCamera.viewportHeight - 50);

        // Optionally, render a player health bar above the player's position
        GameRenderer.renderBar(batch, uiCamera, healthbar, player.getMaxHealthPoints(),
            player.getX(),
            player.getY() + 50);

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
}

