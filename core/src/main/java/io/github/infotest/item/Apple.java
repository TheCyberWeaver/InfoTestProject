package io.github.infotest.item;

import io.github.infotest.util.MyAssetManager;

public class Apple extends Item {
    public String color;

    public Apple(MyAssetManager assetManager) {
        super("apple","just an apple", assetManager.getLoadingScreenTexture());
    }
    public void render(){

    }
}
