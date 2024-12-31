package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.classes.Mage;

public class Player extends Character {
    private Class klasse;
    private String[] items; // inventory

    private float timeSinceLastFireball;

    public Player(String name,Class klasse,
                  int maxHealthPoints, float healthRegen,
                  int maxMana, float manaRegen,
                  Vector2 position, float speed, Vector2 rotation,
                  Texture texture, int invSize) {
        super(name, healthRegen, maxHealthPoints, manaRegen, maxMana, position, speed);
        this.texture = texture;

        this.position = position;
        this.rotation = rotation;
        this.speed = speed;
        this.health = maxHealthPoints;
        this.healthRegen = healthRegen;
        this.maxHealth = maxHealthPoints;
        this.mana = maxMana;
        this.manaRegen = manaRegen;
        this.maxMana = maxMana;
        this.klasse = klasse;

        items = new String[invSize];

        timeSinceLastFireball = 0;

    }

    public Player(String name,Class klasse,
                  int maxHealthPoints, float health, float healthRegen,
                  int maxMana, float mana, float manaRegen,
                  Vector2 position, float speed, Vector2 rotation, Texture texture, int invSize) {
        super(name, healthRegen, maxHealthPoints, manaRegen, maxMana, position, speed);
        this.texture = texture;

        this.position = position;
        this.rotation = rotation;
        this.speed = speed;
        this.health = health;
        this.healthRegen = healthRegen;
        this.maxHealth = maxHealthPoints;
        this.mana = mana;
        this.manaRegen = manaRegen;
        this.maxMana = maxMana;
        this.klasse = klasse;

        items = new String[invSize];

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

    public void updateHPFromPlayerData(int health){
        this.health = health;
    }
    public void updateItemFromPlayerData(String[] items){

    }


    public String[] getItems() {
        return items;
    }
}

