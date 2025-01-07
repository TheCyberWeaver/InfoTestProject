package io.github.infotest;

import com.badlogic.gdx.Game;
/*
The game contains several screens
Screens:
StartScreen
MainGameScreen
TODO: EndGameScreen
 */
public class Main extends Game {
    private String username;
    private String playerClass;
    private String serverUrl;
    public boolean isDevelopmentMode=true;

    private boolean keepInventory = true;
    /*
        create() will be called once the application is started
     */
    @Override
    public void create() {
        setScreen(new StartScreen(this)); //set to start screen
    }

    /*
    The Startscreen will be changed to Maingamescreen when the game starts
     */
    public void startGame(String username, String playerClass,String selectedServerUrl) {
        this.username = username;
        this.playerClass = playerClass;
        this.serverUrl=selectedServerUrl;
        // switch to gaming screen
        setScreen(new MainGameScreen(this));
    }
    public void setMainScreen(MainGameScreen mainScreen) {
        setScreen(mainScreen);
    }

    public String getUsername() {
        return username;
    }

    public String getPlayerClass() {
        return playerClass;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }
}

