package io.github.infotest.util.DataObjects;

import java.util.ArrayList;

/*
PlayerData should only be used when receiving or sending player information to server
The name of the variables here rely on the protocol, und thus should not be edited unless the server protocol is updated.
 */
public class PlayerData {
    public String id;
    public String name;
    public Position position;
    public ArrayList<String> itemIDs;
    public double hp;
    public boolean isAlive;
    public String classtype;
    public int level;
    public double mana;
    public Rotation rotation;
    public int gold;

    public static class Position {
        public double x;
        public double y;
    }
    public static class Rotation {
        public double x;
        public double y;
    }
}
