package io.github.infotest.item;

import com.badlogic.gdx.graphics.Texture;
import io.github.infotest.util.MyAssetManager;

public abstract class Item {
    public String name; // name des Items
    public String description;
    private Texture texture; // Texture, wie das Item im Inventory/ market aussieht // ItemTexture: 14x14 Pixel

    public Item(String name, String description, Texture texture) {
        this.name = name;
        this.texture = texture;
    }

    public void drop(float x, float y) {
        //TODO
    }
    public Item pickUp(){
        //TODO
        return this;
    }

    public Texture getTexture() {
        return texture;
    }
}
