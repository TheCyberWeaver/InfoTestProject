package io.github.infotest.character;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.item.Item;
import io.github.infotest.util.Factory.ItemFactory;
import io.github.infotest.util.Logger;
import io.github.infotest.util.MyAssetManager;
import io.github.infotest.util.ServerConnection;

import java.util.ArrayList;

import static io.github.infotest.MainGameScreen.CELL_SIZE;

import static io.github.infotest.GameSettings.*;

public abstract class Player extends Actor{

    // basic things
    public String id;
    protected String name;
    protected String className;
    protected int level;
    protected float experience;
    protected ArrayList<Item> items;


    protected float mana;
    protected float maxMana;
    protected float manaRegen = 2f;

    protected int gold=0;

    protected int INV_SIZE = 7;

    protected float ausdauer;
    protected float maxAusdauer;
    protected float ausdauerRegen = 3f;
    protected float ausdauerCost = 10f; //Ausdauer kosten pro Sekunde

    protected boolean isSprinting;

    protected boolean hasMoved;
    protected  boolean isHit;
    protected boolean isAttacking;
    protected float animationTime = 0f;

    protected float sprintingSpeed = speed*7/4;
    protected float normalSpeed;

    protected Vector2 spawnpoint;
    protected Vector2 lastDeathPos;



    //Assassin Att
    protected boolean seeAllActive = false;

    // Speech bubble fields
    private String speechBubbleMessage = null;
    private boolean isSpeechBubbleVisible = false;
    private float speechBubbleTimer = 0f;
    private float speechBubbleDuration = 4f;  // how many seconds to show
    // near top of Player class
    protected GlyphLayout glyphLayout = new GlyphLayout();

    protected float T1CoolDownTime =0f;
    protected float timeSinceLastT1Skill;

    public Player(String id, String name, String className, int maxHealthPoints, int maxMana, int maxAusdauer, Vector2 initialPosition, float speed) {
        super(maxHealthPoints,initialPosition,speed);
        this.id = id;
        this.name = name;
        this.className = className;
        this.level = 1;
        this.experience = 0;
        items=new ArrayList<>();
        for(int i=0;i<INV_SIZE;i++) {
            items.add(null);
        }
        // use custom font
        // FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        // FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        // parameter.size = 16;
        // this.font = generator.generateFont(parameter);
        // generator.dispose();

        this.maxMana = maxMana;
        this.mana = maxMana;

        this.maxAusdauer = maxAusdauer;
        this.ausdauer = maxAusdauer;

        this.isSprinting = false;
        this.isHit = false;
        this.isAttacking = false;
        this.normalSpeed = speed;

        this.spawnpoint = initialPosition;

        this.timeSinceLastT1Skill = 0;

    }

    /// game logic
    @Override
    public void render(Batch batch, float delta) {
        Vector2 predictedPosition = predictPosition();
        //1. draw texture if player has texture
        if (texture != null) {
            batch.draw(texture, predictedPosition .x-texture.getWidth()/2f, predictedPosition .y-texture.getHeight()/2f,32,32);
        }
        //2. render Player name
        GlyphLayout layout = new GlyphLayout(font, name);
        float textWidth = layout.width;
        font.draw(batch, name, predictedPosition.x + (CELL_SIZE /2f) - textWidth/2f  , predictedPosition.y + 80);

        // 3) Speech bubble logic
        if (isSpeechBubbleVisible && speechBubbleMessage != null) {
            // Update timer
            speechBubbleTimer += delta;
            if (speechBubbleTimer > speechBubbleDuration) {
                // Hide bubble
                isSpeechBubbleVisible = false;
            } else {
                // STILL VISIBLE, so draw it.

                // (A) Measure text
                glyphLayout.setText(font, speechBubbleMessage);
                float bubbleTextWidth = glyphLayout.width;
                float bubbleTextHeight = glyphLayout.height;

                // (B) Decide bubble position
                // Top-right of the player => offset from predictedPosition
                float offsetX = 40f; // shift to right
                float offsetY = 80f; // shift upwards from the player's sprite
                float bubbleX = predictedPosition.x + offsetX;
                float bubbleY = predictedPosition.y + offsetY;

                // (C) Define bubble rect size with some padding
                float padding = 8f;
                float bubbleWidth  = bubbleTextWidth + padding * 2;
                float bubbleHeight = bubbleTextHeight + padding * 2;

                // (D) Optional: draw a background shape using a 1×1 white texture with alpha
                // e.g., MyAssetManager.getWhitePixel()
                Texture whitePixel = new Texture("ui/whitePixel.png");

                // Set color for tinted draw (e.g., semi‐transparent black)
                batch.setColor(0f, 0f, 0f, 0.7f); // black with 70% alpha
                batch.draw(whitePixel, bubbleX, bubbleY,
                    bubbleWidth, bubbleHeight);

                // Reset color so the subsequent text is not tinted
                batch.setColor(1f, 1f, 1f, 1f);

                // (E) Draw text inside bubble
                float textX = bubbleX + padding;
                float textY = bubbleY + bubbleHeight - padding;
                font.draw(batch, glyphLayout, textX, textY);
            }
        }

    }


    @Override
    public void update(float delta){

//        if (mana < maxMana) {
//            mana += manaRegen * delta;
//            if (mana > maxMana) {
//                mana = maxMana;
//            }
//        }

        if (ausdauer < maxAusdauer && !isSprinting) {
            ausdauer += ausdauerRegen*delta;
            if (ausdauer > maxAusdauer) {
                ausdauer = maxAusdauer;
            }
        }
        timeSinceLastT1Skill += delta;
    }

