package io.github.infotest.util.Factory;

import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.NPC;
import io.github.infotest.util.MyAssetManager;
import io.github.infotest.util.Overlay.UI_Layer;

import java.util.Collections;
import java.util.Comparator;

public class NPCFactory {
    public static NPC createNPC(String npcName, int maxHP, Vector2 pos, int gender, int type, MyAssetManager assetManager) {
        // Serverconnection: NPC attributes: name, pos, gender(0 || 1), type(0-7), startItems für market;
        // WICHTIG: alle 5 oder 6 min market updaten mit neuen Items auf allen Clients
        return new NPC(npcName, maxHP, pos, 100, gender, type, assetManager);
    }

    public static NPC createNPC(String name, double maxHP, int x, int y, MyAssetManager assetManager) {
        return null;
    }
}
