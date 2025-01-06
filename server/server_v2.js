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
    
    if(newPlayer){
      console.log('[INFO]: ',newPlayer.name, newPlayer.classtype , " joins the world");
    }
    else{
      console.log('[ERROR]: ',socket.id, "Player cannot be created, NULL Error");
      socket.emit('loggingINFO',"Player cannot be created after reviecing init from Client, NULL Error");
    }
    /**
   * Notify all clients to update the player list when a new player joins
   */
    io.emit('updateAllPlayers', players);
    //console.log(`[INFO]: Player ${socket.id} (${data.name}) initialized.`);

  });

  /**
   * When a client disconnects, remove the player from the collection
   */
  socket.on('disconnect', () => {
    const player = players[socket.id];
    //console.log('[INFO]: ',player.name, player.classtype , " leaves the world");

    delete players[socket.id];
    io.emit('playerLeft', socket.id);
    
    //console.log('[INFO]: A client disconnected:', socket.id);
  });

  /**
   * Handle player position updates
   */
  socket.on('updatePosition', (data) => {
    const player = players[socket.id];
    if (player) {
      player.setPosition(data.x, data.y);
      player.setRotation(data.Rx, data.Ry);
      // Sync all player data
      io.emit('updateAllPlayers', players);
    }
    else{
      socket.emit('loggingINFO',"Position cannot be updated. Player is not in playerlist");
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

  

  socket.on('playerAction', (data) => {
    const player = players[socket.id];
    const actionType = data.actionType;
    if (player) {
        switch (actionType) {
            case "Normal":
              {
                    
              }
              break;

            case "AOE":
              {
                        
              }
              break;
        
            case "Continuous":
              //TODO: other types of attack
              break;
            case "Fireball":
              {
                  io.emit('playerAction', {
                      playerID: player.id,
                      actionType: "Fireball",
                  });
                  console.log("[Debug]: ",player.name," casts a Fireball");
              }
              
              break;
            case "TakeDamage":
              {
                  const target = players[data.targetId];
                  const damage = data.damage;
                  
                  if(target){
                    target.takeDamage(damage);
                    io.emit('takeDamage', {
                      playerID: data.targetId,
                      actionType: "TakeDamage",
                      damage:damage,
                    });
                    console.log("[Debug]: ",target.name," takes ",damage," Damage");
                  }
                  else{
                    console.log("[Debug]: target is NULL");
                  }                 
              }
              
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
