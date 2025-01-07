package io.github.infotest.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sun.tools.javac.jvm.Items;
import io.github.infotest.item.Item;
import io.github.infotest.util.Logger;
import io.github.infotest.util.MyAssetManager;
import io.github.infotest.util.ServerConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NPC extends Actor {

    protected String name;
    // 1.: Gender: 0 = male, 1 = female
    // 2.: Type: 0-7 NPC Type
    protected Vector2 genderType;

    protected Item[] market;
    protected int marketTextureID;
    protected Texture marketTexture;
    protected HashMap<Integer, ArrayList<Vector2>> NPC_marketMap;

    protected boolean isTrading;

    protected MyAssetManager assetManager;



    public NPC(String name, int maxHealthPoints, Vector2 startPosition, float speed, int gender, int type, MyAssetManager assetManager) {
        super(maxHealthPoints, startPosition, speed);
        this.name = name;
        this.genderType = new Vector2(gender%2, type%8);
        this.isTrading = false;
        this.assetManager = assetManager;
        this.texture = genderTypeToTexture(gender%2, type%8);
        initMarketMap();
    }

    private void initMarketMap(){
        HashMap<Integer, ArrayList<Vector2>> hashMap = new HashMap<>();

        ArrayList<Vector2> temp1 = new ArrayList<>();
        temp1.add(new Vector2(44, 53));
        temp1.add(new Vector2(44, 69));
        temp1.add(new Vector2(44, 85));
        hashMap.put(0, temp1);

        ArrayList<Vector2> temp2 = new ArrayList<>();
        temp2.add(new Vector2(28, 76));
        temp2.add(new Vector2(44, 76));
        temp2.add(new Vector2(60, 76));
        temp2.add(new Vector2(28, 60));
        temp2.add(new Vector2(44, 60));
        temp2.add(new Vector2(60, 60));
        hashMap.put(1, temp2);

        ArrayList<Vector2> temp3 = new ArrayList<>();
        temp3.add(new Vector2(20, 65));
        temp3.add(new Vector2(36, 65));
        temp3.add(new Vector2(52, 65));
        temp3.add(new Vector2(68, 65));
        temp3.add(new Vector2(20, 49));
        temp3.add(new Vector2(36, 49));
        temp3.add(new Vector2(52, 49));
        temp3.add(new Vector2(68, 49));
        hashMap.put(2, temp3);

        ArrayList<Vector2> temp4 = new ArrayList<>();
        temp4.add(new Vector2(44, 115));
        temp4.add(new Vector2(60, 115));
        temp4.add(new Vector2(44, 99));
        temp4.add(new Vector2(60, 99));
        temp4.add(new Vector2(44, 83));
        temp4.add(new Vector2(60, 83));
        temp4.add(new Vector2(44, 67));
        temp4.add(new Vector2(60, 67));
        temp4.add(new Vector2(12, 83));
        temp4.add(new Vector2(12, 67));
        hashMap.put(3, temp4);

        ArrayList<Vector2> temp5 = new ArrayList<>();
        temp5.add(new Vector2(28, 103));
        temp5.add(new Vector2(44, 103));
        temp5.add(new Vector2(60, 103));
        temp5.add(new Vector2(28, 87));
        temp5.add(new Vector2(44, 87));
        temp5.add(new Vector2(60, 87));
        temp5.add(new Vector2(28, 71));
        temp5.add(new Vector2(44, 71));
        temp5.add(new Vector2(60, 71));
        temp5.add(new Vector2(28, 55));
        temp5.add(new Vector2(44, 55));
        temp5.add(new Vector2(60, 55));
        temp5.add(new Vector2(28, 39));
        temp5.add(new Vector2(44, 39));
        temp5.add(new Vector2(60, 39));
        hashMap.put(4, temp5);

        ArrayList<Vector2> temp6 = new ArrayList<>();
        temp6.add(new Vector2(20, 101));
        temp6.add(new Vector2(36, 101));
        temp6.add(new Vector2(52, 101));
        temp6.add(new Vector2(68, 101));
        temp6.add(new Vector2(20, 85));
        temp6.add(new Vector2(36, 85));
        temp6.add(new Vector2(52, 85));
        temp6.add(new Vector2(68, 85));
        temp6.add(new Vector2(20, 69));
        temp6.add(new Vector2(36, 69));
        temp6.add(new Vector2(52, 69));
        temp6.add(new Vector2(68, 69));
        temp6.add(new Vector2(20, 53));
        temp6.add(new Vector2(36, 53));
        temp6.add(new Vector2(52, 53));
        temp6.add(new Vector2(68, 53));
        temp6.add(new Vector2(20, 37));
        temp6.add(new Vector2(36, 37));
        temp6.add(new Vector2(52, 37));
        temp6.add(new Vector2(68, 37));
        hashMap.put(5, temp6);

        NPC_marketMap = hashMap;
    }

    @Override
    public void render(Batch batch) {
        batch.draw(new TextureRegion(texture), position.x, position.y, 0, 0,
            texture.getWidth(), texture.getHeight(), 3/4f, 3/4f,0);


    }
    @Override
    public void update(float delta) {


    }

    public void updateMarket(int pMarketTextureID, Item[] market) {
        this.marketTextureID = pMarketTextureID;
        this.marketTexture = assetManager.getNPCMarketAssets()[marketTextureID];
        this.market = market;

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
        ArrayList<Vector2> temp = NPC_marketMap.get(marketTextureID);
        isTrading = true;
        float screenX = (float) Gdx.graphics.getWidth() / 2 - 50f;
        float screenY = (float) Gdx.graphics.getHeight() / 2 - 75f;
        batch.draw(marketTexture, screenX, screenY);
        for (int i = 0; i < market.length; i++){
            Item item = market[i];
            if (item != null){
                batch.draw(item.getTexture(),screenX+temp.get(i).x, screenY+temp.get(i).y);
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

    private Texture genderTypeToTexture(int gender, int type){
        int tempType = type;
        if (gender == 0){
            if (type == 7){
                tempType = 6;
            }
            return assetManager.getNPCMaleAssets()[tempType];

        } else if (gender == 1){
            return assetManager.getNPCWomenAssets()[tempType];
        }
        Logger.log("[NPC: Warning] gender and Type is not valid");
        return null;
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
