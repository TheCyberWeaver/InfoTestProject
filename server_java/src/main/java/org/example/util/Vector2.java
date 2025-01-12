package org.example.util;

public class Vector2 {
    float x=0;
    float y=0;
    public Vector2(Vector2 v) {
        x=v.x;
        y=v.y;
    }
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
