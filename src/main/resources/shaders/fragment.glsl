#version 300 es

precision highp float;
precision highp int;

in vec2 uv;
in vec3 wNormal;

uniform sampler2D tex;

out vec4 fragColor;

const vec3 lightDir = -normalize(vec3(-0.5f, -1.0f, 0.25f));

void main() {
    float lighting = max(0.4f, 0.5f * dot(wNormal, lightDir) + 0.5f);
    fragColor = lighting * vec4(texture(tex, uv).xyz, 1.0f);
}