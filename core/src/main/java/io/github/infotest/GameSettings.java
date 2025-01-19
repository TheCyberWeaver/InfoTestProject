package io.github.infotest;

public class GameSettings {
    public static final boolean keepInventory = true;
    public static final float defaultCameraZoom = 0.7f;

    public static final boolean highGrafik = true;

    /**
     * Dev
     */
    public static boolean isDevelopmentMode=true;
    //These settings will only be used if isDevelopmentMode==true
    public static final String defaultPlayerName = "Player " + (int)(Math.random() * 1000);
    public static final String defaultPlayerClass = "Mage";
    public static final String defaultServer = "http://localhost:9595";
}
