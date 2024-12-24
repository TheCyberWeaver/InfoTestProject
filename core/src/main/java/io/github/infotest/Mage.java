package io.github.infotest;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Mage extends Player {

    public Mage(String name, int maxHealthPoints, float x, float y, float speed) {
        super(name, maxHealthPoints, x, y, speed);
    }

    @Override
    public void castSkill() {
        // 法师技能示例：火球术
        // 这里可以写释放火球的逻辑，比如生成一个火球对象并让它飞行
        System.out.println(name + "释放了火球术！");
        // ... 具体逻辑
    }

    @Override
    public void render(Batch batch) {
        // 可以在这里添加特效，例如火焰粒子效果
        super.render(batch);
    }
}

