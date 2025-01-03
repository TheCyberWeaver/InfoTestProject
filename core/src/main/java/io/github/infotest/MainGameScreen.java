package io.github.infotest;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Gegner;
import io.github.infotest.character.Player;
import io.github.infotest.util.Overlay.UI_Layer;
import io.github.infotest.util.PlayerFactory;
import io.github.infotest.util.ServerConnection;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.MapCreator;

import java.util.ArrayList;
import java.util.HashMap;

public class MainGameScreen implements Screen, InputProcessor, ServerConnection.SeedListener {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private UI_Layer uiLayer;
    private AssetManager assetManager; //TODO

    // texture needed
    private Texture assassinTexture;
    private Texture normalBlock;
    private Texture grassBlock;
    private Texture rockBlock;
    private Texture basicWoodBlock;

    private Texture fireball_sheet_start;
    private Texture fireball_sheet_fly;
    private Texture fireball_sheet_endTime;
    private Texture fireball_sheet_endHit;
    private Texture[] fireball_sheets;

    private Texture[] textures;


    // Map data
    private int[][] map;
    private static final int CELL_SIZE = 32;
    private static final int INITIAL_SIZE = 3000;
    private static int numOfValidTextures = 4;
    private boolean seedReceived = false;

    // User character
    private Player player;
    private ServerConnection serverConnection;
    // Renderer
    private GameRenderer gameRenderer;

    // player list
    private HashMap<String, Player> players = new HashMap<>();
    private ArrayList<Gegner> allGegner = new ArrayList<>();

    private Main game;
    public int globalSeed = 0;

    public MainGameScreen(Game game) {
        this.game = (Main) game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.uiLayer = new UI_Layer(this);
        create();
    }

    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // load texture
        assassinTexture = new Texture("assassin.png");
        normalBlock = new Texture("normal_block.jpg");
        grassBlock = new Texture("grass_block.jpg");
        rockBlock = new Texture("stone_block.png");
        basicWoodBlock = new Texture("basicWood.png");

        fireball_sheets = new Texture[4];
        fireball_sheet_start = new Texture(Gdx.files.internal("fireball_sheet_start.png"));
        fireball_sheet_fly = new Texture(Gdx.files.internal("fireball_sheet_fly.png"));
        fireball_sheet_endTime = new Texture(Gdx.files.internal("fireball_sheet_endTime.png"));
        fireball_sheet_endHit = new Texture(Gdx.files.internal("fireball_sheet_endHit.png"));

        fireball_sheets[0] = fireball_sheet_start;
        fireball_sheets[1] = fireball_sheet_fly;
        fireball_sheets[2] = fireball_sheet_endTime;
        fireball_sheets[3] = fireball_sheet_endHit;

        // connect to server
        serverConnection = new ServerConnection("http://www.thomas-hub.com:9595", assassinTexture);
        serverConnection.setSeedListener(this);
        serverConnection.connect();


        textures = new Texture[numOfValidTextures];
        textures[0] = normalBlock;
        textures[1] = grassBlock;
        textures[2] = rockBlock;
        textures[3] = basicWoodBlock;


        Gdx.input.setInputProcessor(this);



        Vector2 spawnPosition = new Vector2(INITIAL_SIZE / 2f * CELL_SIZE, INITIAL_SIZE / 2f * CELL_SIZE);
        //System.out.println("class: "+ game.getPlayerClass());
        player = PlayerFactory.createPlayer(game.getUsername(),game.getPlayerClass(),spawnPosition,assassinTexture);
        //System.out.println("class: "+ player.getClass());

        // send initial position to server
        serverConnection.sendPlayerInit(player);

        camera.zoom = 1f;
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        if(game.isDevelopmentMode){
            player.setSpeed(500);
        }

    }
    @Override
    public void onSeedReceived(int seed) {
        // map initialization
        MapCreator mapCreator = new MapCreator(seed, INITIAL_SIZE, this, numOfValidTextures);
        globalSeed = seed;
        map = mapCreator.initializePerlinNoiseMap();

        seedReceived = true;

        gameRenderer = new GameRenderer(textures, map, CELL_SIZE);
        gameRenderer.initAnimations(fireball_sheets);

        System.out.println("Map generated after receiving seed: " + seed);
    }
    @Override
    public void render(float delta) {

        // update player list
        this.players = serverConnection.getPlayers();
        this.players.put(serverConnection.getMySocketId(), player);
        //System.out.println(player);

        uiLayer.setPlayer(player);
        uiLayer.render();

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
            gameRenderer.renderAnimations(batch,delta,shapeRenderer);
            batch.end();

            for(Player p: players.values()){
                p.update(delta);
            }
            //player.update(delta);
            checkFireballCollisions();

            handleInput(delta);
        }
        else{
            batch.begin();
            batch.draw(assassinTexture,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }
    }

    float tempTime = 0;
    private void handleInput(float delta) {
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

    // InputProcessor empty implementations
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
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

    @Override
    public void dispose() {
        // end server connection
        serverConnection.disconnect();

        // release rendering resources
        batch.dispose();
        assassinTexture.dispose();

        normalBlock.dispose();
        grassBlock.dispose();
        rockBlock.dispose();
        basicWoodBlock.dispose();

        assetManager.dispose();

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
                    p.takeDamage(fireball.getDamage());
                    fireball.setHit();
                }
            }

            for (Gegner gegner : allGegner){
                float dX = Math.abs(gegner.getX() - fireball.getX());
                float dY = Math.abs(gegner.getY() - fireball.getY());

                if (dX <= 7f && dY <= 7f){
                    gegner.takeDamage(fireball.getDamage());
                    fireball.setHit();
                }
            }
        }
    }

    /// GETTER / SETTER
    public Camera getCamera() {
        return camera;
    }
}
