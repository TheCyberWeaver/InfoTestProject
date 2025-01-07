package io.github.infotest.util.Factory;

import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.classes.Assassin;
import io.github.infotest.classes.Defender;
import io.github.infotest.classes.Healer;
import io.github.infotest.classes.Mage;
import io.github.infotest.util.Logger;
import io.github.infotest.util.MyAssetManager;

public class PlayerFactory {
    public static Player createPlayer(String id, String playerName, String className, Vector2 initialPosition, MyAssetManager assetManager) {
        switch (className) {
            case "Assassin":
                return new Assassin(id, playerName, initialPosition, assetManager.getPlayerAssets());
            case "Mage":
                return new Mage(id,playerName, initialPosition, assetManager.getPlayerAssets());
            case "Defender":
                return new Defender(id,playerName, initialPosition, assetManager.getPlayerAssets());
            case "Healer":
                return new Healer(id,playerName, initialPosition, assetManager.getPlayerAssets());
            default:
                Logger.log("[WARNING]: Unknown class: " + className+ " - Player not created");
                return null;
        }
    }
}
