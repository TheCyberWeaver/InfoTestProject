package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.classes.Mage;

public class Player extends Character {
    private Class klasse;

    private float timeSinceLastFireball;

    public Player(String name, Vector2 position, Texture texture){
        super(name,);

        this.position = position;
        this.texture = texture;
        super.name = "name";

    }

    public Player(String name,Class klasse, int maxHealthPoints, float healthRegen, int maxMana, float manaRegen, Vector2 targetPosition, float speed, Vector2 rotation, Texture texture) {
        super(name, healthRegen, maxHealthPoints, manaRegen, maxMana, , speed);
        this.texture = texture;

        this.position = playerPosition;
        this.rotation = rotation;
        this.speed = speed;
        this.health = maxHealthPoints;
        this.healthRegen = healthRegen;
        this.maxHealth = maxHealthPoints;
        this.mana = maxMana;
        this.manaRegen = manaRegen;
        this.maxMana = maxMana;
        this.klasse = klasse;

        timeSinceLastFireball = 0;

    }

    @Override
    public void castSkill(){

    }

    //TODO player caracter system
    public void castSkill(int skillID) {
        klasse.cast(this);

        if (skillID == 1) {
            timeSinceLastFireball = 0;
        }

    }

    public void render(float delta) {
        if (health < maxHealth) {
            health += manaRegen * delta;
            if (health > maxHealth) {
                health = maxHealth;
            }
        }

        if (mana < maxMana) {
            mana += manaRegen * delta;
            if (mana > maxMana) {
                mana = maxMana;
            }
        }

        timeSinceLastFireball += delta;


    }

    @Override
    public void render(Batch batch) {
        if (texture != null) {
            Vector2 predictedPosition = predictPosition();
            batch.draw(texture, predictedPosition .x, predictedPosition .y,32,32);
        }
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
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = (int) maxHealth;
    }
    public float getMaxMana() {
        return maxMana;
    }
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }
}
