package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class NPC extends Character {
    public NPC(String name,
               int maxHealthPoints, float healthRegen,
               int maxMana, float manaRegen,
               Vector2 playerPosition, float speed, Texture texture) {

        super(name, healthRegen, maxHealthPoints, manaRegen, maxMana, playerPosition, speed, texture);

    }

    @Override
    public void castSkill(int skillID) {

    }
}
