package org.example;
/*
 * Author: Thomas Lu
 * Translated by: ChatGPT o1
 */
import com.corundumstudio.socketio.*;
import com.google.gson.*;
import org.example.character.Player;
import org.example.character.NPC;
import org.example.util.MapCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSocketServer {

    /**
     * fixed seed:114514 (Meaning of this number could be found on internet)
     */
    private static final int seed = 114514;
    private static final String serverVersion = "v3.1";

    // Map data

    public static final int CELL_SIZE = 32;
    public static final int MAP_SIZE = 1000;
    public static int numOfValidTextures = 4;
    public static int[][] GAME_MAP=new int[MAP_SIZE][MAP_SIZE];

    //NPC
    private static ArrayList<NPC> npcs=new ArrayList<NPC>();
    /**
     * Stores all connected players
     */
    private static final Map<String, Player> players = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // 0. Initialize the Server
        serverInitialization();

        // 1. Configure the server
        Configuration config = new Configuration();
        // Corresponds to server.listen(9595, '0.0.0.0');
        config.setHostname("0.0.0.0");
        config.setPort(9595);

        // 2. Create the server object
        final SocketIOServer server = new SocketIOServer(config);
        System.out.println("[Info]: Starting server...");
        // 3. When a client connects
        server.addConnectListener(client -> {
            // client.getSessionId() corresponds to socket.id
            String socketId = client.getSessionId().toString();
            // Send the yourId event so the client can remember its socketId
            client.sendEvent("yourId", socketId);

            // Broadcast seed and other info to all connected clients via the 'init' event
            // Originally in Node.js, it was io.emit('init', {...})
            // Here we can also choose to send to all clients, or only to the user upon connection
            // To keep consistent with the original logic, we demonstrate broadcasting
            Map<String, Object> initData = new HashMap<>();
            initData.put("seed", seed);
            initData.put("serverVersion", serverVersion);
            //server.getBroadcastOperations().sendEvent("init", initData);
            client.sendEvent("init", initData);
        });

        // 4. Create a new player upon receiving the "init" event
        server.addEventListener("init", Object.class, (client, data, ackSender) -> {
            String socketId = client.getSessionId().toString();

            // data is the JSON sent by the client
            // In Node.js, it is {x, y, name, hp, classtype, items} ...
            // Here we use Gson to parse
            Gson gson = new Gson();
            JsonObject json = gson.toJsonTree(data).getAsJsonObject();

            String name       = json.get("name").getAsString();
            float x           = json.get("x").getAsFloat();
            float y           = json.get("y").getAsFloat();
            float hp          = json.get("hp").getAsFloat();
            String classtype  = json.get("classtype").getAsString();
            JsonArray items   = json.get("items").getAsJsonArray();

            // Create a Player
            Player newPlayer = new Player(socketId, name);
            newPlayer.hp = hp;
            newPlayer.classtype = classtype;
            newPlayer.setPosition(x, y);

            // Retrieve items from the JSON array
//            for (int i=0; i<items.size(); i++){
//                newPlayer.pickItem(items.get(i).getAsString());
//            }
            players.put(socketId, newPlayer);
            System.out.println("[INFO]: " + newPlayer.name + " " + newPlayer.classtype + " joins the world");

            // Notify all clients to update the player list
            broadcastAllPlayers(server);
        });

        // 5. When a client disconnects
        server.addDisconnectListener(client -> {
            String socketId = client.getSessionId().toString();
            Player p = players.get(socketId);
            if (p != null) {
                System.out.println("[INFO]: " + p.name + " " + p.classtype + " leaves the world");
            }
            players.remove(socketId);

            // Send the playerLeft event to all clients
            server.getBroadcastOperations().sendEvent("playerLeft", socketId);
        });

        // 6. Player coordinates update
        server.addEventListener("updatePosition", Object.class, (client, data, ackSender) -> {
            String socketId = client.getSessionId().toString();
            Player player = players.get(socketId);
            if (player != null) {
                Gson gson = new Gson();
                JsonObject json = gson.toJsonTree(data).getAsJsonObject();
                float x = json.get("x").getAsFloat();
                float y = json.get("y").getAsFloat();
                float rx = json.get("Rx").getAsFloat();
                float ry = json.get("Ry").getAsFloat();

                player.setPosition(x, y);
                player.setRotation(rx, ry);

                // Notify all clients to update the player list
                broadcastAllPlayers(server);
            } else {
                // Send a log message to the current client
                client.sendEvent("loggingINFO", "Position cannot be updated. Player is not in playerlist");
            }
        });

        // 7. Handle pickItem
        server.addEventListener("pickItem", String.class, (client, item, ackSender) -> {
            String socketId = client.getSessionId().toString();
            Player player = players.get(socketId);
            if (player != null) {
                player.pickItem(item);
                // Notify all clients
                server.getBroadcastOperations().sendEvent("playerPickedItem",
                    new PickDropData(socketId, player.itemIDs));
            }
        });

        // 8. Handle dropItem
        server.addEventListener("dropItem", String.class, (client, item, ackSender) -> {
            String socketId = client.getSessionId().toString();
            Player player = players.get(socketId);
            if (player != null) {
                player.dropItem(item);
                server.getBroadcastOperations().sendEvent("playerDroppedItem",
                    new PickDropData(socketId, player.itemIDs));
            }
        });

        // 9. Handle various player actions
        server.addEventListener("playerAction", Object.class, (client, data, ackSender) -> {
            String socketId = client.getSessionId().toString();
            Player player = players.get(socketId);
            if (player == null) {
                return;
            }
            Gson gson = new Gson();
            JsonObject json = gson.toJsonTree(data).getAsJsonObject();
            String actionType = json.get("actionType").getAsString();

            switch (actionType) {
                case "Normal":
                    // TODO
                    break;
                case "AOE":
                    // TODO
                    break;
                case "PlayerDeath":
                    String playerId = json.get("targetId").getAsString();
                    Player target1 = players.get(playerId);
                    if (target1 != null) {
                        target1.isAlive=false;
                        // Notify all clients
                        server.getBroadcastOperations().sendEvent("playerAction",
                            new ActionData(playerId, "PlayerDeath"));
                    } else {
                        System.out.println("[Debug]: target is NULL");
                    }


                    System.out.println("[Debug]: " + player.name + " is dead");
                    break;
                case "Fireball":
                    // Broadcast to others that some player cast a Fireball
                    server.getBroadcastOperations().sendEvent("playerAction",
                        new ActionData(socketId, "Fireball"));
                    System.out.println("[Debug]: " + player.name + " casts a Fireball");
                    break;
                case "TakeDamage":
                    // Retrieve targetId and damage from data
                    String targetId = json.get("targetId").getAsString();
                    float damage = json.get("damage").getAsFloat();
                    Player target = players.get(targetId);
                    if (target != null) {
                        target.takeDamage(damage);
                        // Notify all clients
                        server.getBroadcastOperations().sendEvent("takeDamage",
                            new TakeDamageData(targetId, "TakeDamage", damage));
                        System.out.println("[Debug]: " + target.name + " takes " + damage + " Damage");
                    } else {
                        System.out.println("[Debug]: target is NULL");
                    }
                    break;
                default:
                    // Unknown attack type
                    client.sendEvent("errorMsg", "Unknown attack type: " + actionType);
                    break;
            }
        });

        // 10. Start the server
        server.start();
        System.out.println("Server is running on port 9595");
        // To prevent the main thread from exiting, block here
        // This can be changed to a more elegant way
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        server.stop();
    }
    private static void serverInitialization(){
        MapCreator mapCreator=new MapCreator(seed);
        mapCreator.initializePerlinNoiseMap();
        npcs = mapCreator.spawnNPCs();
    }
    /**
     * Push the latest player list to all clients
     */
    private static void broadcastAllPlayers(SocketIOServer server) {
        server.getBroadcastOperations().sendEvent("updateAllPlayers", players);
        server.getBroadcastOperations().sendEvent("updateAllNPCs", npcs);
    }


    /**
     * Used to send initial information to the client
     */
    static class InitData {
        public int seed;
        public String serverVersion;

        public InitData(int seed, String serverVersion){
            this.seed = seed;
            this.serverVersion = serverVersion;
        }
    }

    /**
     * Data object for pickItem & dropItem
     */
    static class PickDropData {
        public String playerId;
        public Object items;

        public PickDropData(String playerId, Object items) {
            this.playerId = playerId;
            this.items = items;
        }
    }

    /**
     * Broadcast player actions (like Fireball) to other clients
     */
    static class ActionData {
        public String playerID;
        public String actionType;

        public ActionData(String playerID, String actionType){
            this.playerID = playerID;
            this.actionType = actionType;
        }
    }

    /**
     * Specialized for TakeDamage
     */
    static class TakeDamageData {
        public String playerID;
        public String actionType;
        public float damage;

        public TakeDamageData(String playerID, String actionType, float damage){
            this.playerID = playerID;
            this.actionType = actionType;
            this.damage = damage;
        }
    }
}
