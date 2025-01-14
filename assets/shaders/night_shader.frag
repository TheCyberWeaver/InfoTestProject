#version 330 core

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;      // Die Textur, die gerendert wird
uniform vec2 u_playerPos;         // Position des Spielers
uniform float u_lightRadius;      // Radius des Lichtes
uniform float u_fadeStart;        // Beginn des Lichtabfalls

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    // Hole die Texturfarbe
    vec4 texColor = texture2D(u_texture, v_texCoord);

    // Berechne die Entfernung des Pixels zur Spielerposition
    vec2 fragPos = gl_FragCoord.xy; // Position des Fragments im Bildschirmraum
    float distance = length(fragPos - u_playerPos);

    // Berechne die Lichtintensität basierend auf der Entfernung
    float intensity = smoothstep(u_fadeStart, u_lightRadius, distance);

    // Kombiniere die Texturfarbe mit der Lichtintensität
    vec4 lightEffect = vec4(texColor.rgb * intensity, texColor.a);

    // Setze die finale Farbe
    gl_FragColor = lightEffect;
}
