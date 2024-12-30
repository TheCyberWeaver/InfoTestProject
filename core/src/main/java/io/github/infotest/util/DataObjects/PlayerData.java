package io.github.infotest.util.DataObjects;

public class PlayerData {
    public String id;
    public String name;
    public Position position; // 这里的 Position 是一个内部类
    public String[] items;
    public int hp;
    public String classtype;

    public static class Position {
        public double x;
        public double y;
    }
}
