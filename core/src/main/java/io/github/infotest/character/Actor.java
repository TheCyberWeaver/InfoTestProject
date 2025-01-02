package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.item.Item;
import io.github.infotest.util.ItemFactory;

import java.util.ArrayList;

public abstract class Actor {
    // basic things
    protected String name;
    protected float healthPoints;    // current HP
    protected float maxHealthPoints; // maximum HP
    protected float healthPointsRegen = 2f; // health regeneration per second

    //Movement related
    protected long lastUpdateTimestamp;
    protected Vector2 position =new Vector2(0,0);
    protected Vector2 targetPosition;// World Position
    protected float speed;
    protected Vector2 velocity;
    protected float lerpSpeed =20f;
    protected Vector2 rotation;

    // LibGDX related
    protected Texture texture;     // character texture
    protected static BitmapFont font;

    public Actor(String name,int maxHealthPoints, Vector2 initialPosition, float speed) {
        this.name = name;
        this.maxHealthPoints = maxHealthPoints;
        this.healthPoints = maxHealthPoints; // full HP at first
        this.speed = speed;

        //Movement related
        this.position = new Vector2(initialPosition);
        this.targetPosition = new Vector2(initialPosition);
        this.speed = speed;
        this.velocity = new Vector2(0, 0);
        this.lastUpdateTimestamp = System.currentTimeMillis();
        this.rotation = new Vector2(1, 0);

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

    public abstract void render(Batch batch);
    public abstract void update(float delta);

    // Getter、Setter
    public String getName() {
        return name;
    }
    public float getHealthPoints() {
        return healthPoints;
    }
    public float getMaxHealthPoints() {
        return maxHealthPoints;
    }
    public float getX() {
        return position.x;
    }
    public void setX(float x) {
        position.x = x;
        targetPosition.x = x;
    }
    public float getY() {
        return position.y;
    }
    public void setY(float y) {
        position.y = y;
        targetPosition.y=y;
    }
    public Vector2 getPosition() {
        return position;
    }
    public void setPosition(Vector2 position) {
        this.position = position;
        this.targetPosition = position;
    }
    public float getSpeed() {
        return speed;
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
    public abstract String toString();
    public void updateHPFromPlayerData(float hp) {

        healthPoints=  hp;
    }

}
