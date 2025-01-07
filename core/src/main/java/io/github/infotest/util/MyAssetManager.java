package io.github.infotest.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class MyAssetManager {
    public final AssetManager manager=new AssetManager();
    public MyAssetManager(){

    }
    public void loadLoadingScreen(){
        manager.load("assassin.png", Texture.class);
        manager.finishLoading();
    }
    public Texture getLoadingScreenTexture(){
        return manager.get("assassin.png", Texture.class);
    }

    public void loadMapAssets(){
        manager.load("normal_block.jpg",Texture.class);
        manager.load("grass_block.jpg",Texture.class);
        manager.load("stone_block.png",Texture.class);
        manager.load("basicWood.png",Texture.class);
        manager.finishLoading();
    }
    public Texture[] getMapAssets(){
        Texture[] textures=new Texture[4];
        textures[0]=manager.get("normal_block.jpg",Texture.class);
        textures[1]=manager.get("grass_block.jpg",Texture.class);
        textures[2]=manager.get("stone_block.png",Texture.class);
        textures[3]=manager.get("basicWood.png",Texture.class);
        return textures;
    }
    public void loadPlayerAssets(){
        manager.load("assassin.png", Texture.class);
        manager.finishLoading();
    }
    public Texture getPlayerAssets(){
        return manager.get("assassin.png", Texture.class);
    }

    public void loadFireballAssets(){
        manager.load("fireball_sheet_start.png", Texture.class);
        manager.load("fireball_sheet_fly.png", Texture.class);
        manager.load("fireball_sheet_endTime.png", Texture.class);
        manager.load("fireball_sheet_endHit.png", Texture.class);
        manager.finishLoading();
    }
    public Texture[] getFireballAssets(){
        Texture[] fireball_sheets=new Texture[4];
        fireball_sheets[0]=manager.get("fireball_sheet_start.png", Texture.class);
        fireball_sheets[1]=manager.get("fireball_sheet_fly.png", Texture.class);
        fireball_sheets[2]=manager.get("fireball_sheet_endTime.png", Texture.class);
        fireball_sheets[3]=manager.get("fireball_sheet_endHit.png", Texture.class);
        return fireball_sheets;
    }

    public void loadHealthBarAssets(){
        manager.load("healthbar_full_start.png", Texture.class);
        manager.load("healthbar_empty_start.png", Texture.class);
        manager.load("healthbar_full_middle.png", Texture.class);
        manager.load("healthbar_empty_middle.png", Texture.class);
        manager.finishLoading();
    }
    public Texture[] getHealthBarAssets(){
        Texture[] healthbar = new Texture[4];
        healthbar[0]=manager.get("healthbar_full_start.png", Texture.class);
        healthbar[1]=manager.get("healthbar_empty_start.png", Texture.class);
        healthbar[2]=manager.get("healthbar_full_middle.png", Texture.class);
        healthbar[3]=manager.get("healthbar_empty_middle.png", Texture.class);
        return healthbar;
    }

    public void loadManaBarAssets(){
        manager.load("manabar_full_start.png", Texture.class);
        manager.load("manabar_empty_start.png", Texture.class);
        manager.load("manabar_full_middle.png", Texture.class);
        manager.load("manabar_empty_middle.png", Texture.class);
        manager.finishLoading();
    }
    public Texture[] getManaBarAssets(){
        Texture[] mana_bar = new Texture[4];
        mana_bar[0]=manager.get("manabar_full_start.png", Texture.class);
        mana_bar[1]=manager.get("manabar_empty_start.png", Texture.class);
        mana_bar[2]=manager.get("manabar_full_middle.png", Texture.class);
        mana_bar[3]=manager.get("manabar_empty_middle.png", Texture.class);
        return mana_bar;
    }
}
