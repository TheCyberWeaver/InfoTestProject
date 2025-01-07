package io.github.infotest.util.Factory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.NPC;
import io.github.infotest.util.MyAssetManager;

public class NPCFactory {
    public static NPC createNPC(String npcName, int maxHP, Vector2 pos, int gender, int type, MyAssetManager assetManager) {
        return new NPC(npcName, maxHP, pos, 100, gender, type, assetManager);
    }
}
