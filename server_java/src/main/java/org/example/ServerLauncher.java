package org.example;

//Author: GPT o1

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;

public class ServerLauncher {
    private JFrame frame;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea debugArea;
    private JLabel versionLabel;

    // 用于标记是否已经启动服务器
    private volatile boolean serverRunning = false;
    // 用于引用服务器线程和服务器对象
    private Thread serverThread;
    private GameSocketServerRunnable serverRunnable;

    public static void main(String[] args) {
        // 根据操作系统判断走 GUI 还是命令行
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win") || osName.contains("mac")) {
            // Windows 和 macOS 上都走 GUI
            SwingUtilities.invokeLater(() -> {
                ServerLauncher launcher = new ServerLauncher();
                launcher.createAndShowGUI();
            });
        } else {
            // Linux 等其他操作系统，走命令行模式
            System.out.println("[Info] current OS: " + osName + "using command line to start the server...");
            // 直接在命令行中启动服务器
            GameSocketServerRunnable serverRunnable = new GameSocketServerRunnable();
            Thread serverThread = new Thread(serverRunnable, "GameSocketServer-Thread");
            serverThread.start();

            // 在命令行下可以进行一些交互，比如让用户输入“stop”来停止服务器
            // 这里仅做简单示例，你可以自己扩展
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            System.out.println("input 'stop' to stop the server'");
            while (true) {
                String input = scanner.nextLine();
                if ("stop".equalsIgnoreCase(input.trim())) {
                    serverRunnable.stopServer();
                    break;
                }
            }
            System.out.println("[Info] server stopped.");
        }
    }

    /**
     * 创建并显示 GUI
     */
    private void createAndShowGUI() {
        frame = new JFrame("Endless Fighter: Game Server GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // 居中

        // 面板布局
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // 服务器版本标签
        versionLabel = new JLabel("server version: " + GameSocketServer.getServerVersion());
        versionLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(versionLabel, BorderLayout.NORTH);

        // Debug 信息输出区域
        debugArea = new JTextArea();
        debugArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(debugArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start server");
        stopButton = new JButton("Stop server");
        stopButton.setEnabled(false); // 初始时只能启动，不能停止

        // 启动按钮事件
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!serverRunning) {
                    startServer();
                }
            }
        });

        // 停止按钮事件
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverRunning) {
                    stopServer();
                }
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.setVisible(true);

        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                // 将字节转换成字符，再追加到 debugArea
                debugArea.append(String.valueOf((char) b));
            }
        });
        System.setOut(printStream);
        System.setErr(printStream);
    }

    /**
     * 启动服务器
     */
    private void startServer() {
        serverRunning = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        appendDebugMessage("[ServerLauncher Info] Starting server...");

        serverRunnable = new GameSocketServerRunnable();
        serverThread = new Thread(serverRunnable, "GameSocketServer-Thread");
        serverThread.start();

        appendDebugMessage("[ServerLauncher Info] Server started.");
    }

    /**
     * 停止服务器
     */
    private void stopServer() {
        serverRunning = false;
        stopButton.setEnabled(false);

        // 通知 serverRunnable 停止
        if (serverRunnable != null) {
            serverRunnable.stopServer();
        }
        appendDebugMessage("[Info] Stopping server...");

        // 等待线程结束
        try {
            if (serverThread != null && serverThread.isAlive()) {
                serverThread.interrupt();
                serverThread.join(2000);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        startButton.setEnabled(true);
        appendDebugMessage("[Info] server stopped.");
    }

    private void appendDebugMessage(String message) {
        // 将 debug 信息追加到文本区域
        debugArea.append(message + "\n");
        // 让滚动条自动滚动到底部
        debugArea.setCaretPosition(debugArea.getDocument().getLength());
    }
}
