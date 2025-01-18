package io.github.infotest.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.infotest.util.MyAssetManager;

public class Apple extends Item {
    public String color;

    public Apple(MyAssetManager assetManager) {
        super("apple","just an apple", assetManager.manager.get("item/apple.png"));
    }

}
