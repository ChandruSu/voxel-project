#version 300 es

in vec2 uv;

out vec4 fragColor;

void main() {
    fragColor = vec4(uv.x, 0.0, uv.y, 1.0);
}