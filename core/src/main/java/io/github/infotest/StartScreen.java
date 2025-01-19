package io.github.infotest;

//ChatGPT

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.infotest.util.Logger;

import static io.github.infotest.GameSettings.*;

public class StartScreen implements Screen {

    private final Main game;
    private Stage stage;
    private TextField nameTextField;
    private SelectBox<String> roleSelectBox;
    private SelectBox<String> serverSelectBox;
    private CheckBox devModeCheckBox;
    private Viewport viewport;

    private final SpriteBatch batch;
    private final Texture texture;

    public StartScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        texture=new Texture("ui/startscreen.png");
    }

    @Override
    public void show() {
        Logger.log("[StartScreen] StartScreen started");
        // Viewport initialization
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
        // set input to  stage, so that we can catch UI events
        Gdx.input.setInputProcessor(stage);

        // skin - default UI skin
        Skin skin = new Skin(Gdx.files.internal("ui/skin/freezing-ui.json"));

        // Title Label
        Label titleLabel = new Label("Select Your Name and Your Role", skin);
        titleLabel.setColor(Color.WHITE);
        titleLabel.setFontScale(1.2f); // Magnified x1.2

        // Text field for player name
        nameTextField = new TextField("", skin);
        nameTextField.setMessageText("Enter Your Name"); // Hint
        nameTextField.setMaxLength(20); // Maximum length
        nameTextField.setTextFieldListener((textField, c) -> {
            // monitor input in realtime (for sensitive characters e.g.)
        });

        // select Box: for selecting player class ("Mage" e.g.)
        roleSelectBox = new SelectBox<>(skin);
        roleSelectBox.setItems("Healer", "Assassin", "Defender", "Mage"); // 设置选项
        roleSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        serverSelectBox = new SelectBox<>(skin);
        serverSelectBox.setItems("Thomas' Server (v3.3)", "Local Server (v3.4)"); // 设置选项
        serverSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {


            }
        });

        devModeCheckBox = new CheckBox("Dev Mode", skin);
        devModeCheckBox.setChecked(isDevelopmentMode);
        devModeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isDevelopmentMode=devModeCheckBox.isChecked();
            }
        });

        // Start Game Button
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGame();

            }
        });

        // use Table to organize the layout
        Table table = new Table();
        table.setFillParent(true); // fill the screen completely
        table.defaults().pad(10);  // set space between widgets

        // add widgets to the table
        table.add(titleLabel).colspan(2).row();   // two columns and to next row
        table.add(new Label("Name:", skin));
        table.add(nameTextField).width(200).row();
        table.add(new Label("Role:", skin));
        table.add(roleSelectBox).width(150).row();
        table.add(new Label("Server:", skin));
        table.add(serverSelectBox).width(150).row();
        table.add(startButton).colspan(2);
        table.add(devModeCheckBox).width(200).row();


        // add table to stage
        stage.addActor(table);
    }
    public void startGame(){

        String playerName = nameTextField.getText();
        String selectedRole = roleSelectBox.getSelected();
        String selectedServer = serverSelectBox.getSelected();


        String selectedServerUrl;
        switch (selectedServer){
            case "Thomas' Server (v3.3)":
                selectedServerUrl="http://www.thomas-hub.com:9595";
                break;
            case "Local Server (v3.4)":
                selectedServerUrl="http://localhost:9595";
                break;
            default:
                selectedServerUrl="http://localhost:9595";
                break;
        }
        if (isDevelopmentMode) {
            playerName=defaultPlayerName;
            selectedRole=defaultPlayerClass;
            selectedServerUrl=defaultServer;
        }
        // Validation
        if (playerName == null || playerName.trim().isEmpty()) {
            Logger.log("[StartScreen WARNING]: Name cannot be empty!");
            return;
        }

        // Log
        Logger.log("-------Init Setup-------");
        Logger.log("User Name:" + playerName);
        Logger.log("Your Role:" + selectedRole);
        Logger.log("Your Server:" + selectedServerUrl);
        Logger.log("-------Init Setup-------");

        game.startGame(playerName, selectedRole,selectedServerUrl);
    }
    @Override
    public void render(float delta) {
        //Logger.log("render");
        // clear screen
        Gdx.gl.glClearColor(0.439f, 0.5f, 0.5625f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(texture,
            0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        handleInput();

        // draw stage
        stage.act(delta);
        stage.draw();
    }
    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            startGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // when the screen is hidden, stop processing input
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
