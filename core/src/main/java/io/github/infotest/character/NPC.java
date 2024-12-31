package io.github.infotest.character;

import com.badlogic.gdx.math.Vector2;

public class NPC extends Player {
    public NPC(String name, int maxHealthPoints, Vector2 playerPosition, float speed) {
        super(name, "Player", maxHealthPoints, playerPosition, speed);

    }

    @Override
    public void castSkill() {

    }
}
