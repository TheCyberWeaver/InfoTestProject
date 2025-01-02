package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.GameRenderer;

public class Mage extends Player {

    private static float fireballCost = 4;
    private static float fireballDamage = 3;
    private static float fireballCooldown = 2;
    private static float fireballSpeed = 3f;
    private static float fireballScale = 1f;


    public Mage(String name, Vector2 playerPosition, Texture t) {

        super(name, "Assassin",50, 150 ,playerPosition, 100,t);
    }

    @Override
    public void castSkill(int skillID) {
         if(skillID == 1){
             timeSinceLastT1Skill = 0;
             castFireball(position.x, position.y, rotation);

         }}



    public void castFireball(float x, float y, Vector2 playerRot) {
        playerRot.nor();
        float velocityX = 1.5f * playerRot.x;
        float velocityY = 1.5f * playerRot.y;


        float calculatedSpeed = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            System.out.println(calculatedSpeed);


        GameRenderer.fireball(x, y, velocityX, velocityY, playerRot, fireballScale, fireballSpeed);
    }



    public float getT1SkillCost() {
        return fireballCost;
    }
    public float getT1SkillDamage() {
        return fireballDamage;
    }
    public float getT1SkillCooldown() {
        return fireballCooldown;
    }
    public float getT1SkillSpeed() {
        return fireballSpeed;
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
         }
    public String toString() {
        return "Mage";
    }
}
