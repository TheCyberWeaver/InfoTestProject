package com.example;

public class ActionData {
    private String actionType;
    private String targetId; // 可能需要，用于指示对谁攻击
    private int damage;

    public ActionData() {}

    public String getActionType() {
        return actionType;
    }

    public String getTargetId() {
        return targetId;
    }

    public int getDamage() {
        return damage;
    }
}
