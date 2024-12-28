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
import io.github.infotest.util.MapCreator;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class MainGameScreen implements Screen, InputProcessor {
    private SpriteBatch batch;
    private Texture playerTexture;

    private Socket socket;

    // Store all players' position information,
    // key as socketId
    // value as a simple coordinate object
    private HashMap<String, Character> players = new HashMap<>();
    private String mySocketId;

    public int gameMode=1;//0:startScreenMode 1:gamingMode

    private static final int CELL_SIZE = 32; // 每个cell的像素大小
    private static final int INITIAL_COLUMNS = 80;
    private static final int INITIAL_ROWS = 60;

    private OrthographicCamera camera;

    private Texture assassin_Texture;

    private Texture normalBlock;
    private Texture grassBlock;
    private Texture rockBlock;

    private int[][] map; // 存储cell类型
    private Character player; // 玩家在地图中的位置
    private float delta;

    public int globalSeed=0;
    private Main game;

    public MainGameScreen(Game game){
        this.game= (Main) game;
        create();
    }

    public void create() {

        batch = new SpriteBatch();

        // load textures
        assassin_Texture =new Texture("assassin.png");


        normalBlock = new Texture("normal_block.jpg");
        grassBlock = new Texture("grass_block.jpg");
        rockBlock = new Texture("stone_block.png");

        connectToServer();

        //initialize the map
        MapCreator mapCreator=new MapCreator(globalSeed,INITIAL_ROWS,INITIAL_COLUMNS);
        map=mapCreator.initializeRandomMap();

        //TODO:Spawn Position needs to be calculated
        Vector2 spawnPosition = new Vector2(INITIAL_COLUMNS/2*CELL_SIZE, INITIAL_ROWS/2*CELL_SIZE);

        switch (game.getPlayerClass()) {
            case "Assassin":
                player= new Assassin(game.getUsername(),spawnPosition, assassin_Texture);
            case "Healer":
                player= new Assassin(game.getUsername(),spawnPosition, assassin_Texture);
                break;
            case "Defender":
                player= new Assassin(game.getUsername(),spawnPosition, assassin_Texture);
                break;
            case "Mage":
                player= new Mage(game.getUsername(),spawnPosition, assassin_Texture);
                break;
            default:
                player= new Assassin(game.getUsername(),spawnPosition, assassin_Texture);
                break;
        }

        sendPlayerPositionToServer();

        // initialize the camera
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1f;
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        Gdx.input.setInputProcessor(this);
    }



    private void connectToServer() {
        try {
            socket = IO.socket("http://www.thomas-hub.com:9595");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected to server");
                }
            }).on("init", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    // args[0] should be a JSON object containing information of all players
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        updatePlayersFromJSON(data);
                    }
                }
            }).on("updateAllPlayers", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        updatePlayersFromJSON(data);
                    }
                }
            }).on("playerLeft", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    // Remove the player who has left
                    if (args.length > 0) {
                        String leftPlayerId = (String) args[0];
                        players.remove(leftPlayerId);
                    }
                }
            }).on("initializeSeed", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0) {
                        globalSeed = (int) args[0];
                    }
                }
            });

            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayersFromJSON(JSONObject data) {
        // Clear the current players and reload them
        HashMap<String, Character> newPlayers = new HashMap<>();
        Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject pos = data.getJSONObject(key);
                float x = (float) pos.getDouble("x");
                float y = (float) pos.getDouble("y");

                newPlayers.put(key, new Assassin(key,new Vector2(x,y), assassin_Texture));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        players = newPlayers;
    }


    @Override
    public void render(float delta) {
        this.delta = delta;

        //clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update camera
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        renderMap();
        renderPlayers();
        batch.end();

        handleInput(delta);
    }

    private void renderMap(){
        for (int y = 0; y < INITIAL_ROWS; y++) {
            for (int x = 0; x < INITIAL_COLUMNS; x++) {
                Texture cellTexture;
                switch (map[y][x]) {
                    case 0:
                        cellTexture = normalBlock;
                        break;
                    case 1:
                        cellTexture = grassBlock;
                        break;
                    case 2:
                        cellTexture = rockBlock;
                        break;
                    default:
                        cellTexture = normalBlock;
                }
                batch.draw(cellTexture, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }
    private void renderPlayers() {
        for (String pid : players.keySet()) {
            Character player = players.get(pid);
            player.render(batch);
        }
    }
    private void handleInput(float delta) {

        // Handle keyboard input
        boolean moved = false;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setX(player.getX() - player.getSpeed() * delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setX(player.getX() + player.getSpeed() * delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setY(player.getY() + player.getSpeed() * delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setY(player.getY() - player.getSpeed() * delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            //TODO:
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            //TODO:
        }

        // If there is any movement, notify the server to update the position
        if (moved && socket != null && socket.connected()) {
            sendPlayerPositionToServer();
        }
    }
    private void sendPlayerPositionToServer() {
        JSONObject pos = new JSONObject();
        try {
            pos.put("x", player.getX());
            pos.put("y", player.getY());
            socket.emit("updatePosition", pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
    @Override
    public boolean scrolled(float amountX, float amountY) {
        //System.out.println("Scroll event detected: " + amountY);
        camera.zoom += amountY * 0.1f; // change camera zooming
        camera.zoom = Math.max(0.5f, Math.min(1.5f, camera.zoom)); // clamp zooming between 0.5 and 1.5
        return true;
    }

    // empty methods from InputProcessor (must be implemented)
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int i, int i1, int i2, int i3) {return false;}
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }


    //release resources
    @Override
    public void dispose() {
        if (socket != null) {
            socket.disconnect();
            socket.close();
        }
        batch.dispose();

        normalBlock.dispose();
        grassBlock.dispose();
        rockBlock.dispose();
    }

}
