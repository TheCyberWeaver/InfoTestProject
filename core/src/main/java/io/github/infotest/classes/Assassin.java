package io.github.infotest.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;

public class Assassin extends Player {

    public Assassin(String name, Vector2 playerPosition, Texture t) {
        super(name, "Assassin",50, playerPosition, 150);
        setTexture(t);
    }

    @Override
    public void castSkill() {
        // 刺客技能示例：瞬步或背刺
        System.out.println(name + "进行了背刺！");
        // ... 具体逻辑
    }

    @Override
    public void render(Batch batch) {
        // 可以在这里添加粒子效果或特殊动画
        super.render(batch);
    }
}
