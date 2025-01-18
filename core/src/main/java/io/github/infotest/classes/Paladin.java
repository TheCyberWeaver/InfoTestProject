package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.ServerConnection;

public class Paladin extends Player {
    public Paladin(String id, String name, Vector2 playerPosition, Texture t) {
        super(id, name, "Paladin",50, 50, 150, playerPosition, 200);
        setTexture(t);
    }
    @Override
    public void castSkill(int skillID, ServerConnection serverConnection) {

    }

    @Override
    public Texture getMainSkillSymbol() {
        return null;
    }

    @Override
    public String toString() {
        return "Paladin";
    }
}
