package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.MainGameScreen;
import io.github.infotest.classes.Class;
import io.github.infotest.classes.Mage;

public class Player extends Character {
    private final io.github.infotest.classes.Class klasse;
    private String[] items; // inventory

    protected float mana; // current mana
    protected float manaRegen; // mana regeneration per second
    protected boolean isRegeneratingMana; // if the character is regenerating mana this is true
    protected int maxMana; // maximum Mana

    private float timeSinceLastT1Skill;

    public Player(String name, io.github.infotest.classes.Class klasse,
                  int maxHealthPoints, float healthRegen,
                  int maxMana, float manaRegen,
                  Vector2 position, float speed, Vector2 rotation,
                  Texture texture, int invSize) {

        super(name, healthRegen, maxHealthPoints, manaRegen, maxMana, position, speed, texture);
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

        timeSinceLastT1Skill = 0;

    }

    public Player(String name, io.github.infotest.classes.Class klasse,
                  int maxHealthPoints, float health, float healthRegen,
                  int maxMana, float manaRegen,
                  Vector2 position, Vector2 rotation, Texture texture, int invSize) {

        super(name, healthRegen, maxHealthPoints, manaRegen, maxMana, position, 0, texture);
        this.texture = texture;

        this.position = position;
        this.rotation = rotation;
        this.speed = speed;
        this.health = health;
        this.healthRegen = healthRegen;
        this.maxHealth = maxHealthPoints;
        this.mana = maxMana;
        this.manaRegen = manaRegen;
        this.maxMana = maxMana;
        this.klasse = klasse;

        items = new String[invSize];

        timeSinceLastT1Skill = 0;

    }


    public Player(String name, Class klasse,
                  int maxHealthPoints, float health, float healthRegen,
                  int maxMana, float mana, float manaRegen,
                  Vector2 position, float speed, Vector2 rotation, Texture texture, int invSize) {

        super(name, healthRegen, maxHealthPoints, manaRegen, maxMana, position, speed, texture);
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

        timeSinceLastT1Skill = 0;

    }

    @Override
    public void castSkill(){

    }

    //TODO player caracter system
    public void castSkill(int skillID) {
        if (skillID == 1) {
            timeSinceLastT1Skill = 0;
            klasse.castT1Skill(position.x, position.y, rotation);
        }

    }

    @Override
    public void update(float delta) {
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

        timeSinceLastT1Skill += delta;
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


    public void drainMana(int amount){
        mana -= amount;
        if (mana < 0) {
            mana = 0;
        }
    }
    public void healMana(int amount) {
        mana += amount;
        if (mana > maxMana) {
            mana = maxMana;
        }
    }

    @Override
    protected void levelUp() {
        level++;
        experience = 0; // 升级后将经验清零或其他处理
        // 升级时也可以增加最大生命值或其他属性
        maxHealth = MainGameScreen.lvlToMaxHP(level);
        health = maxHealth;
        maxMana = MainGameScreen.lvlToMaxMana(level);
        mana = maxMana;
        neededExperience = MainGameScreen.neededExpForLevel(level);
    }

    public String[] getItems() {
        return items;
    }
    public float getMana() {return mana;}
    public float getManaRegen() {return manaRegen;}
    public boolean isRegeneratingMana() {return isRegeneratingMana;}
    public int getMaxMana() {return maxMana;}
}

