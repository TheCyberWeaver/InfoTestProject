package io.github.infotest.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.sun.tools.javac.jvm.Items;
import io.github.infotest.item.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class NPC extends Actor {

    protected String name;
    // 1.: Gender: 0 = male, 1 = female
    // 2.: Type: 1-8 NPC Type
    protected Vector2 genderType;
    protected Item[] market;
    protected Texture marketTexture;
    protected boolean isTrading;
    protected float marketTimer;

    protected Texture[] NPC_male;
    protected Texture[] NPC_women;
    protected Texture[] NPC_market;
    protected HashMap<Integer, ArrayList<Vector2>> NPC_marketMap;



    public NPC(String name, int maxHealthPoints, Vector2 playerPosition, float speed, Texture texture, int gender, int type) {
        super(maxHealthPoints, playerPosition, speed, texture);
        this.name = name;
        this.genderType = new Vector2(gender, type);
        this.isTrading = false;
        this.marketTimer = 0f;

    }

    private void updateMarketMap(){
        HashMap<Integer, ArrayList<Vector2>> temp = new HashMap<>();
        ArrayList<Vector2> temp1 = new ArrayList<>();
        temp1.add(new Vector2(44, 53));
        temp1.add(new Vector2(44, 69));
        temp1.add(new Vector2(44, 85));
        temp.put(0, temp1);

        temp1 = new ArrayList<>();


        temp1 = new ArrayList<>();
        temp1.add(new Vector2(20, 65));
        temp1.add(new Vector2(36, 65));
        temp1.add(new Vector2(52, 65));
        temp1.add(new Vector2(68, 65));
        temp1.add(new Vector2(20, 49));
        temp1.add(new Vector2(36, 49));
        temp1.add(new Vector2(52, 49));
        temp1.add(new Vector2(68, 49));
        temp.put(2, temp1);

    }

    @Override
    public void render(Batch batch) {


    }
    @Override
    public void update(float delta) {
        this.marketTimer += delta;
        if (this.marketTimer >= 300f) {
            updateMarket();
        }

    }

    public void updateMarket() {
        this.marketTexture = new Texture(); //TODO
    }

    public boolean trade(Item item){
        int itemID = contains(market, item);
        if (itemID >= 0){
            market[itemID] = null;
            return true;
        }
        return false;
    }

    public void openMarket(Batch batch){
        isTrading = true;
        float screenX = (float) Gdx.graphics.getWidth() / 2;
        float screenY = (float) Gdx.graphics.getHeight() / 2;
        batch.draw(marketTexture, screenX-50f, screenY-75f);
        for (int i = 0; i < market.length; i++){
            Item item = market[i];
            if (item != null){
                batch.draw(marketTexture, screenX-50f, screenY-75f);
            }
        }
    }

    private void cloaseMarket(){
        isTrading = false;
    }

    private static int contains(Item[] market, Item item){
        for (int i = 0; i < market.length; i++) {
            if (market[i].equals(item)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "NPC: " + name;
    }

    public String getName() {
        return name;
    }
    public Vector2 getGenderType() {
        return genderType;
    }
    public Item[] getMarket() {
        return market;
    }
    public void setMarket(Item[] market) {
        this.market = market;
    }
    public boolean isTrading() {
        return isTrading;
    }

}
