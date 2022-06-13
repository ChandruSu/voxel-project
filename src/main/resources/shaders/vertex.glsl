#version 300 es

precision highp float;
precision highp int;

layout(location = 0) in vec3 vertex;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 transformation;

out vec2 uv;
out vec3 wNormal;

void main() {
    gl_Position = projection * view * transformation * vec4(vertex, 1.0);
    wNormal = normalize((transformation * vec4(normal, 0.0)).xyz);
    uv = texCoord;
}