package io.github.infotest.util;

import java.util.ArrayList;

public class ToxicLines {
    public static String getToxicLines() {
        ArrayList<String> toxicLines = new ArrayList<>();
        toxicLines.add("Loser, loser, chicken cruiser!");
        toxicLines.add("Better luck next flop!");
        toxicLines.add("Defeat complete, next time compete!");
        toxicLines.add("L for legend... wait, nope, just L!");
        toxicLines.add("Oops, better stick to Tetris!");
        toxicLines.add("Congrats, you're the MVP-Most Vanquished Player!");
        toxicLines.add("Game over, but at least your outfit slays!");
        toxicLines.add("Second place is just the first loser!");
        toxicLines.add("Uninstall and rethink life choices!");
        toxicLines.add("Well, someone had to lose-thanks for taking one for the team!");

        return toxicLines.get((int)(Math.random()*toxicLines.size()));
    }
}
