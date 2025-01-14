package io.github.infotest;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.infotest.character.Actor;
import io.github.infotest.character.Gegner;
import io.github.infotest.character.NPC;
import io.github.infotest.character.Player;
import io.github.infotest.item.Item;
import io.github.infotest.util.*;
import io.github.infotest.util.Overlay.UI_Layer;
import io.github.infotest.util.Factory.PlayerFactory;
import io.github.infotest.util.ServerConnection;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.MapCreator;

import java.util.*;

import static io.github.infotest.GameSettings.keepInventory;
import static io.github.infotest.Main.isDevelopmentMode;

public class MainGameScreen implements Screen, InputProcessor, ServerConnection.SeedListener {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private MyAssetManager assetManager;
    private final OrthographicCamera camera;
    public static UI_Layer uiLayer;

    private Vector3 clickPos = null;
    private boolean clicked = false;



    // Map data
    public static int GLOBAL_SEED; // this will be assigned by the seed from server
    public static final int CELL_SIZE = 32;
    public static final int MAP_SIZE = 1000;
    public static int numOfValidTextures = 4;
    public static int[][] GAME_MAP=new int[MAP_SIZE][MAP_SIZE];

    // User character
    public static Player localPlayer;
    private ServerConnection serverConnection;

    // Renderer
    private GameRenderer gameRenderer;

    // player list
    public static HashMap<String, Player> allPlayers = new HashMap<>();
    public static ArrayList<Gegner> allGegner = new ArrayList<>();
    public static ArrayList<NPC> allNPCs = new ArrayList<>();
    private int numberOfNPCInTheLastFrame = 0;
    private NPC currentTradingToNPC;

    private final Main game;
    public static boolean hasInitializedMap = false;


    private float debugTimer=0;

    public MainGameScreen(Game game) {
        this.game = (Main) game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        create();
    }

    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        assetManager = new MyAssetManager();

        assetManager.loadLoadingScreen();
        assetManager.loadMapAssets();
        assetManager.loadPlayerAssets();
        assetManager.loadMageAssets();
        assetManager.loadFireballAssets();
        assetManager.loadHealthBarAssets();
        assetManager.loadManaBarAssets();
        assetManager.loadAusdauerBarAssets();
        assetManager.loadNPCMaleAssets();
        assetManager.loadNPCWomenAssets();
        assetManager.loadNPCMarketAssets();
        assetManager.loadSignsAssets();
        assetManager.manager.finishLoading();

        Actor.assetManager = assetManager;

        // connect to server
        //serverConnection = new ServerConnection("http://www.thomas-hub.com:9595", assassinTexture);
        serverConnection = new ServerConnection(game.getServerUrl(), assetManager, game.clientVersion);

        serverConnection.setSeedListener(this);
        serverConnection.connect();


        uiLayer = new UI_Layer(assetManager);
        Gdx.input.setInputProcessor(this);



        Vector2 spawnPosition = new Vector2(MAP_SIZE / 2f * CELL_SIZE, MAP_SIZE / 2f * CELL_SIZE);
        //Logger.log(""class: "+ game.getPlayerClass());
        localPlayer = PlayerFactory.createPlayer(serverConnection.getMySocketId(),game.getUsername(),game.getPlayerClass(),spawnPosition,assetManager);
        //Logger.log("class: "+ player.getClass());

        // send initial position to server
        if (localPlayer != null) {
            serverConnection.sendPlayerInit(localPlayer);
        }

        camera.zoom = 1f;
        if (localPlayer != null) {
            camera.position.set(localPlayer.getX(), localPlayer.getY(), 0);
        }
        camera.update();

        if(isDevelopmentMode){
            localPlayer.setSpeed(500);
        }

