package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class NPC extends Actor {

    protected String name;

    public NPC(String name, int maxHealthPoints, Vector2 playerPosition, float speed, Texture texture) {
        super(maxHealthPoints, playerPosition, speed, texture);
        this.name = name;

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
