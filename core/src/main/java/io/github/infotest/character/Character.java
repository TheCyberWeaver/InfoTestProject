package io.github.infotest.character;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;

public abstract class Character {
    // basic things
    protected String name;
    protected String className;
    protected int healthPoints;    // current HP
    protected int maxHealthPoints; // maximum HP
    protected int level;
    protected int experience;

    private static BitmapFont font;


    //Movement related
    private long lastUpdateTimestamp;
    protected Vector2 position =new Vector2(0,0);
    private Vector2 targetPosition;// World Position
    protected float speed;
    private Vector2 velocity;
    private float lerpSpeed = 10f;

    // LibGDX related
    protected Texture texture;     // character texture

    public Character(String name,String className, int maxHealthPoints, Vector2 initialPosition, float speed) {
        this.name = name;

        this.className = className;
        this.maxHealthPoints = maxHealthPoints;
        this.healthPoints = maxHealthPoints; // full HP at first

        this.level = 1;
        this.experience = 0;

        // use custom font
//        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
//        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        parameter.size = 16;
//        this.font = generator.generateFont(parameter);
//        generator.dispose();

        //Movement related
        this.position = new Vector2(initialPosition);
        this.targetPosition = new Vector2(initialPosition);
        this.speed = speed;
        this.velocity = new Vector2(0, 0);
        this.lastUpdateTimestamp = System.currentTimeMillis();

        if (font == null) {
            font = new BitmapFont(); // 只初始化一次
        }
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
        //  AI logic maybe

    }

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

    public void takeDamage(int damage) {
        healthPoints -= damage;
        if (healthPoints < 0) {
            healthPoints = 0;
        }
        // 也可以在这里判断角色是否死亡
    }

    public void heal(int amount) {
        healthPoints += amount;
        if (healthPoints > maxHealthPoints) {
            healthPoints = maxHealthPoints;
        }
    }

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

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String toString(){
        return name+" "+className;
    }
}
