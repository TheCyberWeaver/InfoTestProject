package io.github.infotest.character;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.item.Item;
import io.github.infotest.util.ItemFactory;

import java.util.ArrayList;

public abstract class Player extends Actor{
    // basic things
    protected String name;
    protected String className;
    protected int level;
    protected float experience;
    protected ArrayList<Item> items;

    protected float mana;
    protected float maxMana;
    protected float manaRegen = 2f;

    protected float timeSinceLastT1Skill;


    public Player(String name, String className, int maxHealthPoints, int maxMana, Vector2 initialPosition, float speed, Texture t) {
        super(maxHealthPoints,initialPosition,speed, t);
        this.name = name;
        this.className = className;
        this.level = 1;
        this.experience = 0;
        items=new ArrayList<>();
        // use custom font
        // FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        // FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        // parameter.size = 16;
        // this.font = generator.generateFont(parameter);
        // generator.dispose();

        this.maxMana = maxMana;
        this.mana = maxMana;

        this.timeSinceLastT1Skill = 0;

    }


    /// game logic
    @Override
    public void render(Batch batch) {
        Vector2 predictedPosition = predictPosition();
        if (texture != null) {
            batch.draw(texture, predictedPosition .x, predictedPosition .y,32,32);
        }
        //System.out.println(name);
        //calculate name width

        GlyphLayout layout = new GlyphLayout(font, name);
        float textWidth = layout.width;

        font.draw(batch, name, predictedPosition.x + 16 - (int)textWidth/2, predictedPosition.y + 40);
    }

    @Override
    public void update(float delta){
        if (healthPoints < maxHealthPoints) {
            healthPoints += healthPointsRegen * delta;
            if (healthPoints > maxHealthPoints) {
                healthPoints = maxHealthPoints;
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


    /// Abilities
    public void gainExperience(float exp) {
        experience += exp;
        // 这里设置一个简单的升级机制，比如经验超过 100*等级 就升级
        if (experience >= 100 * level) {
            levelUp();
        }
    }

    protected void levelUp() {
        level++;
        experience = 0; // 升级后将经验清零或其他处理
        // 升级时也可以增加最大生命值或其他属性
        maxHealthPoints += 10;
        healthPoints = maxHealthPoints;
    }

    // 抽象方法：角色技能（由各个子类实现）
    public abstract void castSkill(int skillID);




    /// Getter / Setter
    public String getClassName() {
        return className;
    }
    public String getName(){
        return name;
    }
    public int getLevel() {
        return level;
    }
    public float getExperience() {
        return experience;
    }

    public float getMana() {
        return mana;
    }
    public float getMaxMana() {
        return maxMana;
    }
    public float getManaRegen() {
        return manaRegen;
    }

    public float getTimeSinceLastT1Skill() {
        return timeSinceLastT1Skill;
    }

    public ArrayList<Item> getItems() {
        return items;
    }
    public void updateItemFromPlayerData(String[] playerDataItems) {
        for (String itemName : playerDataItems) {
            Item item = ItemFactory.createItem(itemName);
            items.add(item);
        }
    }

    public void setRotation(Vector2 rotation) {
        this.rotation=rotation.cpy();
    }
    @Override
    public String toString(){
        return name+" "+className;
    }

}
