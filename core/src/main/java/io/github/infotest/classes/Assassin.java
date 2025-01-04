package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.ServerConnection;

public class Assassin extends Player {

    public Assassin(String name, Vector2 playerPosition, Texture t) {
        super(name, "Assassin",50, 50 ,playerPosition, 150, t);
    }

    @Override
    public void castSkill(int skillID, ServerConnection serverConnection) {
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
    }
}
