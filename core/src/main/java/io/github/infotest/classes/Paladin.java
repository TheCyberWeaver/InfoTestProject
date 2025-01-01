package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;

public class Paladin extends Player {

    public Paladin(String name, Vector2 initialPosition, Texture texture) {
        super(name, "Paladin", 150, initialPosition, 75,  texture);
    }

    @Override
    public void castSkill() {

    }

    @Override
    public String toString() {
        return "Paladin";
    }
}
