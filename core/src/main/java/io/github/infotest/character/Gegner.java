package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import static io.github.infotest.MainGameScreen.*;
public abstract class Gegner extends  Actor{

    private final float killXP;

    public Gegner(int maxHealthPoints, Vector2 initialPosition, float speed, Texture texture, float exp) {
        super(maxHealthPoints, initialPosition, speed, texture);
        this.killXP = exp;
    }

    @Override
    public void render(Batch batch, float delta) {
        Vector2 predictedPosition = predictPosition();
        if (texture != null) {
            batch.draw(texture, predictedPosition.x, predictedPosition.y);
        }
    }



    public void getKilled(Player p){
        p.gainExperience(killXP);
    }


    public Player findPlayer(allPlayers.value Player){
        Player closestPlayer = null;
        float shortestDistance = Float.MAX_VALUE;
        if (allPlayers == null){
            return null;
        } else{
            distance = position.dst (player.getPosition());
        }
        if (distance < shortestDistance){
            shortestDistance = distance;
        }

        return closestPlayer;
    }


    @Override
    public void update(float delta) {
        allPlayers.value(player.getPosition);
        float distance = position.dst;
        if (distance <= attackRange) {
            performAttack(playerPosition);
        }
    }

    @Override
    public void update(float delta) {
        allPlayers.value(player.getPosition);
        float distance = position.dst;
        if (distance <= attackRange) {
            performAttack(playerPosition);
        }
    }

    public abstract void performAttack();

    @Override
    public String toString() {
        return "";
    }
}


