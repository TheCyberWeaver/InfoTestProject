package io.github.infotest.util.Factory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.classes.Assassin;
import io.github.infotest.classes.Defender;
import io.github.infotest.classes.Healer;
import io.github.infotest.classes.Mage;

public class PlayerFactory {
    public static Player createPlayer(String id, String playerName, String className, Vector2 initialPosition, Texture texture) {
        switch (className) {
            case "Assassin":
                return new Assassin(id, playerName, initialPosition, texture);
            case "Mage":
                return new Mage(id,playerName, initialPosition, texture);
            case "Defender":
                return new Defender(id,playerName, initialPosition, texture);
            case "Healer":
                return new Healer(id,playerName, initialPosition, texture);
            default:
                System.out.println("[WARNING]: Unknown class: " + className+ " - Player not created");
                return null;
        }
    }
}
