package io.github.infotest.util.Factory;

import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.NPC;
import io.github.infotest.util.MyAssetManager;
import io.github.infotest.util.Overlay.UI_Layer;

import java.util.Collections;
import java.util.Comparator;

public class NPCFactory {
    public static NPC createNPC(String id, String npcName, int maxHP, Vector2 pos, int gender, int type, int marketTextureID, MyAssetManager assetManager) {
        // Serverconnection: NPC attributes: name, pos, gender(0 || 1), type(0-7), startItems für market;
        // WICHTIG: alle 5 oder 6 min market updaten mit neuen Items auf allen Clients
        return new NPC(id, npcName, maxHP, pos, 100, gender, type, marketTextureID,assetManager);
    }

}
