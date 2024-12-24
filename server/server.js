const io = require('socket.io')(9595);

let players = {}; // 存储所有玩家的位置

io.on('connection', (socket) => {
    console.log('A client connected:', socket.id);

    // 初始化新玩家位置
    players[socket.id] = { x: 0, y: 0 };

    // 通知新玩家当前所有玩家的位置
    socket.emit('init', players);

    // 广播新玩家加入
    socket.broadcast.emit('newPlayer', { id: socket.id, x: 0, y: 0 });

    // 监听位置更新
    socket.on('move', (data) => {
        if (players[socket.id]) {
            players[socket.id].x = data.x;
            players[socket.id].y = data.y;

            // 广播给所有玩家
            socket.broadcast.emit('update', { id: socket.id, x: data.x, y: data.y });
        }
    });

    // 处理断开连接
    socket.on('disconnect', () => {
        console.log('A client disconnected:', socket.id);
        delete players[socket.id];

        // 通知其他玩家
        socket.broadcast.emit('removePlayer', socket.id);
    });
});

console.log('Server running on port 9595');