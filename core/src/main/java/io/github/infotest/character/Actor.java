package io.github.infotest.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.util.MyAssetManager;
import io.github.infotest.util.ServerConnection;

public abstract class Actor {


    // basic things
    protected float healthPoints;    // current HP
    protected float maxHealthPoints; // maximum HP
    protected boolean isAlive;

    //Movement related
    protected long lastUpdateTimestamp;
    protected Vector2 position ;
    protected Vector2 targetPosition;// World Position
    protected float speed;
    protected Vector2 velocity;
    protected float lerpSpeed = 20f;
    protected Vector2 rotation;

    // LibGDX related
    protected Texture texture;     // character texture
    protected static BitmapFont font;

    public Actor(int maxHealthPoints, Vector2 initialPosition, float speed, Texture texture) {
        this.maxHealthPoints = maxHealthPoints;
        this.healthPoints = maxHealthPoints; // full HP at first
        this.isAlive = true;
        this.speed = speed;

        //Movement related
        this.position = new Vector2(initialPosition);
        this.targetPosition = new Vector2(initialPosition);
        this.speed = speed;
        this.velocity = new Vector2(0, 0);
        this.lastUpdateTimestamp = System.currentTimeMillis();
        this.rotation = new Vector2(1, 0);

        this.texture = texture;

        if (font == null) {
            font = new BitmapFont(); // 只初始化一次
        }

    }
    public Actor(int maxHealthPoints, Vector2 initialPosition, float speed) {
        this.maxHealthPoints = maxHealthPoints;
        this.healthPoints = maxHealthPoints;
        this.isAlive = true;
        this.speed = speed;

        //Movement related
        this.position = new Vector2(initialPosition);
        this.targetPosition = new Vector2(initialPosition);
        this.speed = speed;
        this.velocity = new Vector2(0, 0);
        this.lastUpdateTimestamp = System.currentTimeMillis();
        this.rotation = new Vector2(1, 0);

        if (font == null) {
            font = new BitmapFont();
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

    public void takeDamage(float damage, ServerConnection serverConnection) {
        takeDamage(damage);
    }
    public void takeDamage(float damage) {
        healthPoints -= damage;
        if (healthPoints < 0) {
            healthPoints = 0;
        }
    }
    public void heal(int amount) {
        healthPoints += amount;
        if (healthPoints > maxHealthPoints) {
            healthPoints = maxHealthPoints;
        }
    }

    public abstract void render(Batch batch, float delta);
    public abstract void update(float delta);

    public void kill(){
        isAlive = false;
    }

    /// Getter / Setter
    public float getHealthPoints() {
        return healthPoints;
    }
    public void setHealthPoints(float healthPoints) {
        this.healthPoints = healthPoints;
    }
    public float getMaxHealthPoints() {
        return maxHealthPoints;
    }
    public Vector2 getRotation() {
        return rotation;
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
    public void updateRotationFromPlayerData(double Rx, double Ry) {
        this.rotation = new Vector2((float)Rx,(float)Ry);
    }
    public void updateisAlive(boolean alive) {
        isAlive = alive;
    }
    public boolean isAlive() {
        return isAlive;
    }
}
