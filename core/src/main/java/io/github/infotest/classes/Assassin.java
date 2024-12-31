package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;

public class Assassin extends Player {

    public Assassin(String name, Vector2 playerPosition, Texture t) {
        super(name, "Assassin",50, playerPosition, 150, t);
    }

    @Override
    public void castSkill() {
        // 刺客技能示例：瞬步或背刺
        System.out.println(name + "进行了背刺！");
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
    }
}
