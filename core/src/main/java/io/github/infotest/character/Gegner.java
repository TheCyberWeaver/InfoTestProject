package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Gegner extends  Actor{

    float killXP;

    public Gegner(int maxHealthPoints, Vector2 initialPosition, float speed, Texture texture, float exp) {
        super(maxHealthPoints, initialPosition, speed, texture);
        this.killXP = exp;
    }

    @Override
    public void render(Batch batch) {
        Vector2 predictedPosition = predictPosition();
        if (texture != null) {
            batch.draw(texture, predictedPosition.x, predictedPosition.y);
        }
    }

    public void getKilled(Player p){
        p.gainExperience(killXP);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public String toString() {
        return "";
    }
}
