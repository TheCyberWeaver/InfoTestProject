package io.github.infotest.classes;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.Player;

public class Healer extends Player {

    public Healer(String name, Vector2 initialPosition, Texture texture) {
        super(name, "Healer", 100, 100 ,initialPosition, 50, texture);
    }

    @Override
    public void castSkill(int skillID) {

    }
}
