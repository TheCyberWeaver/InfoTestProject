package io.github.infotest.util.Factory;

import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Gegner;
import io.github.infotest.util.MyAssetManager;

public class GegnerFactory {
    public static Gegner createGegner(String name, int maxHP, Vector2 pos, int gender, MyAssetManager assetManager) {
        // Serverconnection: Gegner attributes: name, pos, gender(0 || 1), type(0-7), startItems für market;
        // WICHTIG: alle 5 oder 6 min market updaten mit neuen Items auf allen Clients
        return new Gegner(maxHP, pos,100, assetManager.getTestPlayerAssets(), 100);
    }
}
