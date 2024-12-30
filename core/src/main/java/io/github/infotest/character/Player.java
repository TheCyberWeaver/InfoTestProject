package io.github.infotest.character;

import com.badlogic.gdx.math.Vector2;
import io.github.infotest.classes.Mage;

public class Player extends Character {
    private Vector2 position;
    private Vector2 rotation;
    private final float speed;
    private float health;
    private float maxHealth;
    private float mana;
    private float maxMana;

    private float timeSinceLastFireball;

    public Player(String name, int maxHealthPoints, Vector2 playerPosition, float speed, Vector2 position, Vector2 rotation, float health, float maxHealth, float mana, float maxMana) {
        super(name, "Player", maxHealthPoints, playerPosition, speed);

        this.position = position;
        this.rotation = rotation;
        this.speed = speed;
        this.health = health;
        this.maxHealth = maxHealth;
        this.mana = mana;
        this.maxMana = maxMana;
    }

    @Override
    public void castSkill() {

    }

    public void castFireball() {
        Mage.fireball(position.x, position.y, rotation);
    }



    public Vector2 getPosition() {
        return position;
    }
    public void setPosition(Vector2 position) {
        this.position = position;
    }
    @Override
    public Vector2 getRotation() {
        return rotation;
    }
    public void setRotation(Vector2 rotation) {
        this.rotation = rotation;
    }
    @Override
    public float getSpeed() {
        return speed;
    }
    public float getHealth() {
        return health;
    }
    public void setHealth(float health) {
        this.health = health;
    }
    public float getMana() {
        return mana;
    }
    public void setMana(float mana) {
        this.mana = mana;
    }
    public float getMaxHealth() {
        return maxHealth;
    }
    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }
    public float getMaxMana() {
        return maxMana;
    }
    public void setMaxMana(float maxMana) {
        this.maxMana = maxMana;
    }
}
