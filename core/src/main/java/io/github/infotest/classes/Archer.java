package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;

public class Archer extends Player {
    public Archer(String name, Vector2 initialPosition, Texture texture) {
        super(name, "Archer", 150, initialPosition, 75,  texture);
    }
    @Override
    public void castSkill() {

    }

    @Override
    public String toString() {
        return "Archer";
    }
}
