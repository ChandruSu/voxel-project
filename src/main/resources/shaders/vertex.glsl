#version 300 es

layout(location = 0) in vec3 vertex;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;

out vec2 uv;

void main() {
    gl_Position = vec4(vertex, 1.0);
    uv = texCoord;
}