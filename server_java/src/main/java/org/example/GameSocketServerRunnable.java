package org.example;
//Author: GPT o1

public class GameSocketServerRunnable implements Runnable {
    private boolean running = true;

    @Override
    public void run() {
        // 这里直接调用 GameSocketServer 的 runServer()
        GameSocketServer.runServer();
        // 注意：如果 GameSocketServer.runServer() 方法不会阻塞，你可能需要自己加一个循环等待
        // 或者保持这个线程的阻塞，直到 running = false
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopServer() {
        running = false;
        // 这里再去调用 GameSocketServer.stopServer() 以确保真正停止逻辑
        GameSocketServer.stopServer();
    }
}