    public void sprint(float delta){
        if (ausdauer > 1f) {
            this.isSprinting = true;
            if (!isDevelopmentMode) {
                this.ausdauer -= ausdauerCost * delta;
            }
            this.speed = this.sprintingSpeed;
            if (isDevelopmentMode) {
                this.speed = 750f;
            }
        } else {
            stopSprint();
        }

    }
    public void stopSprint(){
        this.isSprinting = false;
        this.speed = this.normalSpeed;
        if (isDevelopmentMode) {
            this.speed = 500f;
        }
    }


    /// Abilities
    public void gainExperience(float exp) {
        experience += exp;
        if (experience >= 100 * level) {
            levelUp();
        }
    }

    protected void levelUp() {
        level++;
        experience = 0;
        maxHealthPoints += 10;
        healthPoints = maxHealthPoints;
    }

    public abstract void castSkill(int skillID,ServerConnection serverConnection);

    @Override
    public void takeDamage(float damage, ServerConnection serverConnection) {
        takeDamage(damage);
        serverConnection.sendTakeDamage(this,damage);
        isHit = true;
    }
    @Override
    public void takeDamage(float damage) {
        super.takeDamage(damage);

        Logger.log("[Player INFO]: Player ["+this.name+"] took Damage! "+healthPoints+"/"+maxHealthPoints);
    }

    public boolean drainMana(float amount) {
        float tempMana = this.mana;
        this.mana -= amount;
        if (this.mana < 0) {
            this.mana = tempMana;
            return false;
        }
        return true;
    }

    public void respawn(){
        this.setLastDeathPos(this.getPosition());
        Vector2 spawnpoint = this.getSpawnpoint();
        this.setPosition(new Vector2(spawnpoint.x, spawnpoint.y));
        this.setAlive();

        this.setHealthPoints(this.getMaxHealthPoints());
        this.setMana(this.getMaxMana());

        this.resetT1Timer();

        if (!keepInventory){
            for (Item i : this.getItems()){
                i.drop(this.getLastDeathPos().x,this.getLastDeathPos().y);
            }
            this.clearInv();
        }
    }
    public void showMessage(String message,ServerConnection serverConnection) {
        showMessage(message);
        serverConnection.sendShowPlayerMessage(this,message);
    }
    public void showMessage(String message) {
        // 1) Store the message
        this.speechBubbleMessage = message;
        this.isSpeechBubbleVisible = true;

        // 2) Reset the timer
        this.speechBubbleTimer = 0f;

        // For debugging
        System.out.println("[Player Debug]: " + name + " says: " + message);
    }

    public abstract Texture getMainSkillSymbol();

    /// Getter / Setter
    public float getT1SkillCoolDownTime(){
        return T1CoolDownTime;
    }
    public float getT1SkillCoolDownTimer(){
        return timeSinceLastT1Skill;
    }
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
    public void setMana(float mana) {
        this.mana = mana;
    }
    public float getMaxMana() {
        return maxMana;
    }
    public float getManaRegen() {
        return manaRegen;
    }
    public float getAusdauer() {
        return ausdauer;
    }
    public float getMaxAusdauer() {
        return maxAusdauer;
    }
    public float getAusdauerRegen() {
        return ausdauerRegen;
    }
    public float getTimeSinceLastT1Skill() {
        return timeSinceLastT1Skill;
    }
    public ArrayList<Item> getItems() {
        return items;
    }
    public boolean addItem(Item item){
        for (int j=0;j<items.size();j++){
            Item i = items.get(j);
            if (i == null){
                items.set(j,item);
                Logger.log(items.toString());
                return true;
            }
        }
        return false;
    }
    public boolean addItem(Item item, int index){
        Item i = items.get(index);
        if (i == null){
            items.set(index,item);
            return true;
        }
        return false;
    }
    public void clearInv(){
        items.clear();
    }
    public void setId(String id) {
        this.id = id;
    }
    public void updateItems(ArrayList<String> itemIDs,MyAssetManager assetManager){
        for(int i = 0; i < itemIDs.size(); i++){
            items.set(i % INV_SIZE, ItemFactory.createItem(itemIDs.get(i), assetManager));
        }
        for(int i = itemIDs.size(); i < INV_SIZE; i++){
            items.set(i % INV_SIZE, null);
        }
    }
    public void kill(ServerConnection serverConnection) {
        super.kill();
        this.gold=0;
        serverConnection.sendPlayerDeath(this);
    }
    public void setRotation(Vector2 rotation) {
        this.rotation=rotation.cpy();
    }
    @Override
    public String toString(){
        return name+" "+className;
    }
    public boolean isSprinting() {
        return isSprinting;
    }
    public Vector2 getLastDeathPos(){
        return lastDeathPos;
    }
    public void setLastDeathPos(Vector2 lastDeathPos) {
        this.lastDeathPos = lastDeathPos;
    }
    public Vector2 getSpawnpoint(){
        return spawnpoint;
    }
    public void setAlive(){
        isAlive = true;
    }
    public void resetT1Timer(){
        timeSinceLastT1Skill = 0;
    }
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean isSeeAllActive() {
        return seeAllActive;
    }

    public int getGold() {
        return gold;
    }

    public void updateGold(int gold) {
        this.gold = gold;
    }
}
