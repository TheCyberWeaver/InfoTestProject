package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.Logger;
import io.github.infotest.util.ServerConnection;

public class Mage extends Player {

    public final Animation<TextureRegion> ATTACK_1;
    public final Animation<TextureRegion> DEATH;
    public final Animation<TextureRegion> HIT;
    public final Animation<TextureRegion> IDLE;
    public final Animation<TextureRegion> RUN;

    protected Animation<TextureRegion> STATE;

    private static float fireballCost = 5f;
    private static float fireballDamage = 16f;
    private static float fireballCooldown;
    private static float fireballSpeed = 3f;
    private static float fireballScale = 1f;
    private static float fireballLT = 2f; // lifetime with 0.5 second on start and 0.7 s on hit and 0.8 on end without hit


    public Mage(String id, String name, Vector2 playerPosition) {
        super(id, name, "Mage",60, 125, 50, playerPosition, 200);
        Texture[] animationSheets = assetManager.getMageAssets();
        ATTACK_1 = GameRenderer.sheetsToAnimation(8, 1, animationSheets[0], 0.1f);
        DEATH = GameRenderer.sheetsToAnimation(7, 1, animationSheets[1], 0.1f);
        HIT = GameRenderer.sheetsToAnimation(4, 1, animationSheets[2], 0.1f);
        IDLE = GameRenderer.sheetsToAnimation(6, 1, animationSheets[3], 0.1f);
        IDLE.setPlayMode(Animation.PlayMode.LOOP);
        RUN = GameRenderer.sheetsToAnimation(8, 1, animationSheets[4], 0.1f);
        RUN.setPlayMode(Animation.PlayMode.LOOP);

        fireballCooldown = ATTACK_1.getAnimationDuration()+0.5f;

        STATE = IDLE;
    }

    @Override
    public void castSkill(int skillID,ServerConnection serverConnection) {
        Player localPlayer=serverConnection.getPlayers().get(serverConnection.getMySocketId());
        switch(skillID) {
            case 1:
                if(timeSinceLastT1Skill >= fireballCooldown && drainMana(fireballCost) ||  localPlayer!=this) {
                    Logger.log("[Mage INFO]: Player ["+this.getName()+"] casts skill "+skillID);
                    timeSinceLastT1Skill = 0;

                    int xOffset = 0;
                    if (rotation.angleDeg() == 180) {
                        xOffset = -36;
                    } else {
                        xOffset = 47;
                    }

                    castFireball(this.position.x+xOffset, this.position.y+46, rotation);
                    if(localPlayer==this){
                        serverConnection.sendCastSkill(this, "Fireball");
                    }
                }
                //Logger.log(mana+"/"+maxMana);
                break;
            case 2:
                break;
            default:
                break;
        }
    }



    public void castFireball(float x, float y, Vector2 playerRot) {
        playerRot.nor();
        float velocityX = 1.5f * playerRot.x;
        float velocityY = 1.5f * playerRot.y;
        this.isAttacking = true;
        GameRenderer.fireball(x, y, velocityX, velocityY, playerRot, fireballScale, fireballDamage, fireballSpeed, fireballLT, this);
    }

    @Override
    public void render(Batch batch, float delta) {
        super.render(batch, delta);

        Animation<TextureRegion> oldState = STATE;
        if(!isAlive){
            STATE = DEATH;
        }else if(isAttacking) {
            STATE = ATTACK_1;
        } else if(isHit){
            STATE = HIT;
        } else if(hasMoved){
            STATE = RUN;
        } else {
            STATE = IDLE;
        }
        if(STATE!=oldState){
            animationTime = 0;
        }
        if(animationTime>=STATE.getAnimationDuration()){
            isAttacking = false;
            isHit = false;
            isSprinting = false;
        }
        Sprite currentFrame = new Sprite(STATE.getKeyFrame(animationTime));

        if (rotation.angleDeg() == 180) {
            currentFrame.flip(true, false);
        }

        currentFrame.setPosition(position.x-92, position.y-60);
        currentFrame.setOrigin(currentFrame.getWidth()/2, currentFrame.getHeight()/2);
        currentFrame.setScale(0.75f);
        currentFrame.draw(batch);

        animationTime += delta;

    }
    public String toString() {
        return "Mage: "+name;
    }
}
