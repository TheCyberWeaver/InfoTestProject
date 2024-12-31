package io.github.infotest.classes;

import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;

public class Defender extends Player {

    public Defender(String name, Vector2 initialPosition) {
        super(name, "Defender", 150, initialPosition, 75);
    }

    @Override
    public void castSkill() {

    }
}
