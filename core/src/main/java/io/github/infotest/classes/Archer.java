package io.github.infotest.classes;

import com.badlogic.gdx.math.Vector2;

public class Archer extends Class{
    @Override
    public void castT1Skill(float x, float y, Vector2 pRotation) {

    }

    public float getT1SkillCost() {
        return 0;
    }
    public float getT1SkillDamage() {
        return 0;
    }
    public float getT1SkillCooldown() {
        return 0;
    }
    public float getT1SkillSpeed() {
        return 0;
    }

    @Override
    public String toString() {
        return "Archer";
    }
}
