package io.github.infotest.util;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import io.github.infotest.character.Player;
import io.github.infotest.character.NPC;
import io.github.infotest.util.DataObjects.NPCData;
import io.github.infotest.util.DataObjects.PlayerData;
import io.github.infotest.util.Factory.NPCFactory;
import io.github.infotest.util.Factory.PlayerFactory;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.ArrayList;
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
    private String mySocketId="";
    // key is socketId
    // value is player object
    private HashMap<String, Player> players = new HashMap<>();
    private ArrayList<NPC> npcs = new ArrayList<>();

    private String clientVersion;

    private int globalSeed = 0;

    // TODO
    private MyAssetManager assetManager;

    public interface SeedListener {
        void onSeedReceived(int seed);
    }

    private SeedListener seedListener;
    public void setSeedListener(SeedListener listener) {
        this.seedListener = listener;
    }

    public ServerConnection(String serverUrl, MyAssetManager assetManager, String clientVersion) {
        this.serverUrl = serverUrl;
        this.assetManager = assetManager;
        this.clientVersion = clientVersion;
    }

    public void connect() {
        try {
            socket = IO.socket(serverUrl);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Logger.log("[ServerConnection INFO]: Connected to server");
                }
            }).on("yourId", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 1 && args[1] instanceof String) {
                        mySocketId = args[1].toString();
                        Logger.log("[ServerConnection INFO]: My socket ID: " + mySocketId);
                        //Logger.log("[ServerConnection INFO]: "+(String)args[1]);
                    }
                }
            }).on("init", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 1 && args[1] instanceof JSONObject) {
                        Logger.log("[ServerConnection Debug]: "+args[0].toString());
                        JSONObject data = (JSONObject) args[1];
                        Logger.log("[ServerConnection INFO] Received init data: " + data.toString());
                        try{
                            initClient(data);
                        }catch (Exception e){
                            Logger.log("[ServerConnection ERROR]: Failed to parse init data: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    else {
                        Logger.log("[ServerConnection ERROR]: Received init event with invalid data");
                    }
                }
            }).on("updateAllPlayers", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        String updatedPlayersJson = args[0].toString();
                        // Logger.log("[ServerConnection Debug]: "+updatedPlayersJson);
                        //   - key: socketId (e.g., "socketId_1")
                        //   - value: <PlayerData>
                        Gson gson = new Gson();
                        Type typeOfHashMap = new TypeToken<Map<String, PlayerData>>(){}.getType();
                        Map<String, PlayerData> playersMap = gson.fromJson(updatedPlayersJson, typeOfHashMap);

                        updatePlayers(playersMap);
                    }
                    else {
                        Logger.log("[ServerConnection ERROR]: Received updateAllPlayers event with invalid data");
                    }


                }
            }).on("updateAllNPCs", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
//                    if (args.length > 0 && args[0] instanceof JSONObject) {
                    if(args.length > 0){
                        String updatedNPCsJson = args[0].toString();
                        //Logger.log("[ServerConnection Debug]: "+updatedNPCsJson);
                        //   - key: socketId (e.g., "socketId_1")
                        //   - value: <NPCData>
                        Gson gson = new Gson();
                        Type typeOfHashMap = new TypeToken<ArrayList<NPCData>>(){}.getType();
                        ArrayList<NPCData> NPCsList = gson.fromJson(updatedNPCsJson, typeOfHashMap);

                        updateNPCs(NPCsList);
                    }
                    else {
                        Logger.log("[ServerConnection ERROR]: Received updateAllNPCs event with invalid data");
                    }


                }
            }).on("playerLeft", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 1 && args[1] instanceof String) {
                        String leftPlayerId = (String) args[1];
                        players.remove(leftPlayerId);
                    }
                }
            }).on("playerAction", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            //Logger.log("[ServerConnection INFO]: " + data.toString());
                            doPlayerAction(data);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).on("loggingINFO", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 1 && args[1] instanceof String) {
                        String text = (String) args[1];
                        Logger.log("[Server Warning]: " + text);
                        //TODO: Write server warnings into a logging file
                    }
                }
            });

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    private void initClient(JSONObject data) {
        globalSeed = (int) data.get("seed");
        Logger.log("[INFO]: Global seed : " + globalSeed);
        // call back
        if (seedListener != null) {
            seedListener.onSeedReceived(globalSeed);
        }

        String serverVersion = (String) data.get("serverVersion");
        if (serverVersion.equals(clientVersion)) {
            Logger.log("[ServerConnection INFO]: All Up to Date. Server&Client  Version: " + serverVersion);
        }
        else{
            int[] client = parseVersion(clientVersion); // [clientMajor, clientMinor]
            int[] server = parseVersion(serverVersion); // [serverMajor, serverMinor]

            int clientMajor = client[0];
            int clientMinor = client[1];
            int serverMajor = server[0];
            int serverMinor = server[1];

            if (clientMajor < serverMajor) {
                // Major version is behind
                Logger.log("[ServerConnection ERROR]: Client Major version is behind, Client is not compatible");
                Logger.log("[ServerConnection ERROR]: Server Version: " + serverVersion + "| Client Version: " + clientVersion);
                Logger.log("[ServerConnection ERROR]: Shutting Down the Client");
            } else if (clientMajor == serverMajor && clientMinor < serverMinor) {
                // Minor version is behind
                Logger.log("[ServerConnection WARNING]: Client Minor version is behind. There might be bugs not discovered. Pls consider updating your client", true );
                Logger.log("[ServerConnection WARNING]: Server Version: " + serverVersion + "| Client Version: " + clientVersion);
            } else {
                // Otherwise: client is ahead
                Logger.log("[ServerConnection WARNING]: Server Version is behind");
                Logger.log("[ServerConnection WARNING]: Server Version: " + serverVersion + "| Client Version: " + clientVersion);
            }
        }
    }
    private void doPlayerAction(JSONObject data) throws JSONException {
        String actionType = data.getString("actionType");
        String playerID   = data.getString("playerID");
        Player player = players.get(playerID);
        if(player == null ){
            Logger.log("[ServerConnection Warning]: Cannot find player with id " + playerID);
            return;
        }
        if(playerID.equals(mySocketId)){
            return;
        }
        switch (actionType) {
            case "Fireball":
                player.castSkill(1, this);
                //Logger.log("[INFO]: Fireball triggered"+player.getName());
                break;
            case "TakeDamage":
                float damage = Float.parseFloat(data.getString("damage"));
                player.takeDamage(damage);
                Logger.log("[ServerConnection INFO]: Taking damage of " + damage);
                break;
            default:
                Logger.log("[SeverConnection Warning]: received Action not Known: " + actionType);
                break;
        }

    }

    private void updatePlayers(Map<String, PlayerData> playersMap){
        // playersMap 中每一个 key 都是一个 socketId，
        // value 则是对应的 PlayerData 对象
        for (Map.Entry<String, PlayerData> entry : playersMap.entrySet()) {
            String socketId = entry.getKey();
            PlayerData playerData = entry.getValue();
//            Logger.log("Debug:"+socketId+" | "+playerData.name);
            float x = (float)playerData.position.x;
            float y = (float)playerData.position.y;

            if (socketId.equals(mySocketId)) {
                continue;
            }
            Player player = players.get(socketId);
            if (player == null) {

                player = PlayerFactory.createPlayer(playerData.id,playerData.name,playerData.classtype, new Vector2(x, y), assetManager);

                if(player!=null){players.put(socketId, player);}

            } else {
                // Old Player - update position
                player.updateTargetPosition(new Vector2(x, y));
                player.updateHPFromPlayerData((float)playerData.hp);
                player.updateItemFromPlayerData(playerData.items, assetManager);
                player.updateRotationFromPlayerData(playerData.rotation.x,playerData.rotation.y);
                //Logger.log("[INFO]: Player Rotation update " + playerData.rotation.x+" "+playerData.rotation.y);
            }
        }
    }
    private void updateNPCs(ArrayList<NPCData> NPCsMap){
        // playersMap 中每一个 key 都是一个 socketId，
        // value 则是对应的 PlayerData 对象
        npcs=new ArrayList<>();
        for (NPCData npcData : NPCsMap) {
//            Logger.log("Debug:"+socketId+" | "+playerData.name);
            float x = (float)npcData.position.x;
            float y = (float)npcData.position.y;

            NPC npc = NPCFactory.createNPC(npcData.name,npcData.maxHP,new Vector2(x,y),npcData.gender,npcData.type,assetManager);
            npcs.add(npc);
        }
    }



    public void sendPlayerPosition(float x, float y, float Rx, float Ry) {
        //if (socket == null || !socket.connected()) return;
        JSONObject pos = new JSONObject();
        try {
            pos.put("x", x);
            pos.put("y", y);
            pos.put("Rx", Rx);
            pos.put("Ry", Ry);
            socket.emit("updatePosition", pos);
            //Logger.log("Updated position: " + pos);
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

            Logger.log("[ServerConnection INFO]: Init Data: "+initData.toString());

            socket.emit("init", initData);
            //Logger.log("Updated position: " + pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void sendCastSkill(Player player, String skillName){
        JSONObject skillData = new JSONObject();
        try {
            skillData.put("actionType", skillName);
            skillData.put("targetId", "");
            //skillData.put("damage", damage);

            socket.emit("playerAction", skillData);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void sendTakeDamage(Player player, float damage){
        JSONObject takeDamageData = new JSONObject();
        try {
            String id= player.id;
            if(id!=null){
                takeDamageData.put("actionType", "TakeDamage");
                takeDamageData.put("targetId", id);
                takeDamageData.put("damage", damage);
                socket.emit("playerAction", takeDamageData);
            }
            else{
                Logger.log("[ServerConnection Error]: sendTakeDamage: key is null"+player.getName());
            }

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

    /**
     * Converts "vX.Y" into [X, Y].
     * Author: ChatGPT o1
     */
    private static int[] parseVersion(String version) {
        // Remove 'v' prefix if present
        String pureVersion = version.startsWith("v") ? version.substring(1) : version;
        // Split by '.'
        String[] parts = pureVersion.split("\\.");

        int major = 0;
        int minor = 0;
        if (parts.length > 0) {
            major = Integer.parseInt(parts[0]);
        }
        if (parts.length > 1) {
            minor = Integer.parseInt(parts[1]);
        }

        return new int[]{major, minor};
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }
    public ArrayList<NPC> getNPCs() {
        return npcs;
    }
    public int getGlobalSeed() {
        return globalSeed;
    }
    public String getMySocketId() {
        return mySocketId;
    }
}


