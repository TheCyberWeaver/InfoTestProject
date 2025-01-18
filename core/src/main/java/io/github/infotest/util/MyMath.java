package io.github.infotest.util;

import com.badlogic.gdx.math.Vector2;

public class MyMath {
    /**
     * Die Methode prüft, ob sich point_2 im aufgespannten Rechtecks der waagerechten und
     * des Senkrechten Vektors mit doppelter Länge des Radius.
     * @param point1 Punkt 1
     * @param point2 Punkt 2
     * @param radius die halbe Seitenlänge des Rechtecks
     * @return gibt true zurück, wenn sich Punkt 2 im Rechteck befindet und false, wenn nicht
     */
    public static boolean inInPixelRange(Vector2 point1, Vector2 point2, float radius) {
        float minX = point1.x;
        float maxX = point1.x + 2 * radius;
        float minY = point1.y;
        float maxY = point1.y + 2 * radius;

        return point2.x >= minX && point2.x <= maxX && point2.y >= minY && point2.y <= maxY;
    }

    /**
     * Die Methode berechnet den Funktionswert für den Wert x der Funktion:
     * b^-x,
     * wobei von: x_e - 0,5 kleiner als  x kleiner als x_e
     * eine Linearfunktion anliegt.
     * @param b die basis der Exponentialfunktion
     * @param x_e der maximale Wert, des Definitionsbereichs
     * @param x der x-Wert der des gesuchten Funktionswertes
     * @return gibt den Funktionswert an der Stelle x wieder
     */
    public static float getExpValue(float b, float x_e, float x) {
        // Ungültige Eingaben prüfen
        if (x < 0 || x_e < 0 || x > x_e) {
            return Float.NaN; // Ungültige Eingabe
        }

        // Exponentialfunktion für x <= x_e - 0.5
        if (x <= x_e - 0.5) {
            return (float) Math.pow(b, -x);
        }

        // Linearfunktion für x_e - 0.5 < x <= x_e
        if (x > x_e - 0.5 && x <= x_e) {
            // Berechnung der Steigung (m) und des Achsenabschnitts (n)
            float y1 = (float) Math.pow(b, -(x_e - 0.5)); // Funktionswert an x_e-0.5
            float y2 = 0; // Funktionswert an x_e
            float m = (float) ((y2 - y1) / (x_e - (x_e - 0.5))); // Steigung
            float n = (float) (y1 - m * (x_e - 0.5)); // Achsenabschnitt

            // Linearfunktion anwenden
            return m * x + n;
        }

        return Float.NaN; // Sicherheitsrückgabe
    }
    public static float interpolate(float a, float b, float x) {
        if (x < 0 || x > 1) {
            return Float.NaN;
        }
        return a+x*(b-a);
    }
}
