package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.ServerConnection;

public class Archer extends Player {
    public Archer(String id,String name, Vector2 playerPosition, Texture t) {
        super(id, name, "Archer",100, 50 ,playerPosition, 150, t);
    }

    @Override
    public void castSkill(int skillID, ServerConnection serverConnection) {

    }

    @Override
    public String toString() {
        return "Archer";
    }
}
