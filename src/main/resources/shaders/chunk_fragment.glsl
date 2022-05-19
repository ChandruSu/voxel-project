#version 300 es

const vec3 toLight = normalize(vec3(1, 3.5, 2));

precision highp float;

in vec3 passUv;
in vec3 passNormal;

uniform sampler2D tex;

out vec4 outColor;

void main() {
    float lighting = max(0.05, dot(passNormal, toLight) * 0.5 + 0.5);
    vec3 colour = texture(tex, passUv.xy).rgb * lighting;
    outColor = vec4(colour, 1.0);
}