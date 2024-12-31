package io.github.infotest.character;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.Main;
import io.github.infotest.MainGameScreen;

public abstract class Character {

    // basic things
    protected String name;
    protected int level;
    protected int experience;
    protected int neededExperience;
    protected boolean alive; // when alive == true character lives

    protected float health; // current Health
    protected float healthRegen; // health regeneration per second
    protected boolean isRegenerating; // if the character is regenerating health this is true
    protected int maxHealth; // maximum Health

    //Movement related
    private long lastUpdateTimestamp;
    protected Vector2 position = new Vector2(0,0);
    private Vector2 targetPosition;// World Position
    protected float speed;
    private Vector2 velocity;
    private float lerpSpeed = 10f;
    protected Vector2 rotation;

    // LibGDX related
    protected Texture texture; // character texture

    public Character(String name,float healthRegen, int maxHealth, float manaRegen, int maxMana, Vector2 initialPosition, float speed, Texture texture) {
        this.texture = texture;

        this.name = name;
        this.level = 1;
        this.experience = 0;
        this.neededExperience = MainGameScreen.neededExpForLevel(level);
        this.alive = true;

        this.health = maxHealth;
        this.healthRegen = healthRegen;
        this.maxHealth = maxHealth;

        //Movement related
        this.position = new Vector2(initialPosition);
        this.targetPosition = new Vector2(initialPosition);
        this.speed = speed;
        this.velocity = new Vector2(0, 0);
        this.lastUpdateTimestamp = System.currentTimeMillis();
        this.rotation = new Vector2(0, 0);
    }


    public void updateTargetPosition(Vector2 newTargetPosition) {
        this.targetPosition.set(newTargetPosition);
    }
    public Vector2 predictPosition() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTimestamp) / 1000f;

        // Prediction
        return new Vector2(position.x + velocity.x * deltaTime,
            position.y + velocity.y * deltaTime);
    }

    public void interpolatePosition(float deltaTime) {
        position.lerp(targetPosition, lerpSpeed * deltaTime); // 线性插值
    }

    public void update(float delta) {
        if (isRegenerating && health < maxHealth) {
            health += healthRegen * delta;
            if (health > maxHealth) {
                health = maxHealth;
            }
        }

        // 这里可以根据输入或者 AI 逻辑改变 x, y
        // 示例：x += speed * delta;

    }

    public void render(Batch batch) {
        if (texture != null) {
            Vector2 predictedPosition = predictPosition();
            batch.draw(texture, predictedPosition .x, predictedPosition .y,32,32);
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
            alive = false;
        }
        // 也可以在这里判断角色是否死亡
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public void gainExperience(int exp) {
        experience += exp;
        // 这里设置一个简单的升级机制，比如经验超过 100*等级 就升级 nö ;)
        if (experience >= neededExperience) {
            levelUp();
        }
    }

    protected void levelUp() {
        level++;
        experience = 0; // 升级后将经验清零或其他处理
        // 升级时也可以增加最大生命值或其他属性
        maxHealth = MainGameScreen.lvlToMaxHP(level);
        health = maxHealth;
        neededExperience = MainGameScreen.neededExpForLevel(level);
    }

    public void kill(){
        health = 0;
        alive = false;
    }

    // 抽象方法：角色技能（由各个子类实现）
    public abstract void castSkill(int skillID);


    // Getter、Setter
    public String getName() {
        return name;
    }
    public int getLevel(){return level;}
    public float getExp(){return experience;}
    public boolean isAlive(){return alive;}

    public float getHP() {
        return health;
    }
    public float getHPRegen() {return healthRegen;}
    public boolean isRegenerating() {return isRegenerating;}
    public int getMaxHP() {return maxHealth;}

    public float getX() {
        return position.x;
    }
    public void setX(float x) {
        position.x = x;
        targetPosition.x = x;
    }
    public float getY() { return position.y; }
    public void setY(float y) {

        position.y = y;
        targetPosition.y=y;
    }
    public Vector2 getPosition() {
        return position;
    }
    public void setPosition(Vector2 position) {
        this.position = position;
    }
    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }
    public Texture getTexture() {
        return texture;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    public String toString(){
        return "Character: Name:"+name;
    }
    public Vector2 getRotation(){
        return rotation;
    }
    public void setRotation(Vector2 rotation) {
        this.rotation = rotation;
    }
}
