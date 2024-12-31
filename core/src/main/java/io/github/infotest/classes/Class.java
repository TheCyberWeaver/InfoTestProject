package io.github.infotest.classes;

import com.badlogic.gdx.math.Vector2;

public abstract class Class {

    public abstract void castT1Skill(float x, float y, Vector2 pRotation);

    public abstract float getT1SkillCost();
    public abstract float getT1SkillDamage();
    public abstract float getT1SkillCooldown();
    public abstract float getT1SkillSpeed();

    public abstract String toString();
}
