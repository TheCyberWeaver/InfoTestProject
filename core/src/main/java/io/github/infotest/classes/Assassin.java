package io.github.infotest.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.infotest.MainGameScreen;
import io.github.infotest.character.Player;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.Logger;
import io.github.infotest.util.ServerConnection;

import java.util.ArrayList;
import java.util.Collection;

public class Assassin extends Player {

    private float seeAllCost = 20f;
    private float seeAllCooldown = 2f;

    public Assassin(String id,String name, Vector2 playerPosition, Texture t) {
        super(id, name, "Assassin",50, 50, 150, playerPosition, 200);
        setTexture(t);
    }

    @Override
    public void castSkill(int skillID, ServerConnection serverConnection) {
        switch (skillID) {
            case 1:
                if (timeSinceLastT1Skill >= seeAllCooldown && mana >= seeAllCost) {
                    timeSinceLastT1Skill = 0;
                    this.seeAllActive = true;
                    mana -= seeAllCost;
                    seeAll();
                }
                break;
        }
    }

    @Override
    public Texture getMainSkillSymbol() {
        return null;
    }

    public void closeCombat(){

    }

    public void seeAll(){
        Collection<Player> allPlayer = MainGameScreen.allPlayers.values();
        for(Player p : allPlayer){
            if (p.equals(this)) continue;
            Vector3 directAndDist = GameRenderer.calcDistAndDirection(this, p);
            Vector2 richtung = new Vector2(directAndDist.x, directAndDist.y);
            float dist = directAndDist.z;

            if (!GameRenderer.checkPlayerOnScreen(p)){
                GameRenderer.initArrows(richtung, dist, p);
            }

        }
    }


    @Override
    public void render(Batch batch, float delta) {
        super.render(batch, delta);
    }
}
