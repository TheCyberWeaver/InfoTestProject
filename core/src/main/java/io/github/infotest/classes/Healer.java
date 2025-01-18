package io.github.infotest.classes;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.ServerConnection;

public class Healer extends Player {

    public Healer(String id, String name, Vector2 initialPosition, Texture t) {
        super(id, name, "Healer", 50, 100, 50, initialPosition, 200);
        setTexture(t);
    }

    @Override
    public void castSkill(int skillID, ServerConnection serverConnection) {

    }

    @Override
    public Texture getMainSkillSymbol() {
        return null;
    }
}
