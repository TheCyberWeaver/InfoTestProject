package org.example.character;

import java.util.ArrayList;
import java.util.List;

/**
 * Player class represents a game character with various attributes and actions
 */
public class Player {
    /**
     * The socket ID associated with this player
     */
    private String id;

    /**
     * The player's name
     */
    public String name;

    /**
     * The player's current position
     */
    public Position position;

    /**
     * The items that the player is currently carrying
     */
    public List<String> items;

    /**
     * The player's hit points
     */
    public float hp;

    /**
     * The player's classType
     */
    public String classtype;

    public float mana;
    public int level;

    /**
     * rotation, for example, angle or direction
     */
    public Position rotation;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.position = new Position(0, 0);
        this.rotation = new Position(0, 0);
        this.items = new ArrayList<>();
        this.hp = 100;
        this.classtype = "";
        this.level = 0;
        this.mana = 0;
    }

    public String getId() {
        return id;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public void setRotation(float rx, float ry) {
        this.rotation.x = rx;
        this.rotation.y = ry;
    }

    public void setMana(float m) {
        this.mana = m;
    }

    public void setLevel(int l) {
        this.level = l;
    }

    public void pickItem(String item) {
        items.add(item);
    }

    public void dropItem(String item) {
        items.remove(item);
    }

    public void takeDamage(float damage) {
        this.hp -= damage;
    }

    public void professionAttack(Player target, float damage) {
        target.hp -= damage;
    }
}


class Position {
    public float x;
    public float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
