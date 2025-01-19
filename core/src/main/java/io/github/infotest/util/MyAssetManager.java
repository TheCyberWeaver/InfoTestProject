package io.github.infotest.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class MyAssetManager {
    public final AssetManager manager=new AssetManager();
    public MyAssetManager(){

    }
    public void loadLoadingScreen(){
        manager.load("ui/loadingscreen.png", Texture.class);
    }
    public Texture getLoadingScreenTexture(){
        return manager.get("ui/loadingscreen.png", Texture.class);
    }

    public void loadMapAssets(){
        manager.load("worldTexture/Tile1.png",Texture.class);
        manager.load("worldTexture/Tile2.png",Texture.class);
        manager.load("worldTexture/Tile3.png",Texture.class);
        manager.load("worldTexture/Tile4.png",Texture.class);
        manager.load("worldTexture/Tile5.png",Texture.class);
    }
    public Texture[] getMapAssets(){
        Texture[] textures=new Texture[5];
        textures[0]=manager.get("worldTexture/Tile1.png",Texture.class);
        textures[1]=manager.get("worldTexture/Tile2.png",Texture.class);
        textures[2]=manager.get("worldTexture/Tile3.png",Texture.class);
        textures[3]=manager.get("worldTexture/Tile4.png",Texture.class);
        textures[4]=manager.get("worldTexture/Tile5.png",Texture.class);
        return textures;
    }

    public void loadPlayerAssets(){
        manager.load("assassin.png", Texture.class);
    }
    public Texture getPlayerAssets(){
        return manager.get("assassin.png", Texture.class);
    }

    public void loadMageAssets(){
        manager.load("player/mage/Attack1.png",Texture.class);
        manager.load("player/mage/Death.png",Texture.class);
        manager.load("player/mage/Hit.png",Texture.class);
        manager.load("player/mage/Idle.png",Texture.class);
        manager.load("player/mage/Run.png",Texture.class);
    }
    public Texture[] getMageAssets(){
        Texture[] mageTextures=new Texture[5];
        mageTextures[0]=manager.get("player/mage/Attack1.png",Texture.class);
        mageTextures[1]=manager.get("player/mage/Death.png",Texture.class);
        mageTextures[2]=manager.get("player/mage/Hit.png",Texture.class);
        mageTextures[3]=manager.get("player/mage/Idle.png",Texture.class);
        mageTextures[4]=manager.get("player/mage/Run.png",Texture.class);
        return mageTextures;
    }

    public void loadFireballAssets(){
        manager.load("fireball_sheet_start.png", Texture.class);
        manager.load("fireball_sheet_fly.png", Texture.class);
        manager.load("fireball_sheet_endTime.png", Texture.class);
        manager.load("fireball_sheet_endHit.png", Texture.class);
    }
    public Texture[] getFireballAssets(){
        Texture[] fireball_sheets=new Texture[4];
        fireball_sheets[0]=manager.get("fireball_sheet_start.png", Texture.class);
        fireball_sheets[1]=manager.get("fireball_sheet_fly.png", Texture.class);
        fireball_sheets[2]=manager.get("fireball_sheet_endTime.png", Texture.class);
        fireball_sheets[3]=manager.get("fireball_sheet_endHit.png", Texture.class);
        return fireball_sheets;
    }
    public void loadFireballSymbol(){
        manager.load("player/mage/mageFireballSymbol.png", Texture.class);
    }
    public Texture getFireballSymbol(){
        return manager.get("player/mage/mageFireballSymbol.png", Texture.class);
    }
    public void loadSkillBarAsset(){
        manager.load("ui/skillbar.png", Texture.class);
    }
    public Texture getSkillBarAsset(){
        return manager.get("ui/skillbar.png", Texture.class);
    }
    public void loadGoldBarAsset(){
        manager.load("ui/goldbar.png", Texture.class);
    }
    public Texture getGoldBarAsset(){
        return manager.get("ui/goldbar.png", Texture.class);
    }
    public void loadHealthBarAssets(){
        manager.load("healthbar_full_start.png", Texture.class);
        manager.load("healthbar_empty_start.png", Texture.class);
        manager.load("healthbar_full_middle.png", Texture.class);
        manager.load("healthbar_empty_middle.png", Texture.class);
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
    }
    public Texture[] getManaBarAssets(){
        Texture[] mana_bar = new Texture[4];
        mana_bar[0]=manager.get("manabar_full_start.png", Texture.class);
        mana_bar[1]=manager.get("manabar_empty_start.png", Texture.class);
        mana_bar[2]=manager.get("manabar_full_middle.png", Texture.class);
        mana_bar[3]=manager.get("manabar_empty_middle.png", Texture.class);
        return mana_bar;
    }

    public void loadAusdauerBarAssets(){
        manager.load("ausdauerbar_full_start.png", Texture.class);
        manager.load("ausdauerbar_empty_start.png", Texture.class);
        manager.load("ausdauerbar_full_middle.png", Texture.class);
        manager.load("ausdauerbar_empty_middle.png", Texture.class);
    }
    public Texture[] getAusdauerBarAssets(){
        Texture[] ausdauerbar = new Texture[4];
        ausdauerbar[0]=manager.get("ausdauerbar_full_start.png", Texture.class);
        ausdauerbar[1]=manager.get("ausdauerbar_empty_start.png", Texture.class);
        ausdauerbar[2]=manager.get("ausdauerbar_full_middle.png", Texture.class);
        ausdauerbar[3]=manager.get("ausdauerbar_empty_middle.png", Texture.class);
        return ausdauerbar;
    }

    public void loadNPCMaleAssets(){
        manager.load("NPC_Male_1.png", Texture.class);
        manager.load("NPC_Male_2.png", Texture.class);
        manager.load("NPC_Male_3.png", Texture.class);
        manager.load("NPC_Male_4.png", Texture.class);
        manager.load("NPC_Male_5.png", Texture.class);
        manager.load("NPC_Male_6.png", Texture.class);
        manager.load("NPC_Male_7.png", Texture.class);
        manager.load("NPC_Male_8.png", Texture.class);
    }
    public Texture[] getNPCMaleAssets(){
        Texture[] npcmale = new Texture[8];
        npcmale[0]=manager.get("NPC_Male_1.png", Texture.class);
        npcmale[1]=manager.get("NPC_Male_2.png", Texture.class);
        npcmale[2]=manager.get("NPC_Male_3.png", Texture.class);
        npcmale[3]=manager.get("NPC_Male_4.png", Texture.class);
        npcmale[4]=manager.get("NPC_Male_5.png", Texture.class);
        npcmale[5]=manager.get("NPC_Male_6.png", Texture.class);
        npcmale[6]=manager.get("NPC_Male_7.png", Texture.class);
        npcmale[7]=manager.get("NPC_Male_8.png", Texture.class);
        return npcmale;
    }

    public void loadNPCWomenAssets(){
        manager.load("NPC_Women_1.png", Texture.class);
        manager.load("NPC_Women_2.png", Texture.class);
        manager.load("NPC_Women_3.png", Texture.class);
        manager.load("NPC_Women_4.png", Texture.class);
        manager.load("NPC_Women_5.png", Texture.class);
        manager.load("NPC_Women_6.png", Texture.class);
        manager.load("NPC_Women_7.png", Texture.class);
        manager.load("NPC_Women_8.png", Texture.class);
    }
    public Texture[] getNPCWomenAssets(){
        Texture[] npcwomen = new Texture[8];
        npcwomen[0]=manager.get("NPC_Women_1.png", Texture.class);
        npcwomen[1]=manager.get("NPC_Women_2.png", Texture.class);
        npcwomen[2]=manager.get("NPC_Women_3.png", Texture.class);
        npcwomen[3]=manager.get("NPC_Women_4.png", Texture.class);
        npcwomen[4]=manager.get("NPC_Women_5.png", Texture.class);
        npcwomen[5]=manager.get("NPC_Women_6.png", Texture.class);
        npcwomen[6]=manager.get("NPC_Women_7.png", Texture.class);
        npcwomen[7]=manager.get("NPC_Women_8.png", Texture.class);
        return npcwomen;
    }

    public void loadNPCMarketAssets(){
        manager.load("NPC/market/klein.png", Texture.class);
        manager.load("NPC/market/kiste.png", Texture.class);
        manager.load("NPC/market/besondereKiste.png", Texture.class);
        manager.load("NPC/market/tasche.png", Texture.class);
        manager.load("NPC/market/koffer.png", Texture.class);
        manager.load("NPC/market/besonders.png", Texture.class);
    }
    public Texture[] getNPCMarketAssets(){
        Texture[] npcmarket = new Texture[6];
        npcmarket[0]=manager.get("NPC/market/klein.png", Texture.class);
        npcmarket[1]=manager.get("NPC/market/kiste.png", Texture.class);
        npcmarket[2]=manager.get("NPC/market/besondereKiste.png", Texture.class);
        npcmarket[3]=manager.get("NPC/market/tasche.png", Texture.class);
        npcmarket[4]=manager.get("NPC/market/koffer.png", Texture.class);
        npcmarket[5]=manager.get("NPC/market/besonders.png", Texture.class);
        return npcmarket;

    }

    public void loadSignsAssets(){
        manager.load("Ur_Inv_Is_Full_Sign.png", Texture.class);
    }
    public Texture getSignsAssets(){
        return manager.get("Ur_Inv_Is_Full_Sign.png", Texture.class);
    }

    public void loadArrowAssets(){
        manager.load("arrow.png", Texture.class);
    }
    public Texture getArrowAssets(){
        return manager.get("arrow.png", Texture.class);
    }

    public void loadItemBarAssets(){
        manager.load("ui/itembar.png", Texture.class);
    }
    public Texture getItemBarAssets(){
        return manager.get("ui/itembar.png", Texture.class);
    }
    public void loadItemAssets(){
        manager.load("item/apple.png", Texture.class);
    }

}