        currentTradingToNPC = null;
    }
    @Override
    public void onSeedReceived(int seed) {
        // map initialization
        MapCreator mapCreator = new MapCreator(seed);
        GLOBAL_SEED = seed;
        mapCreator.initializePerlinNoiseMap();

        localPlayer.setId(serverConnection.getMySocketId());

        hasInitializedMap = true;

        gameRenderer = new GameRenderer(assetManager);
        gameRenderer.initAnimations();

        Logger.log("[MainGameScreen INFO]: Map generated after receiving seed: " + seed);
    }
    @Override
    public void render(float delta) {


        if(!serverConnection.getMySocketId().isEmpty()){
           allPlayers.put(serverConnection.getMySocketId(), localPlayer);
        }

        //Logger.log(player);

        // clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(localPlayer !=null && gameRenderer!=null){
            // update camera position
            camera.position.set(localPlayer.getX(), localPlayer.getY(), 0);
            camera.update();
            batch.setProjectionMatrix(camera.combined);

            batch.begin();
            gameRenderer.renderMap(batch, camera.zoom, localPlayer.getPosition());
            gameRenderer.renderPlayers(batch, allPlayers, delta);
            gameRenderer.renderGegner(batch, allGegner, delta);
            handleInput(batch, delta);
            if (numberOfNPCInTheLastFrame < allNPCs.size()) {
                //Sort list based on y coordinate (dsc)
                allNPCs.sort(new Comparator<NPC>() {
                    @Override
                    public int compare(NPC npc1, NPC npc2) {
                        return Float.compare(npc2.getPosition().y, npc1.getPosition().y);
                    }
                });

            }

            gameRenderer.renderNPCs(batch, allNPCs, delta);
            gameRenderer.renderAnimations(batch,delta,shapeRenderer);

            // Render Market and Items
            if (currentTradingToNPC != null) {
                uiLayer.renderMarket(batch, currentTradingToNPC.getMarketTexture());
                uiLayer.renderItems(batch, currentTradingToNPC.getMarket(), currentTradingToNPC.getNPC_marketMapValue(currentTradingToNPC.getMarketTextureID()));
                handleUIInput(batch, delta);
            }
            // Render INV_FULL sign
            if (uiLayer.isRenderingSign()){
                Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                float worldX = worldCoordinates.x;
                float worldY = worldCoordinates.y;
                float time = uiLayer.getSignTimer();
                if (time <= uiLayer.getDuration()){
                    batch.draw(assetManager.getSignsAssets(), worldX, worldY);
                } else if (time > uiLayer.getDuration() && time-uiLayer.getSignTimer()< uiLayer.getFadeDuration()) {
                    float alpha = MyMath.getExpValue(uiLayer.getBase(), uiLayer.getFadeDuration(), time-uiLayer.getDuration());
                    batch.setColor(1,1,1,alpha);
                    batch.draw(assetManager.getSignsAssets(), worldX, worldY);
                    batch.setColor(1,1,1,1);
                    if (Float.isNaN(alpha)){
                        uiLayer.resetTimer();
                        uiLayer.resetRenderingSign();
                    }

                }
                uiLayer.addSignTimer(delta);
            }


            batch.draw(assetManager.getPlayerAssets(), 0, 0, 0, 0, assetManager.getPlayerAssets().getWidth(), assetManager.getPlayerAssets().getWidth(), 32, 32);
            batch.end();

            for(Player p: allPlayers.values()){
                p.update(delta);
            }
            //player.update(delta);
            checkFireballCollisions();

            if (localPlayer.getHealthPoints() <= 0 && localPlayer.isAlive()) {
                localPlayer.kill(serverConnection);
                //respawn(localPlayer);
            }
        }
        else{
            batch.begin();
            batch.draw(assetManager.getLoadingScreenTexture(),
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }
        debugTimer+=delta;
        uiLayer.render();

        numberOfNPCInTheLastFrame = allNPCs.size();
        clicked = false;
    }

    Vector3 oldPosition = null;
    private void handleUIInput(Batch batch, float delta) {
        if (clickPos == null) return;
        if (!clickPos.equals(oldPosition)) {
            oldPosition = clickPos;
            Vector2 clickPosition = new Vector2(clickPos.x, clickPos.y);
            for (int i = 0; i < currentTradingToNPC.getMarket().length; i++) {
                Vector2 itemPos = currentTradingToNPC.getItemPos(i, localPlayer, uiLayer.getNScale(), uiLayer.getWindowSize());
                if (MyMath.inInPixelRange(itemPos, clickPosition, 21)) {
                    //Player has Clicked on Item
                    currentTradingToNPC.trade(i, localPlayer);
                }
            }
        } else if (clickPos.equals(oldPosition) && clicked) {
            uiLayer.resetTimer();
        }
    }

    float tempTime = 0;
    private void handleInput(Batch batch, float delta) {
        boolean moved = false;
        float speed = localPlayer.getSpeed();

        tempTime += delta;
        if(localPlayer.isAlive()){
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                localPlayer.setX(localPlayer.getX() - speed * delta);
                moved = true;
                localPlayer.setRotation(new Vector2(-1,0));
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                localPlayer.setX(localPlayer.getX() + speed * delta);
                moved = true;
                localPlayer.setRotation(new Vector2(1,0));
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                localPlayer.setY(localPlayer.getY() + speed * delta);
                moved = true;
                localPlayer.setRotation(new Vector2(0,1));
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                localPlayer.setY(localPlayer.getY() - speed * delta);
                moved = true;
                localPlayer.setRotation(new Vector2(0,-1));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                localPlayer.castSkill(1,serverConnection);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
                localPlayer.sprint(delta, isDevelopmentMode);
            } else if(localPlayer.isSprinting()){
                localPlayer.stopSprint();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                if (tempTime >= 0.5f){
                    NPC npc = new NPC("NPC"+(allNPCs.toArray().length+1),50,
                        new Vector2(localPlayer.getPosition().x-6.5f, localPlayer.getPosition().y),
                        0, 0, 0, assetManager);
                    allNPCs.add(npc);
                    tempTime = 0;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.F)){
                NPC cNpc = getClosestNPC();
                if(cNpc!=null){
                    float distance = localPlayer.getPosition().dst(cNpc.getPosition());
                    if (currentTradingToNPC == null && distance <= 100 && !cNpc.isTrading()) {
                        cNpc.openMarket(batch);
                        currentTradingToNPC = cNpc;
                    }
                }
            }
        }



        if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            localPlayer.kill(serverConnection);
            Logger.log(localPlayer.isAlive()+"");
        }
        if (moved && currentTradingToNPC != null) {
            currentTradingToNPC.closeMarket();
            currentTradingToNPC = null;
        }
        //Debug Player status
        if(Gdx.input.isKeyPressed(Input.Keys.P) && isDevelopmentMode && debugTimer>=1){

            Logger.log("-----[Debug: showing player status]-----");
            Logger.log("socketID | Name | HP | ItemsLength | alive");
            for (Map.Entry<String, Player> stringPlayerEntry : allPlayers.entrySet()) {
                Player tmpPlayer=stringPlayerEntry.getValue();
                Logger.log(stringPlayerEntry.getKey()+" "+tmpPlayer.getName()+" "+tmpPlayer.getHealthPoints()+ " "+ tmpPlayer.getItems().size() +" "+tmpPlayer.isAlive());
                String str="";
                for (Item i : tmpPlayer.getItems()) {
                    if (i==null) {
                        str+="null ";
                    }
                    else{
                        str+=i+" ";
                    }
                }
                Logger.log("-> Items: "+str);
            }
            Logger.log("-----[Debug END]-----");
            debugTimer=0;
        }

        localPlayer.setHasMoved(moved);

        if (moved && localPlayer.isAlive()) {
            // update position
            serverConnection.sendPlayerPosition(localPlayer.getX(), localPlayer.getY(), localPlayer.getRotation().x, localPlayer.getRotation().y);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }
    @Override
    public void hide() { }
    @Override
    public void pause() { }
    @Override
    public void resume() { }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom += amountY * 0.1f;
        if(!isDevelopmentMode){
            camera.zoom = Math.max(0.5f, Math.min(1.5f, camera.zoom));
        }
        if(isDevelopmentMode){
            camera.zoom = Math.max(0.01f, camera.zoom);
        }

        return true;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        clickPos = camera.unproject(new Vector3(screenX, screenY, 0));
        clicked = true;
        return true;
    }

    // InputProcessor empty implementations
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int i, int i1, int i2, int i3) {return false;}
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }

    public static int lvlToMaxHP(int lvl){
        return 50 + 5 * lvl;
    }
    public static int lvlToMaxMana(int lvl){
        return 25 + 5 * lvl;
    }
    public static int neededExpForLevel(int lvl){
        return 20 + 10 * lvl;
    }

    public NPC getClosestNPC(){
        NPC cNpc = null;
        float dist = Float.MAX_VALUE;
        Vector2 playerPos = localPlayer.getPosition();
        for (NPC npc : allNPCs){
            float distanceSq = npc.getPosition().dst2(playerPos);
            if (distanceSq < dist){
                dist = distanceSq;
                cNpc = npc;
            }
        }
        return cNpc;
    }

    @Override
    public void dispose() {
        // end server connection
        serverConnection.disconnect();

        // release rendering resources
        batch.dispose();

        assetManager.manager.dispose();

        // gameRenderer.dispose();
    }

    /// GAME LOGIC
    public void checkFireballCollisions() {
        for (GameRenderer.AbilityInstance fireball : gameRenderer.getActiveFireballs()) {
            for (Player p : allPlayers.values()){
                if (p.equals(fireball.getOwner())){
                    continue;
                }

                float dX = Math.abs(p.getX() - fireball.getX());
                float dY = Math.abs(p.getY() - fireball.getY());

                if (dX <= 32f && dY <= 64f && !fireball.hasHit()){
                    p.takeDamage(fireball.getDamage(),serverConnection);

                    fireball.setHit();
                }
            }

            for (Gegner gegner : allGegner){
                float dX = Math.abs(gegner.getX() - fireball.getX());
                float dY = Math.abs(gegner.getY() - fireball.getY());

                if (dX <= 7f && dY <= 7f){
                    gegner.takeDamage(fireball.getDamage(),serverConnection);
                    fireball.setHit();
                }
            }
        }
    }
    public void respawn(Player p){
        p.setLastDeathPos(p.getPosition());
        Vector2 spawnpoint = p.getSpawnpoint();
        p.setPosition(new Vector2(spawnpoint.x, spawnpoint.y));
        p.setAlive();

        p.setHealthPoints(p.getMaxHealthPoints());
        p.setMana(p.getMaxMana());

        p.resetT1Timer();

        if (!keepInventory){
            for (Item i : p.getItems()){
                i.drop(p.getLastDeathPos().x,p.getLastDeathPos().y);
            }
            p.clearInv();
        }
    }

}
