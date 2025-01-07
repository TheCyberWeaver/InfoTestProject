package io.github.infotest.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.infotest.character.NPC;

public class NPCFactory {
    public static NPC createNPC(String npcName, Vector2 pos, int gender, int type) {
        gender = gender % 2;
        type = type % 8;

        if (gender == 0 && type == 7){
            type = 6;
        }

        Texture t;
        if (gender == 0){

        }

    }
}
