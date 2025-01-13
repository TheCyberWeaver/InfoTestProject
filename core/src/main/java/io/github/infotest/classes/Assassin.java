package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.ServerConnection;

public class Assassin extends Player {

    public Assassin(String id,String name, Vector2 playerPosition, Texture t) {
        super(id, name, "Assassin",50, 50, 150, playerPosition, 200, t);
    }

    @Override
    public void castSkill(int skillID, ServerConnection serverConnection) {
    }

    @Override
    public void render(Batch batch, float delta) {
        super.render(batch, delta);
    }
}
