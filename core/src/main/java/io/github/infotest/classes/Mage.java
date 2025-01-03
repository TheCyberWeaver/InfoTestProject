package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.ServerConnection;

public class Mage extends Player {

    private static float fireballCost = 4f;
    private static float fireballDamage = 3f;
    private static float fireballCooldown = 0.5f;
    private static float fireballSpeed = 3f;
    private static float fireballScale = 1f;
    private static float fireballLT = 2f; // lifetime with 0.5 second on start and 0.7 s on hit and 0.8 on end without hit



    public Mage(String name, Vector2 playerPosition, Texture t) {
        super(name, "Mage",50, 150 ,playerPosition, 100,t);
    }

    @Override
    public void castSkill(int skillID,ServerConnection serverConnection) {
        //System.out.println(serverConnection.getPlayers().get(serverConnection.getMySocketId()).getName()+" "+this.getName());
         if(skillID == 1 && timeSinceLastT1Skill >= fireballCooldown) {
             timeSinceLastT1Skill = 0;
             castFireball(this.position.x, this.position.y, rotation);
             if(serverConnection.getPlayers().get(serverConnection.getMySocketId())==this){

                 serverConnection.sendCastSkill(this);
             }
             else{
                 //System.out.println("cast skill");
             }
         }
    }



    public void castFireball(float x, float y, Vector2 playerRot) {
        playerRot.nor();
        float velocityX = 1.5f * playerRot.x;
        float velocityY = 1.5f * playerRot.y;
        GameRenderer.fireball(x, y, velocityX, velocityY, playerRot, fireballScale, fireballDamage, fireballSpeed, fireballLT, this);


    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
         }
    public String toString() {
        return "Mage";
    }
}
