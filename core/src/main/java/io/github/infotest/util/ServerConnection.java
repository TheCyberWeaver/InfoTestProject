package io.github.infotest.util;

import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.character.NPC;
import io.github.infotest.item.Item;
import io.github.infotest.util.DataObjects.NPCData;
import io.github.infotest.util.DataObjects.PlayerData;
import io.github.infotest.util.Factory.NPCFactory;
import io.github.infotest.util.Factory.PlayerFactory;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

import static io.github.infotest.MainGameScreen.*;


/**
 * maintain connection to server and other player's information
 */
public class ServerConnection {
    private Socket socket;
    private final String serverUrl;
    private String mySocketId="";
    // key is socketId
    // value is player object

    private final String clientVersion;


    private final MyAssetManager assetManager;

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
                        //Logger.log("[ServerConnection Debug]: "+args[0].toString());
                        JSONObject data = (JSONObject) args[1];
                        Logger.log("[ServerConnection INFO] Received init data: " + data);
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
            });

            socket.on("updateAllPlayers", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0  && hasInitializedMap) {
                        String updatedPlayersJson;
                        if(args[0].toString().equals("updateAllPlayers")){  //DO NOT try to optimize any of this code, only god and I knew how it worked.
                            updatedPlayersJson = args[1].toString();
                        }else{
                            updatedPlayersJson = args[0].toString();
                        }
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
                    if(args.length > 0 && hasInitializedMap){
                        // DO NOT optimize any of this code, only god knew why it worked.
                        // Therefore, if you are trying to optimize this routine, and it fails (most surely),
                        // please increase this counter as a warning for the next person:
                        // total_hours_wasted_here = 5
                        String updatedNPCsJson;
                        if(args[0].toString().equals("updateAllNPCs")){
                            updatedNPCsJson = args[1].toString();
                        }else{
                            updatedNPCsJson = args[0].toString();
                        }

                        //Logger.log("[ServerConnection Debug]: "+updatedNPCsJson);
                        //   - key: socketId (e.g., "socketId_1")
                        //   - value: <NPCData>
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<NPCData>>(){}.getType();
                        ArrayList<NPCData> NPCsList = gson.fromJson(updatedNPCsJson, type);

                        updateNPCs(NPCsList);
                    }
                    else {
                        Logger.log("[ServerConnection ERROR]: Received updateAllNPCs event with invalid data");
                    }


                }
            }).on("deathBroadcastMessage", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof JSONObject && hasInitializedMap) {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            showDeathMessage(data);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).on("playerLeft", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof String && hasInitializedMap) {
                        String leftPlayerId = (String) args[0];
                        //Logger.log("[ServerConnection Debug]: left Playerid "+leftPlayerId);
                        allPlayers.remove(leftPlayerId);
                    }
                }
            }).on("playerAction", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof JSONObject && hasInitializedMap) {
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
            //e.printStackTrace();
        }
    }
    private void initClient(JSONObject data) {
        GLOBAL_SEED = (int) data.get("seed");
        //Logger.log("[ServerConnection INFO]: Global seed : " + GLOBAL_SEED);
        // call back
        if (seedListener != null) {
            seedListener.onSeedReceived(GLOBAL_SEED);
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

        Player player = allPlayers.get(playerID);
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
            case "PlayerDeath":
                player.kill();
                break;
            case "PlayerShowMessage":
                String message = data.getString("message");
                player.showMessage(message);
                break;
            default:
                Logger.log("[SeverConnection Warning]: received Action not Known: " + actionType);
                break;
        }

    }
    private void showDeathMessage (JSONObject data) throws JSONException {
        String deathMessage = data.getString("deathMessage");
        String targetId   = data.getString("targetId");

        if (deathMessage != null) {
            if (allPlayers.get(deathMessage)!=null) {
                Player attacker= allPlayers.get(deathMessage);
                String attackerName= attacker.getName();
                Player deadPlayer = allPlayers.get(targetId);
                if(deadPlayer != null){
                    String deadPlayerName = deadPlayer.getName();
                    uiLayer.showDeathMessage(attackerName, deadPlayerName);
                }

            }
            else{
                String deadPlayerName = allPlayers.get(targetId).getName();
                uiLayer.showDeathMessage(deathMessage, deadPlayerName);
            }
        }
        else{
            Logger.log("[SeverConnection Error]: DeathMessage is null");
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


            Player player = allPlayers.get(socketId);
            if (player == null) {

                player = PlayerFactory.createPlayer(playerData.id,playerData.name,playerData.classtype, new Vector2(x, y), assetManager);

                if(player!=null){
                    allPlayers.put(socketId, player);
                }

            } else {
                // Old Player - update position
                player.updateTargetPosition(new Vector2(x, y));
                player.updateHPFromPlayerData((float)playerData.hp);
                player.updateItems(playerData.itemIDs, assetManager);
                player.updateRotationFromPlayerData(playerData.rotation.x,playerData.rotation.y);
                player.updateisAlive(playerData.isAlive);
                player.updateGold(playerData.gold);
                //Logger.log("[Serverconnection Debug]: "+playerData.gold);
                //Logger.log("[INFO]: Player Rotation update " + playerData.rotation.x+" "+playerData.rotation.y);
            }
        }
    }
    private void updateNPCs(ArrayList<NPCData> NPCsMap){
        // playersMap 中每一个 key 都是一个 socketId，
        // value 则是对应的 PlayerData 对象
        for (NPCData npcData : NPCsMap) {
            boolean found = false;
            for(NPC npc : allNPCs){
                if (npcData.id.equals(npc.id)){
                    found = true;
                    npc.updateItems(npcData.itemIDs);
                    break;
                }
            }
            if (!found) {
//                Logger.log("Debug:"+socketId+" | "+playerData.name);
                float x = npcData.position.x;
                float y = npcData.position.y;
                NPC npc = NPCFactory.createNPC(npcData.id, npcData.name,npcData.maxHP,new Vector2(x,y),npcData.gender,npcData.type, npcData.marketTextureID,assetManager);
                npc.updateItems(npcData.itemIDs);
                allNPCs.add(npc);
            }
        }
    }

    public void sendPlayerDeath(Player player){
        JSONObject actionData = new JSONObject();
        try {
            actionData.put("actionType", "PlayerDeath");
            actionData.put("targetId", player.id);
            //Logger.log("[ServerConnection INFO]: sendPlayerDeath actionData: " + actionData);
            //skillData.put("damage", damage);

            socket.emit("playerAction", actionData);


        } catch (JSONException e) {
            e.printStackTrace();
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
            initData.put("item",player.getItems());

            Logger.log("[ServerConnection INFO]: Sending Init Data: "+ initData);

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
            skillData.put("targetId", player.id);
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
    public void sendShowPlayerMessage(Player player, String message){
        JSONObject ShowPlayerMessageeData = new JSONObject();
        try {
            String id= player.id;
            if(id!=null){
                ShowPlayerMessageeData.put("actionType", "PlayerShowMessage");
                ShowPlayerMessageeData.put("targetId", id);
                ShowPlayerMessageeData.put("message", message);
                socket.emit("playerAction", ShowPlayerMessageeData);
            }
            else{
                Logger.log("[ServerConnection Error]: sendShowPlayerMessage: key is null"+player.getName());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void sendPlayerTradeWithNPC(NPC npc, Item item){
        JSONObject tradeWithNPCData = new JSONObject();
        try {
            tradeWithNPCData.put("NPCID", npc.id);
            tradeWithNPCData.put("itemID", item.id);
            socket.emit("playerTradeWithNPC", tradeWithNPCData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void sendPlayerUpdateGold(Player player){
        JSONObject playerUpdateGoldData = new JSONObject();
        try {
            playerUpdateGoldData.put("gold", player.getGold()+"");
            socket.emit("playerUpdateGold", playerUpdateGoldData);

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
    public String getMySocketId() {
        return mySocketId;
    }
}


