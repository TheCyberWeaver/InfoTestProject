package io.github.infotest.util;

import io.github.infotest.classes.*;
import io.github.infotest.classes.Class;

public class ClassFactory {
    public static Class createClass(String klassName) {
        Class klasse;
        switch (klassName) {
            case "Archer":
                klasse = new Archer();
                break;
            case "Assassin":
                klasse = new Assassin();
                break;
            case "Defender":
                klasse = new Defender();
                break;
            case "Healer":
                klasse = new Healer();
                break;
            case "Mage":
                klasse = new Mage();
                break;
            case "Paladin":
                klasse = new Paladin();
                break;
            default:
                klasse = new Archer();
                break;
        }
        return klasse;
    }
}
