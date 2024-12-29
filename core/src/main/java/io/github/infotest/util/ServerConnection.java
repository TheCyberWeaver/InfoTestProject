package io.github.infotest.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Assassin;
import io.github.infotest.character.Character;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * maintain connection to server and other player's information
 */
public class ServerConnection {
    private Socket socket;
    private final String serverUrl;
    private String mySocketId;
    // key is socketId
    // value is player object
    private HashMap<String, Character> players = new HashMap<>();

    private int globalSeed = 0;

    // TODO
    private Texture assassinTexture;

    public ServerConnection(String serverUrl, Texture assassinTexture) {
        this.serverUrl = serverUrl;
        this.assassinTexture = assassinTexture;
    }

    public void connect() {
        try {
            socket = IO.socket(serverUrl);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected to server");
                }
            }).on("yourId", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof String) {
                        mySocketId = (String) args[0];
                        System.out.println("My socket ID: " + mySocketId);
                    }
                }
            }).on("init", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
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
                    if (args.length > 0 && args[0] instanceof String) {
                        String leftPlayerId = (String) args[0];
                        players.remove(leftPlayerId);
                    }
                }
            }).on("initializeSeed", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof Integer) {
                        globalSeed = (int) args[0];
                        System.out.println("Global seed: " + globalSeed);
                    }
                }
            });

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    private void updatePlayersFromJSON(JSONObject data) {

        Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject pos = data.getJSONObject(key);
                float x = (float) pos.getDouble("x");
                float y = (float) pos.getDouble("y");

                if (key.equals(mySocketId)) {
                    //System.out.println("My socket ID: " + mySocketId);
                    continue;
                }

                Character player = players.get(key);
                if (player == null) {
                    // New Player
                    player = new Assassin(key, new Vector2(x, y), assassinTexture);
                    players.put(key, player);
                } else {
                    // Old Player - update position
                    player.updateTargetPosition(new Vector2(x, y));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendPlayerPosition(float x, float y) {
        //if (socket == null || !socket.connected()) return;
        JSONObject pos = new JSONObject();
        try {
            pos.put("x", x);
            pos.put("y", y);
            socket.emit("updatePosition", pos);
            //System.out.println("Updated position: " + pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.close();
            socket = null;
        }
    }


    public HashMap<String, Character> getPlayers() {
        return players;
    }


    public int getGlobalSeed() {
        return globalSeed;
    }
    public String getMySocketId() {
        return mySocketId;
    }
}
