package io.github.infotest.util.DataObjects;

public class NPCData {
    public String name;
    public int gender;
    public int type;
    public int hp;
    public int maxHP;
    public MyVector2 position;

    public static class MyVector2 {
        public float x;
        public float y;
    }
}
