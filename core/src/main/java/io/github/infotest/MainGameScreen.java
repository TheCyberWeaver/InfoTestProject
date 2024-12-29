package io.github.infotest;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Assassin;
import io.github.infotest.character.Character;
import io.github.infotest.character.Mage;
import io.github.infotest.util.ServerConnection;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.MapCreator;

import java.util.HashMap;

public class MainGameScreen implements Screen, InputProcessor {
    private SpriteBatch batch;
    private OrthographicCamera camera;

    // texture needed
    private Texture assassinTexture;
    private Texture normalBlock;
    private Texture grassBlock;
    private Texture rockBlock;

    // Map data
    private int[][] map;
    private static final int CELL_SIZE = 32;
    private static final int INITIAL_COLUMNS = 20;
    private static final int INITIAL_ROWS = 20;

    // User character
    private Character player;
    private ServerConnection serverConnection;
    // Renderer
    private GameRenderer gameRenderer;

    // player list
    private HashMap<String, Character> players = new HashMap<>();

    private Main game;
    public int globalSeed = 0;

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

        // connect to server
        serverConnection = new ServerConnection("http://www.thomas-hub.com:9595", assassinTexture);
        serverConnection.connect();

        // map initialization
        MapCreator mapCreator = new MapCreator(globalSeed, INITIAL_ROWS, INITIAL_COLUMNS);
        map = mapCreator.initializeRandomMap();

        gameRenderer = new GameRenderer(normalBlock, grassBlock, rockBlock, map, CELL_SIZE);

        Vector2 spawnPosition = new Vector2(INITIAL_COLUMNS / 2f * CELL_SIZE, INITIAL_ROWS / 2f * CELL_SIZE);

        switch (game.getPlayerClass()) {
            case "Assassin":
                player = new Assassin(game.getUsername(), spawnPosition, assassinTexture);
                break;
            case "Mage":
                player = new Mage(game.getUsername(), spawnPosition, assassinTexture);
                break;
            case "Healer":
                player= new Assassin(game.getUsername(),spawnPosition, assassinTexture);
                break;
            case "Defender":
                player= new Assassin(game.getUsername(),spawnPosition, assassinTexture);
                break;
            default:
                player = new Assassin(game.getUsername(), spawnPosition, assassinTexture);
                break;
        }

        // send initial position to server
        serverConnection.sendPlayerPosition(player.getX(), player.getY());

        camera.zoom = 1f;
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        Gdx.input.setInputProcessor(this);
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
        gameRenderer.renderMap(batch);
        gameRenderer.renderPlayers(batch, players,delta);
        batch.end();

        handleInput(delta);
    }

    private void handleInput(float delta) {
        boolean moved = false;
        float speed = player.getSpeed();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setX(player.getX() - speed * delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setX(player.getX() + speed * delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setY(player.getY() + speed * delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setY(player.getY() - speed * delta);
            moved = true;
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
        camera.zoom = Math.max(0.5f, Math.min(1.5f, camera.zoom));
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

        // gameRenderer.dispose();
    }
}
