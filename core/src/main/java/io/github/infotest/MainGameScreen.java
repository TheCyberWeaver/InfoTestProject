package io.github.infotest;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

public class MainGameScreen implements Screen, InputProcessor, ServerConnection.SeedListener {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    public static UI_Layer uiLayer;
    private MyAssetManager assetManager;

    private Vector3 clickPos = null;
    private boolean clicked = false;

    //Settings
    private boolean keepInventory;


    // Map data
    private int[][] map;
    private static final int CELL_SIZE = 32;
    private static final int INITIAL_SIZE = 3000;
    private static int numOfValidTextures = 4;

    // User character
    private Player player;
    private ServerConnection serverConnection;
    private boolean seedReceived = false;
    // Renderer
    private GameRenderer gameRenderer;

    // player list
    private HashMap<String, Player> players = new HashMap<>();
    private ArrayList<Gegner> allGegner = new ArrayList<>();
    private ArrayList<NPC> allNPC = new ArrayList<>();
    private int lastLength = 0;
    private NPC isTradingTo;

    private Main game;
    public int globalSeed = 0;

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
        assetManager.loadFireballAssets();
        assetManager.loadHealthBarAssets();
        assetManager.loadManaBarAssets();
        assetManager.loadAusdauerBarAssets();
        assetManager.loadNPCMaleAssets();
        assetManager.loadNPCWomenAssets();
        assetManager.loadNPCMarketAssets();
        assetManager.loadSignsAssets();
        assetManager.manager.finishLoading();

        // connect to server
        //serverConnection = new ServerConnection("http://www.thomas-hub.com:9595", assassinTexture);
        serverConnection = new ServerConnection(game.getServerUrl(), assetManager, game.clientVersion);

        serverConnection.setSeedListener(this);
        serverConnection.connect();


        this.uiLayer = new UI_Layer(this,assetManager,gameRenderer);
        Gdx.input.setInputProcessor(this);



        Vector2 spawnPosition = new Vector2(INITIAL_SIZE / 2f * CELL_SIZE, INITIAL_SIZE / 2f * CELL_SIZE);
        //Logger.log(""class: "+ game.getPlayerClass());
        player = PlayerFactory.createPlayer(serverConnection.getMySocketId(),game.getUsername(),game.getPlayerClass(),spawnPosition,assetManager);
        //Logger.log("class: "+ player.getClass());

        // send initial position to server
        serverConnection.sendPlayerInit(player);

        camera.zoom = 1f;
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        if(game.isDevelopmentMode){
            player.setSpeed(500);
        }

