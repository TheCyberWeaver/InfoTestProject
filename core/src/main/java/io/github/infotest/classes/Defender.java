package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.ServerConnection;

public class Defender extends Player {

    public Defender(String id, String name, Vector2 initialPosition, Texture texture) {
        super(id, name, "Defender", 250, 25 ,initialPosition, 75,  texture);
    }

    @Override
    public void castSkill(int skillID, ServerConnection serverConnection) {

    }
}
