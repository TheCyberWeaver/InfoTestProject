package io.github.infotest;

import com.badlogic.gdx.Game;

public class Main extends Game {
    private String username;
    private String playerClass;

    @Override
    public void create() {
        setScreen(new StartScreen(this)); //set to start screen
    }

    public void startGame(String username, String playerClass) {
        this.username = username;
        this.playerClass = playerClass;

        // switch to gaming screen
        setScreen(new MainGameScreen(this));
    }

    public String getUsername() {
        return username;
    }

    public String getPlayerClass() {
        return playerClass;
    }
}

