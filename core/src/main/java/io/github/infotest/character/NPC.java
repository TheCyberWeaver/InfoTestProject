package io.github.infotest.character;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class NPC extends Actor {
    public NPC(String name, int maxHealthPoints, Vector2 playerPosition, float speed) {
        super(name, maxHealthPoints, playerPosition, speed);

    }

    @Override
    public void render(Batch batch) {

    }
    @Override
    public void update(float delta) {

    }

    @Override
    public String toString() {
        return "";
    }
}
