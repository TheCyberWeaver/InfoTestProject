package io.github.infotest.item;

public abstract class Item {
    public String name;
    public String description;
    public Item(String name, String description) {
        this.name = name;
    }

    public void drop(float x, float y) {
        //TODO
    }
    public Item pickUp(){
        //TODO
        return this;
    }
}
