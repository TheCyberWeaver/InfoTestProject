package io.github.infotest.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.item.Item;
import io.github.infotest.util.Factory.ItemFactory;
import io.github.infotest.util.Logger;
import io.github.infotest.util.MyAssetManager;
import io.github.infotest.util.ServerConnection;

import java.util.ArrayList;
import java.util.HashMap;

import static io.github.infotest.MainGameScreen.uiLayer;

public class NPC extends Actor {
    public String id;
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


    public int maxItemsNumber=3;

    public NPC(String id, String name, int maxHealthPoints, Vector2 startPosition, float speed, int gender, int type, int marketTextureID ,MyAssetManager assetManager) {
        super(maxHealthPoints, startPosition, speed);
        this.name = name;
        this.id=id;
        this.genderType = new Vector2(gender%2, type%8);
        this.isTrading = false;
        this.assetManager = assetManager;
        this.texture = genderTypeToTexture(gender%2, type%8);
        initMarketMap();
        Item[] items = new Item[maxItemsNumber]; //max Possible number of items
//        items[0] = new Apple(assetManager);
//        items[1] = new Apple(assetManager);
//        items[2] = new Apple(assetManager);
        updateMarket(marketTextureID, items);
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
    public void render(Batch batch, float delta) {
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

    public void trade(int itemIndex, Player player, ServerConnection serverConnection) {
        if (itemIndex >= 0){
            Item item = market[itemIndex];
            if (player.addItem(item)) {
                market[itemIndex] = null;
            } else if (item != null) {
                uiLayer.startSignRendering(0.5f, 1, 20);
            }
            if(item!=null){
                serverConnection.sendPlayerTradeWithNPC(this, item);
            }
        }
    }

    public void openMarket(Batch batch){
        isTrading = true;
    }
    public void closeMarket(){
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
    public Vector2 getItemPos(int itemID, Player player, float nScale, Vector2 windowSize){
        float screenScaleX = Gdx.graphics.getWidth()/windowSize.x * nScale;
        Vector2 awns = new Vector2();
        Vector2 offset = NPC_marketMap.get(marketTextureID).get(itemID);
        Vector2 playerPos = player.getPosition();

        awns.x = player.getX()+(offset.x-51f)*screenScaleX;
        awns.y = player.getY()+(offset.y-76f)*screenScaleX;

        return awns;
    }

    public void updateItems(ArrayList<String> itemIDs){
        for(int i = 0; i < itemIDs.size(); i++){
            market[i%maxItemsNumber]= ItemFactory.createItem(itemIDs.get(i),assetManager);
        }
        for(int i = itemIDs.size(); i < maxItemsNumber; i++){
            market[i]= null;
        }
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
    public Texture getMarketTexture() {
        return marketTexture;
    }
    public ArrayList<Vector2> getNPC_marketMapValue(int key) {
        return NPC_marketMap.get(key);
    }
    public int getMarketTextureID(){
        return marketTextureID;
    }
}
