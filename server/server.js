// server.js
const express = require('express');
const http = require('http');
const { Server } = require('socket.io');

/**
 * Import the Player class
 */
const Player = require('./Player');

const app = express();
const server = http.createServer(app);
const io = new Server(server);

/**
 * We use a fixed seed for demonstration
 */
const seed = 14353434;

/**
 * A collection to keep track of all connected players
 */
const players = {};

io.on('connection', (socket) => {
    //console.log('[INFO]: A client connected:', socket.id);

    // Send socket.id to the client
    socket.emit('yourId', socket.id);

    socket.emit('initializeSeed', seed);
    // Create a new Player instance for the connecting socket
    // players[socket.id] = new Player(socket.id, `Player_${socket.id}`);

    socket.on('init', (data) => {

        const newPlayer = new Player(socket.id, data.name);
        newPlayer.name=data.name;
        newPlayer.setPosition(data.x,data.y);
        newPlayer.hp=data.hp;
        newPlayer.classtype=data.classtype;
        newPlayer.items=data.items;

        players[socket.id] = newPlayer;


        socket.emit('init', players);
        // Sync seed to client


        console.log('[INFO]: ',newPlayer.name, newPlayer.classtype , " joins the world");
        /**
         * Notify all clients to update the player list when a new player joins
         */
        io.emit('updateAllPlayers', players);
        console.log(`[INFO]: Player ${socket.id} (${data.name}) initialized.`);

    });

    /**
     * When a client disconnects, remove the player from the collection
     */
    socket.on('disconnect', () => {
        delete players[socket.id];
        io.emit('playerLeft', socket.id);
        console.log('[INFO]: ',newPlayer.name, newPlayer.classtype , " leaves the world");
        //console.log('[INFO]: A client disconnected:', socket.id);
    });

    /**
     * Handle player position updates
     */
    socket.on('updatePosition', (data) => {
        const player = players[socket.id];
        if (player) {
            player.setPosition(data.x, data.y);
            // Sync all player data
            io.emit('updateAllPlayers', players);
        }
    });

    /**
     * Handle when a player picks up an item
     */
    socket.on('pickItem', (item) => {
        const player = players[socket.id];
        if (player) {
            player.pickItem(item);
            // Notify all clients about the change
            io.emit('playerPickedItem', {
                playerId: socket.id,
                items: player.items,
            });
        }
    });

    /**
     * Handle when a player drops an item
     */
    socket.on('dropItem', (item) => {
        const player = players[socket.id];
        if (player) {
            player.dropItem(item);
            // Notify all clients about the change
            io.emit('playerDroppedItem', {
                playerId: socket.id,
                items: player.items,
            });
        }
    });



    socket.on('playerAttack', (data) => {
        const attacker = players[data.attackerId];
        const attackType = data.attackType;
        if (attacker && target) {
            switch (attackType) {
                case "Normal":
                {
                    const target = players[data.targetId];
                    const damage = player[data.damage];

                    attacker.normalAttack(target,damage);

                    io.emit('updatePlayerStatus', {
                        player: target
                    });
                    io.emit('playerAction', {
                        playerID: attacker.id,
                        actionType: "normalAttack",
                    });
                }

                    break;

                case "AOE":

                {
                    const damage = data.damage || 0;
                    const targetIds = data.targets || [];  // array of target playerIds

                    targetIds.forEach((tid) => {
                        const target = players[tid];
                        if (target) {
                            attacker.professionAttack(target,damage);
                            target.hp -= damage;
                            // Broadcast an update for each target
                            io.emit('updatePlayerStatus', {
                                player: player
                            });
                        }
                    });
                    io.emit('playerAction', {
                        playerID: attacker.id,
                        actionType: 'professionAttack',
                    });

                }
                    break;

                case "Continuous":
                    //TODO: other types of attack
                    break;

                default:
                    // Unknown attack type
                    socket.emit('errorMsg', {
                        msg: `Unknown attack type: ${attackType}`
                    });
                    break;
            }
        }
    });
});

server.listen(9595, '0.0.0.0', () => {
    console.log('Server is running on port 9595');
});
