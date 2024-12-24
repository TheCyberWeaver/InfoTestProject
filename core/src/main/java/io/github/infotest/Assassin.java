package io.github.infotest;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Assassin extends Player {

    public Assassin(String name, int maxHealthPoints, float x, float y, float speed,Texture t) {
        super(name, maxHealthPoints, x, y, speed);
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
