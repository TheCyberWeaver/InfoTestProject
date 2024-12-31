package io.github.infotest.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.classes.Assassin;
import io.github.infotest.classes.Mage;
import io.github.infotest.item.Apple;
import io.github.infotest.item.Item;

public class PlayerFactory {
    public static Player createPlayer(String playerName, String className, Vector2 initialPosition, Texture texture) {
        switch (className) {
            case "Assassin":
                return new Assassin(playerName, initialPosition, texture);
            case "Mage":
                return new Mage(playerName, initialPosition, texture);
            default:
                System.out.println("[WARNING]: Unknown class: " + className+ " - Player not created");
                return null;
        }
    }
}
