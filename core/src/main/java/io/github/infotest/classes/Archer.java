package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;
import io.github.infotest.util.ServerConnection;

public class Archer extends Player {
    private static float arrowCost = 3f;
    private static float arrowDamage = 3f;
    private static float arrowCooldown = 0.5f;
    private static float arrowSpeed = 3f;
    private static float arrowLT = 2f; // lifetime with 0.5 second on start and 0.7 s on hit and 0.8 on end without hit

    public Archer(String id,String name, Vector2 playerPosition, Texture t) {
        super(id, name, "Archer",100, 50, 50, playerPosition, 200, t);
    }

    @Override
    public void castSkill(int skillID, ServerConnection serverConnection) {

    }

    @Override
    public String toString() {
        return "Archer";
    }
}
