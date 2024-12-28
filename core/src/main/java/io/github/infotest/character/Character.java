package io.github.infotest.character;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public abstract class Character {
    // 基础属性
    protected String name;         // 角色名称
    protected String className;
    protected int healthPoints;    // 生命值
    protected int maxHealthPoints; // 生命值上限
    protected int level;           // 等级
    protected int experience;      // 经验值

    // LibGDX 相关
    protected Texture texture;     // 角色的纹理
    protected Vector2 playerPosition=new Vector2(0,0);;          // 角色在世界坐标中的位置
    protected float speed;         // 角色的移动速度

    // 构造方法
    public Character(String name, int maxHealthPoints, Vector2 playerPosition, float speed) {
        this.name = name;
        this.maxHealthPoints = maxHealthPoints;
        this.healthPoints = maxHealthPoints; // 初始时满血
        this.playerPosition=playerPosition;
        this.speed = speed;

        this.level = 1;
        this.experience = 0;
    }

    // 更新方法（在游戏循环中调用，比如用于处理移动、状态更新等）

    public void update(float delta) {
        // 这里可以根据输入或者 AI 逻辑改变 x, y
        // 示例：x += speed * delta;

    }

    // 绘制方法
    public void render(Batch batch) {
        if (texture != null) {
            batch.draw(texture, playerPosition.x, playerPosition.y,32,32);
        }
    }

    // 受伤或扣血
    public void takeDamage(int damage) {
        healthPoints -= damage;
        if (healthPoints < 0) {
            healthPoints = 0;
        }
        // 也可以在这里判断角色是否死亡
    }

    // 加血
    public void heal(int amount) {
        healthPoints += amount;
        if (healthPoints > maxHealthPoints) {
            healthPoints = maxHealthPoints;
        }
    }

    // 升级方法
    public void gainExperience(int exp) {
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
    public abstract void castSkill();

    // Getter、Setter
    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public float getX() {
        return playerPosition.x;
    }
    public void setX(float x) {
        playerPosition.x = x;
    }

    public float getY() { return playerPosition.y; }
    public void setY(float y) {
        playerPosition.y = y;
    }
    public Vector2 getPlayerPosition() {
        return playerPosition;
    }
    public void setPlayerPosition(Vector2 playerPosition) {
        this.playerPosition = playerPosition;
    }


    public float getSpeed() {
        return speed;
    }

    public void setPosition(float x, float y) {
        this.playerPosition.x = x;
        this.playerPosition.y = y;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
