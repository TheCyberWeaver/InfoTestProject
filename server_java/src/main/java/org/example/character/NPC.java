package org.example.character;


import org.example.item.Item;
import org.example.util.Vector2;

import java.util.ArrayList;

public class NPC {

    public String name;
    // 1.: Gender: 0 = male, 1 = female
    // 2.: Type: 0-7 NPC Type
    public int gender;
    public int type;
    //public ArrayList<Item> market;

    public int maxHP;
    public int hp;
    public Vector2 position;


    public NPC(String name, int maxHealthPoints, Vector2 startPosition, int gender, int type) {
        this.name = name;
        this.maxHP = maxHealthPoints;
        this.hp = maxHealthPoints;
        this.position = new Vector2(startPosition);
        this.gender = gender%2;
        this.type = type%8;
    }


    @Override
    public String toString() {
        return "NPC: " + name;
    }

}
