package io.github.infotest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * EndScreen is displayed when the player dies or the game ends.
 * It shows how long the player survived and provides a button
 * to restart the game, returning to the StartScreen.
 */
public class EndScreen implements Screen {

    private final Main game;
    private Stage stage;
    private final float survivalTime; // in seconds

    private final SpriteBatch batch;
    private final Texture texture;

    public EndScreen(Main game, float survivalTime) {
        this.game = game;
        this.survivalTime = survivalTime;
        this.batch = new SpriteBatch();
        texture= new Texture("ui/endgamescreen.png");
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        //Label titleLabel = new Label("Game Over", skin);
        //titleLabel.setFontScale(2f);

        // Convert survivalTime to hh:mm:ss
        String timeString = formatTime(survivalTime);
        Label survivalLabel = new Label("Survived: " + timeString, skin);
        survivalLabel.setFontScale(1.2f);

        // Restart button
        TextButton restartButton = new TextButton("Restart Game", skin);
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Return to the StartScreen
                game.setScreen(new StartScreen(game));
            }
        });

        // Layout with a Table
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(15);

        //table.add(titleLabel).row();
        table.add(survivalLabel).row();
        table.add(restartButton).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(texture,
            0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    // The other Screen interface methods
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() { stage.dispose(); }

    /**
     * Converts time in seconds to a string in "HH:MM:SS" format.
     */
    private String formatTime(float timeInSeconds) {
        long totalSeconds = (long) timeInSeconds;      // truncate float to whole seconds
        long hours        = totalSeconds / 3600;
        long minutes      = (totalSeconds % 3600) / 60;
        long seconds      = totalSeconds % 60;

        // Format as hh:mm:ss with leading zeros if needed
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
