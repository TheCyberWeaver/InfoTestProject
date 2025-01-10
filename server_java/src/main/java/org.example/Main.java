package com.example;

import static spark.Spark.*;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 主服务器入口
 */
public class Main {

    // 这里直接写死你的常量
    private static final int SEED = 114514;
    private static final String SERVER_VERSION = "v2.1";

    // 保存所有连接的玩家
    private static Map<String, Player> players = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        // --- 1. 启动 Spark Java (处理一些简单的 HTTP 请求) ---
        port(8080);  // Spark 默认是 4567，这里改为 8080
        get("/", (req, res) -> "Hello from Spark Java + netty-socketio demo!");

        // --- 2. 启动 netty-socketio (处理 Socket.IO 事件) ---
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9595);

        final SocketIOServer io = new SocketIOServer(config);

        // 当客户端 socket.io 连接时
        io.addConnectListener(client -> {
            String clientId = client.getSessionId().toString();
            System.out.println("[INFO]: A client connected with sessionId: " + clientId);

            // 让客户端知道它自己的ID
            client.sendEvent("yourId", clientId);

            // 发一个 init 给所有人
            io.getBroadcastOperations().sendEvent("init", Map.of(
                "seed", SEED,
                "serverVersion", SERVER_VERSION
            ));
        });

        // 当收到 "init" 事件时（对应 JS：socket.on('init', (data) => {...})）
        io.addEventListener("init", PlayerInitData.class, (client, data, ackSender) -> {
            String clientId = client.getSessionId().toString();
            Player newPlayer = new Player(clientId, data.getName());
            newPlayer.name = data.getName();
            newPlayer.setPosition(data.getX(), data.getY());
            newPlayer.hp = data.getHp();
            newPlayer.classtype = data.getClasstype();
            newPlayer.items = data.getItems();

            players.put(clientId, newPlayer);

            System.out.println("[INFO]: " + newPlayer.name + " (" + newPlayer.classtype + ") joins the world");

            // 通知所有客户端，更新玩家列表
            io.getBroadcastOperations().sendEvent("updateAllPlayers", players);
        });

        // 当客户端断开连接时
        io.addDisconnectListener(client -> {
            String clientId = client.getSessionId().toString();
            Player p = players.get(clientId);
            if (p != null) {
                System.out.println("[INFO]: " + p.name + " (" + p.classtype + ") leaves the world");
            }
            players.remove(clientId);

            // 通知所有客户端，有玩家离开
            io.getBroadcastOperations().sendEvent("playerLeft", clientId);
        });

        // 当客户端发来 "updatePosition" 事件
        io.addEventListener("updatePosition", PositionData.class, (client, data, ackSender) -> {
            String clientId = client.getSessionId().toString();
            Player player = players.get(clientId);
            if (player != null) {
                player.setPosition(data.getX(), data.getY());
                player.setRotation(data.getRx(), data.getRy());
                // 同步给所有人
                io.getBroadcastOperations().sendEvent("updateAllPlayers", players);
            } else {
                client.sendEvent("loggingINFO", "Position cannot be updated. Player is not in playerlist");
            }
        });

        // 当客户端发来 "pickItem" 事件
        io.addEventListener("pickItem", String.class, (client, item, ackSender) -> {
            String clientId = client.getSessionId().toString();
            Player player = players.get(clientId);
            if (player != null) {
                player.pickItem(item);
                io.getBroadcastOperations().sendEvent("playerPickedItem", Map.of(
                    "playerId", clientId,
                    "items", player.items
                ));
            }
        });

        // 当客户端发来 "dropItem" 事件
        io.addEventListener("dropItem", String.class, (client, item, ackSender) -> {
            String clientId = client.getSessionId().toString();
            Player player = players.get(clientId);
            if (player != null) {
                player.dropItem(item);
                io.getBroadcastOperations().sendEvent("playerDroppedItem", Map.of(
                    "playerId", clientId,
                    "items", player.items
                ));
            }
        });

        // 当客户端发来 "playerAction" 事件（用于处理各种技能/动作）
        io.addEventListener("playerAction", ActionData.class, (client, data, ackSender) -> {
            String clientId = client.getSessionId().toString();
            Player player = players.get(clientId);
            if (player != null) {
                switch (data.getActionType()) {
                    case "Fireball": {
                        io.getBroadcastOperations().sendEvent("playerAction", Map.of(
                            "playerID", player.id,
                            "actionType", "Fireball"
                        ));
                        System.out.println("[Debug]: " + player.name + " casts a Fireball");
                        break;
                    }
                    case "TakeDamage": {
                        String targetId = data.getTargetId();
                        int damage = data.getDamage();
                        Player targetPlayer = players.get(targetId);
                        if (targetPlayer != null) {
                            targetPlayer.takeDamage(damage);
                            io.getBroadcastOperations().sendEvent("takeDamage", Map.of(
                                "playerID", targetId,
                                "actionType", "TakeDamage",
                                "damage", damage
                            ));
                            System.out.println("[Debug]: " + targetPlayer.name + " takes " + damage + " Damage");
                        } else {
                            System.out.println("[Debug]: target is NULL");
                        }
                        break;
                    }
                    default: {
                        client.sendEvent("errorMsg", Map.of(
                            "msg", "Unknown action type: " + data.getActionType()
                        ));
                    }
                }
            }
        });

        // 启动 Socket.IO 服务
        io.start();

        System.out.println("HTTP server on port 8080 (Spark) & Socket.IO server on port 9595 (netty-socketio).");
    }
}
