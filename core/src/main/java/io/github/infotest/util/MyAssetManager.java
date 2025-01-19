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
        manager.load("worldTexture/TreeTile.png",Texture.class);
        manager.load("worldTexture/TreeTile_snow.png",Texture.class);
        manager.load("worldTexture/Tile4.png",Texture.class);
        manager.load("worldTexture/Tile5.png",Texture.class);
    }
    public Texture[] getMapAssets(){
        Texture[] textures=new Texture[6];
        textures[0]=manager.get("worldTexture/Tile1.png",Texture.class);
        textures[1]=manager.get("worldTexture/Tile2.png",Texture.class);
        textures[2]=manager.get("worldTexture/TreeTile.png",Texture.class);
        textures[3]=manager.get("worldTexture/TreeTile_snow.png",Texture.class);
        textures[4]=manager.get("worldTexture/Tile4.png",Texture.class);
        textures[5]=manager.get("worldTexture/Tile5.png",Texture.class);
        return textures;
    }

    public void loadMapFadeAssets(){
        manager.load("worldTexture/fade/Tile1_corner.png",Texture.class);
        manager.load("worldTexture/fade/Tile1_bottom.png",Texture.class);
        manager.load("worldTexture/fade/Tile1_left.png",Texture.class);
        manager.load("worldTexture/fade/Tile1_right.png",Texture.class);
        manager.load("worldTexture/fade/Tile1_top.png",Texture.class);

        manager.load("worldTexture/fade/Tile2_corner.png",Texture.class);
        manager.load("worldTexture/fade/Tile2_bottom.png",Texture.class);
        manager.load("worldTexture/fade/Tile2_left.png",Texture.class);
        manager.load("worldTexture/fade/Tile2_right.png",Texture.class);
        manager.load("worldTexture/fade/Tile2_top.png",Texture.class);

        manager.load("worldTexture/fade/Tile4_corner.png",Texture.class);
        manager.load("worldTexture/fade/Tile4_bottom.png",Texture.class);
        manager.load("worldTexture/fade/Tile4_left.png",Texture.class);
        manager.load("worldTexture/fade/Tile4_right.png",Texture.class);
        manager.load("worldTexture/fade/Tile4_top.png",Texture.class);

        manager.load("worldTexture/fade/Tile5_corner.png",Texture.class);
        manager.load("worldTexture/fade/Tile5_bottom.png",Texture.class);
        manager.load("worldTexture/fade/Tile5_left.png",Texture.class);
        manager.load("worldTexture/fade/Tile5_right.png",Texture.class);
        manager.load("worldTexture/fade/Tile5_top.png",Texture.class);
    }
    public Texture[] getMapFadeAssets(){
        Texture[] textures=new Texture[20];
        textures[0]=manager.get("worldTexture/fade/Tile1_corner.png",Texture.class);
        textures[1]=manager.get("worldTexture/fade/Tile1_bottom.png",Texture.class);
        textures[2]=manager.get("worldTexture/fade/Tile1_left.png",Texture.class);
        textures[3]=manager.get("worldTexture/fade/Tile1_right.png",Texture.class);
        textures[4]=manager.get("worldTexture/fade/Tile1_top.png",Texture.class);

        textures[5]=manager.get("worldTexture/fade/Tile2_corner.png",Texture.class);
        textures[6]=manager.get("worldTexture/fade/Tile2_bottom.png",Texture.class);
        textures[7]=manager.get("worldTexture/fade/Tile2_left.png",Texture.class);
        textures[8]=manager.get("worldTexture/fade/Tile2_right.png",Texture.class);
        textures[9]=manager.get("worldTexture/fade/Tile2_top.png",Texture.class);

        textures[10]=manager.get("worldTexture/fade/Tile4_corner.png",Texture.class);
        textures[11]=manager.get("worldTexture/fade/Tile4_bottom.png",Texture.class);
        textures[12]=manager.get("worldTexture/fade/Tile4_left.png",Texture.class);
        textures[13]=manager.get("worldTexture/fade/Tile4_right.png",Texture.class);
        textures[14]=manager.get("worldTexture/fade/Tile4_top.png",Texture.class);

        textures[15]=manager.get("worldTexture/fade/Tile5_corner.png",Texture.class);
        textures[16]=manager.get("worldTexture/fade/Tile5_bottom.png",Texture.class);
        textures[17]=manager.get("worldTexture/fade/Tile5_left.png",Texture.class);
        textures[18]=manager.get("worldTexture/fade/Tile5_right.png",Texture.class);
        textures[19]=manager.get("worldTexture/fade/Tile5_top.png",Texture.class);
        return textures;
    }

    public void loadMapDecoAssets(){
        manager.load("worldTexture/deco/stone1.png",Texture.class);
        manager.load("worldTexture/deco/stone2.png",Texture.class);
        manager.load("worldTexture/deco/stone3.png",Texture.class);
        manager.load("worldTexture/deco/stone4.png",Texture.class);
        manager.load("worldTexture/deco/stone5.png",Texture.class);
        manager.load("worldTexture/deco/stone6.png",Texture.class);
        manager.load("worldTexture/deco/stone7.png",Texture.class);
        manager.load("worldTexture/deco/stone8.png",Texture.class);
        manager.load("worldTexture/deco/stone9.png",Texture.class);
        manager.load("worldTexture/deco/stone_big.png",Texture.class);
        manager.load("worldTexture/deco/fallenTree.png",Texture.class);
        manager.load("worldTexture/deco/mushroom1.png",Texture.class);
        manager.load("worldTexture/deco/mushroom2.png",Texture.class);
    }
    public Texture[] getMapDecoAssets(){
        Texture[] textures=new Texture[13];
        textures[0]=manager.get("worldTexture/deco/stone1.png",Texture.class);
        textures[1]=manager.get("worldTexture/deco/stone2.png",Texture.class);
        textures[2]=manager.get("worldTexture/deco/stone3.png",Texture.class);
        textures[3]=manager.get("worldTexture/deco/stone4.png",Texture.class);
        textures[4]=manager.get("worldTexture/deco/stone5.png",Texture.class);
        textures[5]=manager.get("worldTexture/deco/stone6.png",Texture.class);
        textures[6]=manager.get("worldTexture/deco/stone7.png",Texture.class);
        textures[7]=manager.get("worldTexture/deco/stone8.png",Texture.class);
        textures[8]=manager.get("worldTexture/deco/stone9.png",Texture.class);
        textures[9]=manager.get("worldTexture/deco/stone_big.png",Texture.class);
        textures[10]=manager.get("worldTexture/deco/fallenTree.png",Texture.class);
        textures[11]=manager.get("worldTexture/deco/mushroom1.png",Texture.class);
        textures[12]=manager.get("worldTexture/deco/mushroom2.png",Texture.class);
        return textures;
    }

    public void loadMapTreeAssets(){
        manager.load("worldTexture/deco/tree/tree.png",Texture.class);
        manager.load("worldTexture/deco/tree/tree_bottom.png",Texture.class);
        manager.load("worldTexture/deco/tree/tree_top.png",Texture.class);
        manager.load("worldTexture/deco/tree/tree_snow.png",Texture.class);
        manager.load("worldTexture/deco/tree/tree_bottom_snow.png",Texture.class);
        manager.load("worldTexture/deco/tree/tree_top_snow.png",Texture.class);
    }
    public Texture[] getMapTreeAssets(){
        Texture[] textures=new Texture[6];
        textures[0]=manager.get("worldTexture/deco/tree/tree.png",Texture.class);
        textures[1]=manager.get("worldTexture/deco/tree/tree_bottom.png",Texture.class);
        textures[2]=manager.get("worldTexture/deco/tree/tree_top.png",Texture.class);
        textures[3]=manager.get("worldTexture/deco/tree/tree_snow.png",Texture.class);
        textures[4]=manager.get("worldTexture/deco/tree/tree_bottom_snow.png",Texture.class);
        textures[5]=manager.get("worldTexture/deco/tree/tree_top_snow.png",Texture.class);
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
        manager.load("ui/healthbar/healthbar_full_start.png", Texture.class);
        manager.load("ui/healthbar/healthbar_empty_start.png", Texture.class);
        manager.load("ui/healthbar/healthbar_full_middle.png", Texture.class);
        manager.load("ui/healthbar/healthbar_empty_middle.png", Texture.class);
    }
    public Texture[] getHealthBarAssets(){
        Texture[] healthbar = new Texture[4];
        healthbar[0]=manager.get("ui/healthbar/healthbar_full_start.png", Texture.class);
        healthbar[1]=manager.get("ui/healthbar/healthbar_empty_start.png", Texture.class);
        healthbar[2]=manager.get("ui/healthbar/healthbar_full_middle.png", Texture.class);
        healthbar[3]=manager.get("ui/healthbar/healthbar_empty_middle.png", Texture.class);
        return healthbar;
    }

    public void loadManaBarAssets(){
        manager.load("ui/manabar/manabar_full_start.png", Texture.class);
        manager.load("ui/manabar/manabar_empty_start.png", Texture.class);
        manager.load("ui/manabar/manabar_full_middle.png", Texture.class);
        manager.load("ui/manabar/manabar_empty_middle.png", Texture.class);
    }
    public Texture[] getManaBarAssets(){
        Texture[] mana_bar = new Texture[4];
        mana_bar[0]=manager.get("ui/manabar/manabar_full_start.png", Texture.class);
        mana_bar[1]=manager.get("ui/manabar/manabar_empty_start.png", Texture.class);
        mana_bar[2]=manager.get("ui/manabar/manabar_full_middle.png", Texture.class);
        mana_bar[3]=manager.get("ui/manabar/manabar_empty_middle.png", Texture.class);
        return mana_bar;
    }

    public void loadAusdauerBarAssets(){
        manager.load("ui/ausdauerbar/ausdauerbar_full_start.png", Texture.class);
        manager.load("ui/ausdauerbar/ausdauerbar_empty_start.png", Texture.class);
        manager.load("ui/ausdauerbar/ausdauerbar_full_middle.png", Texture.class);
        manager.load("ui/ausdauerbar/ausdauerbar_empty_middle.png", Texture.class);
    }
    public Texture[] getAusdauerBarAssets(){
        Texture[] ausdauerbar = new Texture[4];
        ausdauerbar[0]=manager.get("ui/ausdauerbar/ausdauerbar_full_start.png", Texture.class);
        ausdauerbar[1]=manager.get("ui/ausdauerbar/ausdauerbar_empty_start.png", Texture.class);
        ausdauerbar[2]=manager.get("ui/ausdauerbar/ausdauerbar_full_middle.png", Texture.class);
        ausdauerbar[3]=manager.get("ui/ausdauerbar/ausdauerbar_empty_middle.png", Texture.class);
        return ausdauerbar;
    }

    public void loadNPCMaleAssets(){
        manager.load("NPC/male/NPC_Male_1.png", Texture.class);
        manager.load("NPC/male/NPC_Male_2.png", Texture.class);
        manager.load("NPC/male/NPC_Male_3.png", Texture.class);
        manager.load("NPC/male/NPC_Male_4.png", Texture.class);
        manager.load("NPC/male/NPC_Male_5.png", Texture.class);
        manager.load("NPC/male/NPC_Male_6.png", Texture.class);
        manager.load("NPC/male/NPC_Male_7.png", Texture.class);
        manager.load("NPC/male/NPC_Male_8.png", Texture.class);
    }
    public Texture[] getNPCMaleAssets(){
        Texture[] npcmale = new Texture[8];
        npcmale[0]=manager.get("NPC/male/NPC_Male_1.png", Texture.class);
        npcmale[1]=manager.get("NPC/male/NPC_Male_2.png", Texture.class);
        npcmale[2]=manager.get("NPC/male/NPC_Male_3.png", Texture.class);
        npcmale[3]=manager.get("NPC/male/NPC_Male_4.png", Texture.class);
        npcmale[4]=manager.get("NPC/male/NPC_Male_5.png", Texture.class);
        npcmale[5]=manager.get("NPC/male/NPC_Male_6.png", Texture.class);
        npcmale[6]=manager.get("NPC/male/NPC_Male_7.png", Texture.class);
        npcmale[7]=manager.get("NPC/male/NPC_Male_8.png", Texture.class);
        return npcmale;
    }

    public void loadNPCWomenAssets(){
        manager.load("NPC/woman/NPC_Women_1.png", Texture.class);
        manager.load("NPC/woman/NPC_Women_2.png", Texture.class);
        manager.load("NPC/woman/NPC_Women_3.png", Texture.class);
        manager.load("NPC/woman/NPC_Women_4.png", Texture.class);
        manager.load("NPC/woman/NPC_Women_5.png", Texture.class);
        manager.load("NPC/woman/NPC_Women_6.png", Texture.class);
        manager.load("NPC/woman/NPC_Women_7.png", Texture.class);
        manager.load("NPC/woman/NPC_Women_8.png", Texture.class);
    }
    public Texture[] getNPCWomenAssets(){
        Texture[] npcwomen = new Texture[8];
        npcwomen[0]=manager.get("NPC/woman/NPC_Women_1.png", Texture.class);
        npcwomen[1]=manager.get("NPC/woman/NPC_Women_2.png", Texture.class);
        npcwomen[2]=manager.get("NPC/woman/NPC_Women_3.png", Texture.class);
        npcwomen[3]=manager.get("NPC/woman/NPC_Women_4.png", Texture.class);
        npcwomen[4]=manager.get("NPC/woman/NPC_Women_5.png", Texture.class);
        npcwomen[5]=manager.get("NPC/woman/NPC_Women_6.png", Texture.class);
        npcwomen[6]=manager.get("NPC/woman/NPC_Women_7.png", Texture.class);
        npcwomen[7]=manager.get("NPC/woman/NPC_Women_8.png", Texture.class);
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
