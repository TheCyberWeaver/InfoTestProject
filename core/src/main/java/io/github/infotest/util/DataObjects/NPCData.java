package io.github.infotest.util.DataObjects;

import io.github.infotest.item.Item;

import java.util.ArrayList;

public class NPCData {
    public String id;
    public String name;
    public int gender;
    public int type;
    public int marketTextureID;
    public int hp;
    public int maxHP;
    public MyVector2 position;
    public ArrayList<String> itemIDs;

    public static class MyVector2 {
        public float x;
        public float y;
    }
}
