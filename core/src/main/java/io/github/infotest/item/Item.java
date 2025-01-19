package io.github.infotest.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.infotest.util.MyAssetManager;

public abstract class Item {
    public String id;
    public String name; // name des Items
    public String description;
    private Texture texture; // Texture, wie das Item im Inventory/ market aussieht // ItemTexture: 14x14 Pixel

    public Item(String id,String name, String description, Texture texture) {
        this.name = name;
        this.id=id;
        this.description=description;
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

    public void render(Batch batch, float x, float y,float scale) {
        batch.draw(texture, x, y, 80*scale, 80*scale);
    }
    @Override
    public String toString(){
        return id;
    }
}
