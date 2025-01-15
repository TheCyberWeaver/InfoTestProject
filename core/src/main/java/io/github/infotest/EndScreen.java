package io.github.infotest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * EndScreen is displayed when the player dies or the game ends.
 * It shows how long the player survived and provides a button
 * to restart the game, returning to the StartScreen.
 */
public class EndScreen implements Screen {

    private final Main game;      // Reference to the main Game class for screen switching
    private Stage stage;             // Scene2D stage to manage UI
    private float survivalTime;      // How long the player survived in seconds

    /**
     * Constructs the EndScreen with a reference to GameMain and the survival time.
     *
     * @param game         main game instance for screen switching
     * @param survivalTime the time (in seconds) the player survived
     */
    public EndScreen(Main game, float survivalTime) {
        this.game = game;
        this.survivalTime = survivalTime;
    }

    @Override
    public void show() {
        // Create a new Stage and set it to receive input
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create a default skin (assuming you have uiskin.json in assets)
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create UI elements
        Label titleLabel = new Label("Game Over", skin);
        titleLabel.setColor(Color.RED);
        titleLabel.setFontScale(2f);

        Label survivalLabel = new Label("You survived: " + survivalTime + " seconds", skin);
        survivalLabel.setColor(Color.WHITE);
        survivalLabel.setFontScale(1.2f);

        // Restart button
        TextButton restartButton = new TextButton("Game Restart", skin);
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Switch back to StartScreen
                game.setScreen(new StartScreen(game));
            }
        });

        // Use a Table for layout
        Table table = new Table();
        table.setFillParent(true);       // Make the table fill the entire stage
        table.defaults().pad(15);        // Default spacing around widgets

        // Add UI elements to the table
        table.add(titleLabel).row();
        table.add(survivalLabel).row();
        table.add(restartButton).row();

        // Add the table to the stage
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.439f, 0.5f, 0.5625f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport to the new window size
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Not used in this simple example
    }

    @Override
    public void resume() {
        // Not used in this simple example
    }

    @Override
    public void hide() {
        // Remove input processor when this screen is no longer active
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        // Clean up resources
        stage.dispose();
    }
}
