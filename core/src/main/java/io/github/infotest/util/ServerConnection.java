package io.github.infotest.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.classes.Assassin;
import io.github.infotest.character.Player;
import io.github.infotest.classes.Mage;
import io.github.infotest.util.DataObjects.PlayerData;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * maintain connection to server and other player's information
 */
public class ServerConnection {
    private Socket socket;
    private final String serverUrl;
    private String mySocketId;
    // key is socketId
    // value is player object
    private HashMap<String, Player> players = new HashMap<>();

    private int globalSeed = 0;

    // TODO
    private Texture testTexture;

    public interface SeedListener {
        void onSeedReceived(int seed);
    }

    private SeedListener seedListener;
    public void setSeedListener(SeedListener listener) {
        this.seedListener = listener;
    }

    public ServerConnection(String serverUrl, Texture testTexture) {
        this.serverUrl = serverUrl;
        this.testTexture = testTexture;
    }

    public void connect() {
        try {
            socket = IO.socket(serverUrl);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("[INFO]: Connected to server");
                }
            }).on("yourId", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof String) {
                        mySocketId = (String) args[0];
                        System.out.println("[INFO]: My socket ID: " + mySocketId);
                    }
                }
            }).on("init", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String updatedPlayersJson = args[0].toString();

                    //   - key: socketId (e.g., "socketId_1")
                    //   - value: <PlayerData>
                    Gson gson = new Gson();
                    Type typeOfHashMap = new TypeToken<Map<String, PlayerData>>(){}.getType();
                    Map<String, PlayerData> playersMap = gson.fromJson(updatedPlayersJson, typeOfHashMap);

                    updatePlayers(playersMap);
                }
            }).on("updateAllPlayers", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String updatedPlayersJson = args[0].toString();

                    //   - key: socketId (e.g., "socketId_1")
                    //   - value: <PlayerData>
                    Gson gson = new Gson();
                    Type typeOfHashMap = new TypeToken<Map<String, PlayerData>>(){}.getType();
                    Map<String, PlayerData> playersMap = gson.fromJson(updatedPlayersJson, typeOfHashMap);

                    updatePlayers(playersMap);

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
                        //System.out.println("[INFO]: Global seed : " + globalSeed);

                        // call back
                        if (seedListener != null) {
                            seedListener.onSeedReceived(globalSeed);
                        }
                    }
                }
            });

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayers(Map<String, PlayerData> playersMap){
        // playersMap 中每一个 key 都是一个 socketId，
        // value 则是对应的 PlayerData 对象
        for (Map.Entry<String, PlayerData> entry : playersMap.entrySet()) {
            String socketId = entry.getKey();
            PlayerData playerData = entry.getValue();

            float x = (float)playerData.position.x;
            float y = (float)playerData.position.y;

            if (socketId.equals(mySocketId)) {
                continue;
            }
            Player player = players.get(socketId);
            if (player == null) {

                player = PlayerFactory.createPlayer(playerData.name,playerData.classtype, new Vector2(x, y), testTexture);

                if(player!=null){players.put(socketId, player);}

            } else {
                // Old Player - update position
                player.updateTargetPosition(new Vector2(x, y));
                player.updateHPFromPlayerData((float)playerData.hp);
                player.updateItemFromPlayerData(playerData.items);
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

    public void sendPlayerInit(Player player) {
        JSONObject initData = new JSONObject();
        try {
            initData.put("x", player.getX());
            initData.put("y", player.getY());
            initData.put("name", player.getName());
            initData.put("hp",player.getHealthPoints());
            initData.put("classtype",player.getClassName());
            initData.put("items",player.getItems());
            System.out.println("----------");
            System.out.println(initData.toString());

            socket.emit("init", initData);
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


    public HashMap<String, Player> getPlayers() {
        return players;
    }


    public int getGlobalSeed() {
        return globalSeed;
    }
    public String getMySocketId() {
        return mySocketId;
    }
}


