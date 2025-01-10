package com.example;

import java.util.List;

/**
 * 用于解析客户端发来的 init 数据
 */
public class PlayerInitData {
    private String name;
    private double x;
    private double y;
    private int hp;
    private String classtype;
    private List<String> items;

    // 必须要有空构造函数和 getter/setter，才能被 netty-socketio 反序列化
    public PlayerInitData() {}

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getHp() {
        return hp;
    }

    public String getClasstype() {
        return classtype;
    }

    public List<String> getItems() {
        return items;
    }
}
