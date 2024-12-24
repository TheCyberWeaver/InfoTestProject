package io.github.infotest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private SpriteBatch batch;
    private Texture playerTexture;

    private Socket socket;
    private float playerX = 0;
    private float playerY = 0;

    // Store all players' position information,
    // key as socketId
    // value as a simple coordinate object
    private HashMap<String, Player> players = new HashMap<>();
    private String mySocketId;

    private Texture t;

    public int gameMode=0;//0:startScreenMode 1:gamingMode
    @Override
    public void create() {

        setScreen(new StartScreen(this));

        batch = new SpriteBatch();
        t=new Texture("assassin.png");


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
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayersFromJSON(JSONObject data) {
        // Clear the current players and reload them
        HashMap<String, Player> newPlayers = new HashMap<>();
        Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject pos = data.getJSONObject(key);
                float x = (float) pos.getDouble("x");
                float y = (float) pos.getDouble("y");

                newPlayers.put(key, new Assassin(key,50,x,y,0.1f,t));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        players = newPlayers;
    }

    @Override
    public void render() {
        if (gameMode == 0) {
            super.render();
        }
        if (gameMode==1){
            handleInput();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            renderPlayers();
        }
    }
    private void renderPlayers() {

        // render players
        batch.begin();
        for (String pid : players.keySet()) {
            Player player = players.get(pid);
            player.render(batch);
        }
        batch.end();
    }
    private void handleInput() {
        // Handle keyboard input
        boolean moved = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerX -= 2;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerX += 2;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerY += 2;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerY -= 2;
            moved = true;
        }

        // If there is any movement, notify the server to update the position
        if (moved && socket != null && socket.connected()) {
            JSONObject pos = new JSONObject();
            try {
                pos.put("x", playerX);
                pos.put("y", playerY);
                socket.emit("updatePosition", pos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose() {
        if (socket != null) {
            socket.disconnect();
            socket.close();
        }
        batch.dispose();
        playerTexture.dispose();
    }

}
