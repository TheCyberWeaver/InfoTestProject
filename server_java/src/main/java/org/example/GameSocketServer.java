package org.example;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.*;
import com.google.gson.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSocketServer {

    /**
     * 固定seed
     */
    private static final int seed = 114514;
    private static final String serverVersion = "v2.1";

    /**
     * 存放所有连接的玩家
     */
    private static final Map<String, Player> players = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // 1. 配置服务器
        Configuration config = new Configuration();
        // 对应 server.listen(9595, '0.0.0.0');
        config.setHostname("0.0.0.0");
        config.setPort(9595);

        // 2. 创建服务器对象
        final SocketIOServer server = new SocketIOServer(config);
        System.out.println("[Info]: Starting server...");
        // 3. 当有客户端连接时
        server.addConnectListener(client -> {
            // client.getSessionId() 对应 socket.id
            String socketId = client.getSessionId().toString();
            // 发送 yourId 事件，让客户端记住自己的 socketId
            client.sendEvent("yourId", socketId);

            // 将 seed 等信息通过 init 事件广播给所有连接的客户端
            // 原 Node.js 中是 io.emit('init', {...})
            // 这里我们也可以选择给所有客户端发，也可以只在 connect 时发给当前用户
            // 为了和原本逻辑更一致，这里演示广播
            server.getBroadcastOperations().sendEvent("init", new InitData(seed, serverVersion));
        });

        // 4. 当收到 "init" 事件时，创建新玩家
        server.addEventListener("init", Object.class, (client, data, ackSender) -> {
            String socketId = client.getSessionId().toString();

            // data 就是客户端发来的 JSON
            // Node.js 里是 {x, y, name, hp, classtype, items} ...
            // 这里用 Gson 解析
            Gson gson = new Gson();
            JsonObject json = gson.toJsonTree(data).getAsJsonObject();

            String name       = json.get("name").getAsString();
            float x           = json.get("x").getAsFloat();
            float y           = json.get("y").getAsFloat();
            float hp          = json.get("hp").getAsFloat();
            String classtype  = json.get("classtype").getAsString();
            JsonArray items   = json.get("items").getAsJsonArray();

            // 创建 Player
            Player newPlayer = new Player(socketId, name);
            newPlayer.hp = hp;
            newPlayer.classtype = classtype;
            newPlayer.setPosition(x, y);

            // 从 JSON 数组中取 item
            for (int i=0; i<items.size(); i++){
                newPlayer.pickItem(items.get(i).getAsString());
            }

            players.put(socketId, newPlayer);
            System.out.println("[INFO]: " + newPlayer.name + " " + newPlayer.classtype + " joins the world");

            // 通知所有客户端更新玩家列表
            broadcastAllPlayers(server);
        });

        // 5. 当客户端断开连接时
        server.addDisconnectListener(client -> {
            String socketId = client.getSessionId().toString();
            Player p = players.get(socketId);
            if (p != null) {
                System.out.println("[INFO]: " + p.name + " " + p.classtype + " leaves the world");
            }
            players.remove(socketId);

            // 给所有客户端发 playerLeft 事件
            server.getBroadcastOperations().sendEvent("playerLeft", socketId);
        });

        // 6. 玩家坐标更新
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

                // 通知所有客户端更新玩家列表
                broadcastAllPlayers(server);
            } else {
                // 给当前客户端发日志
                client.sendEvent("loggingINFO", "Position cannot be updated. Player is not in playerlist");
            }
        });

        // 7. 处理 pickItem
        server.addEventListener("pickItem", String.class, (client, item, ackSender) -> {
            String socketId = client.getSessionId().toString();
            Player player = players.get(socketId);
            if (player != null) {
                player.pickItem(item);
                // 通知所有客户端
                server.getBroadcastOperations().sendEvent("playerPickedItem",
                        new PickDropData(socketId, player.items));
            }
        });

        // 8. 处理 dropItem
        server.addEventListener("dropItem", String.class, (client, item, ackSender) -> {
            String socketId = client.getSessionId().toString();
            Player player = players.get(socketId);
            if (player != null) {
                player.dropItem(item);
                server.getBroadcastOperations().sendEvent("playerDroppedItem",
                        new PickDropData(socketId, player.items));
            }
        });

        // 9. 处理各种玩家行为
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
                case "Continuous":
                    // TODO
                    break;
                case "Fireball":
                    // 广播告诉其他人，某个玩家施放了 Fireball
                    server.getBroadcastOperations().sendEvent("playerAction",
                            new ActionData(socketId, "Fireball"));
                    System.out.println("[Debug]: " + player.name + " casts a Fireball");
                    break;
                case "TakeDamage":
                    // data 中获取 targetId, damage
                    String targetId = json.get("targetId").getAsString();
                    float damage = json.get("damage").getAsFloat();
                    Player target = players.get(targetId);
                    if (target != null) {
                        target.takeDamage(damage);
                        // 通知所有客户端
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

        // 10. 启动服务器
        server.start();
        System.out.println("Server is running on port 9595");
        // 为了防止主线程退出，这里阻塞一下
        // 可以改成更优雅的方式
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        server.stop();
    }

    /**
     * 给所有客户端推送最新的玩家列表
     */
    private static void broadcastAllPlayers(SocketIOServer server) {
        server.getBroadcastOperations().sendEvent("updateAllPlayers", players);
    }

    /**
     * 用于向客户端发送初始信息
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
     * 用于 pickItem & dropItem 的数据体
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
     * 玩家行为（比如 Fireball）广播给其他客户端
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
     * TakeDamage 专用
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
