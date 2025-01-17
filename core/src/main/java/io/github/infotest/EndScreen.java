package io.github.infotest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EndScreen implements Screen {
    private final Main game;
    private final Screen gameScreen; // Reference to the game screen
    private Stage stage;
    private Label screenText;
    private TextButton respawnButton;

    public EndScreen(Main game, Screen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // "You Died" label
        screenText = new Label("YOU DIED!", skin);
        screenText.setColor(Color.RED);
        screenText.setFontScale(2f);
        screenText.setPosition(
            Gdx.graphics.getWidth() / 2f - screenText.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f + 50
        );

        // Respawn button
        respawnButton = new TextButton("Respawn", skin);
        respawnButton.setPosition(
            Gdx.graphics.getWidth() / 2f - respawnButton.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - 50
        );
        respawnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Respawn player and return to the game screen
                MainGameScreen.localPlayer.respawn();
                game.setScreen(gameScreen); // Switch back to the game screen
            }
        });

        stage.addActor(screenText);
        stage.addActor(respawnButton);
    }

    @Override
    public void render(float delta) {
        // Render the game screen first
        gameScreen.render(delta);

        // Then draw the overlay
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        gameScreen.pause();
    }

    @Override
    public void resume() {
        gameScreen.resume();
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
