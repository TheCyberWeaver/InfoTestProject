package io.github.infotest.util.DataObjects;

public class PlayerData {
    public String id;
    public String name;
    public Position position;
    public String[] items;
    public double hp;
    public String classtype;
    public int level;
    public double mana;
    public Rotation rotation;

    public static class Position {
        public double x;
        public double y;
    }
    public static class Rotation {
        public double x;
        public double y;
    }
}
