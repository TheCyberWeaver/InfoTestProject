package io.github.infotest.classes;

import com.badlogic.gdx.math.Vector2;
import io.github.infotest.util.GameRenderer;

public class Mage extends Class{
    private static final float fireballCost = 4;
    private static final float fireballDamage = 3;
    private static final float fireballCooldown = 2;
    private static final float fireballSpeed = 1.5f;

    public static void fireball(float x, float y, Vector2 playerRot) {
        playerRot.nor();
        float velocityX = 1.5f * playerRot.x;
        float velocityY = 1.5f * playerRot.y;

        float calculatedSpeed = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (calculatedSpeed != fireballSpeed) {
            System.out.println(calculatedSpeed + " ist ungleich 1.5");
        }

        GameRenderer.fireball(x, y, velocityX, velocityY, playerRot);
    }


    public static float getFireballCost() {
        return fireballCost;
    }

    public static float getFireballDamage() {
        return fireballDamage;
    }

    public static float getFireballCooldown() {
        return fireballCooldown;
    }

    public static float getFireballSpeed() {
        return fireballSpeed;
    }
}