        isTradingTo= null;
    }
    @Override
    public void onSeedReceived(int seed) {
        // map initialization
        MapCreator mapCreator = new MapCreator(seed, INITIAL_SIZE,  numOfValidTextures);
        globalSeed = seed;
        map = mapCreator.initializePerlinNoiseMap();

        seedReceived = true;

        gameRenderer = new GameRenderer(assetManager, map, CELL_SIZE);
        gameRenderer.initAnimations();

        Logger.log("[MainGameScreen INFO]: Map generated after receiving seed: " + seed);
    }
    @Override
    public void render(float delta) {

        // update player list
        this.players = serverConnection.getPlayers();
        this.allNPC = serverConnection.getNPCs();
        if(serverConnection.getMySocketId()!=""){
            this.players.put(serverConnection.getMySocketId(), player);
        }

        //Logger.log(player);

        uiLayer.setPlayer(player);

        // clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(player!=null && gameRenderer!=null){
            // update camera position
            camera.position.set(player.getX(), player.getY(), 0);
            camera.update();
            batch.setProjectionMatrix(camera.combined);

            batch.begin();
            gameRenderer.renderMap(batch, camera.zoom, player.getPosition());
            gameRenderer.renderPlayers(batch, players, delta);
            gameRenderer.renderGegner(batch, allGegner, delta);
            handleInput(batch, delta);
            if (lastLength < allNPC.size()) {
                //Sort list based on y coordinate (dsc)
                allNPC.sort(new Comparator<NPC>() {
                    @Override
                    public int compare(NPC npc1, NPC npc2) {
                        return Float.compare(npc2.getPosition().y, npc1.getPosition().y);
                    }
                });
            }

            gameRenderer.renderNPCs(batch, allNPC, delta);
            gameRenderer.renderAnimations(batch,delta,shapeRenderer);

            // Render Market and Items
            if (isTradingTo != null) {
                uiLayer.renderMarket(batch, isTradingTo.getMarketTexture());
                uiLayer.renderItems(batch, isTradingTo.getMarket(), isTradingTo.getNPC_marketMapValue(isTradingTo.getMarketTextureID()));
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

            for(Player p: players.values()){
                p.update(delta);
            }
            //player.update(delta);
            checkFireballCollisions();

            if (player.getHealthPoints() <= 0) {
                player.kill();
                respawn(player);
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

        lastLength = allNPC.size();
        clicked = false;
    }

    Vector3 oldPosition = null;
    private void handleUIInput(Batch batch, float delta) {
        if (clickPos == null) return;
        if (!clickPos.equals(oldPosition)) {
            oldPosition = clickPos;
            Vector2 clickPosition = new Vector2(clickPos.x, clickPos.y);
            for (int i = 0; i < isTradingTo.getMarket().length; i++) {
                Vector2 itemPos = isTradingTo.getItemPos(i, player, uiLayer.getNScale(), uiLayer.getWindowSize());
                if (MyMath.inInPixelRange(itemPos, clickPosition, 21)) {
                    //Player has Clicked on Item
                    isTradingTo.trade(i, player);
                }
            }
        } else if (clickPos.equals(oldPosition) && clicked) {
            uiLayer.resetTimer();
        }
    }

    float tempTime = 0;
    private void handleInput(Batch batch, float delta) {
        boolean moved = false;
        float speed = player.getSpeed();

        tempTime += delta;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setX(player.getX() - speed * delta);
            moved = true;
            player.setRotation(new Vector2(-1,0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setX(player.getX() + speed * delta);
            moved = true;
            player.setRotation(new Vector2(1,0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setY(player.getY() + speed * delta);
            moved = true;
            player.setRotation(new Vector2(0,1));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setY(player.getY() - speed * delta);
            moved = true;
            player.setRotation(new Vector2(0,-1));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            player.castSkill(1,serverConnection);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            player.sprint(delta, game.isDevelopmentMode);
        } else if(player.isSprinting()){
            player.stopSprint();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (tempTime >= 0.5f){
                NPC npc = new NPC("NPC"+(allNPC.toArray().length+1),50,
                    new Vector2(player.getPosition().x-6.5f,player.getPosition().y),
                    0, 0, 0, assetManager);
                allNPC.add(npc);
                tempTime = 0;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F)){
            NPC cNpc = getClosestNPC();
            if(cNpc!=null){
                float distance = player.getPosition().dst(cNpc.getPosition());
                if (isTradingTo == null && distance <= 100 && !cNpc.isTrading()) {
                    cNpc.openMarket(batch);
                    isTradingTo = cNpc;
                }
            }
        }
        if (moved && isTradingTo != null) {
            isTradingTo.closeMarket();
            isTradingTo = null;
        }
        //Debug Key P
        if(Gdx.input.isKeyPressed(Input.Keys.P) && game.isDevelopmentMode && debugTimer>=1){

            Logger.log("----------");
//            for (Map.Entry<String, Player> stringPlayerEntry : players.entrySet()) {
//                Player tmpPlayer=stringPlayerEntry.getValue();
//                Logger.log(stringPlayerEntry.getKey()+" "+tmpPlayer.getName()+" "+tmpPlayer.getHealthPoints());
//            }
            for(NPC npc: allNPC){
                Logger.log(npc.toString());
            }
            Logger.log("----------");
            debugTimer=0;
        }

        if (moved) {
            // update position
            serverConnection.sendPlayerPosition(player.getX(), player.getY(),player.getRotation().x,player.getRotation().y);
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
        if(!game.isDevelopmentMode){
            camera.zoom = Math.max(0.5f, Math.min(1.5f, camera.zoom));
        }
        if(game.isDevelopmentMode){
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
        Vector2 playerPos = player.getPosition();
        for (NPC npc : allNPC){
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
        for (GameRenderer.FireballInstance fireball : gameRenderer.getActiveFireballs()) {
            for (Player p : players.values()){
                if (p.equals(fireball.getOwner())){
                    continue;
                }

                float dX = Math.abs(p.getX() - fireball.getX());
                float dY = Math.abs(p.getY() - fireball.getY());

                if (dX <= 16f && dY <= 16f && !fireball.hasHit()){
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

    /// GETTER / SETTER
    public boolean hasSeedReceived(){
        return seedReceived;
    }
    public boolean isKeepInventory(){
        return keepInventory;
    }
}
