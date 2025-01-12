package io.github.infotest.item;

import io.github.infotest.util.MyAssetManager;

public class Geld extends Item {
    public String color;

    public Geld(MyAssetManager assetManager) {
        super("geld","just geld", assetManager.getLoadingScreenTexture());
    }
    public void render(){

    }
}
