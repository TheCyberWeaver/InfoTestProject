package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Player class represents a game character with various attributes and actions
 */
public class Player {
    public String id;
    public String name;
    public Position position = new Position(0, 0);
    public List<String> items = new ArrayList<>();
    public int hp = 100;
    public String classtype = "";
    public int level = 0;
    public int mana = 0;
    public Rotation rotation = new Rotation(0, 0);

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setPosition(double x, double y) {
        this.position.x = x;
        this.position.y = y;
    }

    public void setRotation(double x, double y){
        this.rotation.x = x;
        this.rotation.y = y;
    }

    public void pickItem(String item) {
        this.items.add(item);
    }

    public void dropItem(String item) {
        this.items.remove(item);
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
    }

    // 你也可以加上 setMana, setLevel, professionAttack() 等方法
    // ...
}

// 简单封装坐标
class Position {
    public double x;
    public double y;
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

// 简单封装旋转
class Rotation {
    public double x;
    public double y;
    public Rotation(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
