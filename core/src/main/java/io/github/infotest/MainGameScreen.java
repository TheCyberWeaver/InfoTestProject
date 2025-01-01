package io.github.infotest;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.PlayerFactory;
import io.github.infotest.util.ServerConnection;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.MapCreator;

import java.util.HashMap;

public class MainGameScreen implements Screen, InputProcessor {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private AssetManager assetManager; //TODO

    // texture needed
    private Texture assassinTexture;
    private Texture normalBlock;
    private Texture grassBlock;
    private Texture rockBlock;
    private Texture basicWoodBlock;

    // Map data
    private int[][] map;
    private static final int CELL_SIZE = 32;
    private static final int INITIAL_SIZE = 3000;
    private static int numOfValidTextures = 4;

    //player stats
    private static final int pMaxHP = 50;
    private static final float pHPRegen = 2;
    private static final int pMaxMana = 50;
    private static final float pManaRegen = 2;
    private static final float pSpeed = 1.5f;
    private static final int invSize = 10;

    // User character
    private Player player;
    private ServerConnection serverConnection;
    // Renderer
    private GameRenderer gameRenderer;

    // player list
    private HashMap<String, Player> players = new HashMap<>();

    private Main game;
    public int globalSeed;

    public MainGameScreen(Game game) {
        this.game = (Main) game;
        create();
    }

    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // load texture
        assassinTexture = new Texture("assassin.png");
        normalBlock = new Texture("normal_block.jpg");
        grassBlock = new Texture("grass_block.jpg");
        rockBlock = new Texture("stone_block.png");
        basicWoodBlock = new Texture("basicWood.png");


        // connect to server
        serverConnection = new ServerConnection("http://www.thomas-hub.com:9595", assassinTexture);
        serverConnection.connect();

        // map initialization
        MapCreator mapCreator = new MapCreator(globalSeed, INITIAL_SIZE, this, numOfValidTextures);
        System.out.println("Seed: "+globalSeed);
        map = mapCreator.initializePerlinNoiseMap();

        Texture[] textures = new Texture[numOfValidTextures];
        textures[0] = normalBlock;
        textures[1] = grassBlock;
        textures[2] = rockBlock;
        textures[3] = basicWoodBlock;

        gameRenderer = new GameRenderer(textures, map, CELL_SIZE);

        Vector2 spawnPosition = new Vector2(INITIAL_SIZE / 2f * CELL_SIZE, INITIAL_SIZE / 2f * CELL_SIZE);

        player = PlayerFactory.createPlayer(game.getUsername(), game.getPlayerClass(), spawnPosition, assassinTexture);

        // send initial position to server
        // serverConnection.sendPlayerPosition(player.getX(), player.getY());
        serverConnection.sendPlayerInit(player);

        camera.zoom = 1f;
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        Gdx.input.setInputProcessor(this);

        if(game.isDevelopmentMode){
            player.setSpeed(500);
        }
    }

    @Override
    public void render(float delta) {

        // update player list
        this.players = serverConnection.getPlayers();
        this.players.put(serverConnection.getMySocketId(), player);
        //System.out.println(player);

        // clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update camera position
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        gameRenderer.renderMap(batch, camera.zoom, player.getPosition());
        gameRenderer.renderPlayers(batch, players,delta);
        gameRenderer.renderAnimations(batch, delta, assassinTexture);
        player.update(delta);

        batch.end();

        handleInput(delta);
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
            if (tempTime > 1f) {
                System.out.println("Button pressed");
                player.castSkill(1);
                tempTime = 0;
            }
        }

        if (moved) {
            // update position
            serverConnection.sendPlayerPosition(player.getX(), player.getY());
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

    public static int get_PMaxHP(){
        return pMaxHP;
    }
    public static float get_PHPRegen(){
        return pHPRegen;
    }
    public static int get_PMaxMana(){
        return pMaxMana;
    }
    public static float get_PManaRegen(){
        return pManaRegen;
    }
    public static float get_PSpeed(){
        return pSpeed;
    }
    public static int get_PInvSize(){
        return invSize;
    }
}
