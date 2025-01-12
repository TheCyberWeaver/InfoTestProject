package org.example.item;


public abstract class Item {
    public String name; // name des Items

    public Item(String name) {
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
