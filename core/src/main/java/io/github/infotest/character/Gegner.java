package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Gegner extends  Actor{
    public Gegner(int maxHealthPoints, Vector2 initialPosition, float speed, Texture texture) {
        super(maxHealthPoints, initialPosition, speed, texture);
    }

    @Override
    public void render(Batch batch) {
        Vector2 predictedPosition = predictPosition();
        if (texture != null) {
            batch.draw(texture, predictedPosition.x, predictedPosition.y);
        }
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public String toString() {
        return "";
    }
}
