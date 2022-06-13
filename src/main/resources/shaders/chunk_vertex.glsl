#version 300 es

precision highp float;
precision highp int;

#define AXIS_BITS 5u
#define WIDTH     31u

const vec2 uvCoords[4] = vec2 [] (
    vec2(0.0, 0.0),
    vec2(1.0, 0.0),
    vec2(0.0, 1.0),
    vec2(1.0, 1.0)
);

const vec3 normals[6] = vec3 [] (
    vec3(-1.0, 0.0, 0.0),
    vec3(1.0, 0.0, 0.0),
    vec3(0.0, -1.0, 0.0),
    vec3(0.0, 1.0, 0.0),
    vec3(0.0, 0.0, -1.0),
    vec3(0.0, 0.0, 1.0)
);

const vec3 offset = vec3(0, 0, -10);

layout (location=0) in uint data;
layout (location=1) in uint type;

uniform ivec3 position;
uniform mat4 projection;
uniform mat4 view;

out vec3 passUv;
out vec3 passNormal;

void main() {
    uint x = data & WIDTH;
    uint y = (data >> AXIS_BITS) & WIDTH;
    uint z = (data >> (AXIS_BITS * 2u)) & WIDTH;

    vec3 pos = vec3(x, y, z);

    uint orientation = (data >> 25u) & 7u;
    passNormal = normals[orientation];
    passUv = vec3(uvCoords[(data >> 28u) & 3u], type);

    if (orientation % 2u == 1u) {
        pos += passNormal;
        orientation = orientation - 1u;
    }

    orientation = orientation / 2u;
    pos[(orientation + 1u) % 3u] += passUv.x;
    pos[(orientation + 2u) % 3u] += passUv.y;

    passUv.y *= float((data >> 15u) & WIDTH) + 1.0;
    passUv.x *= float((data >> 20u) & WIDTH) + 1.0;

    pos += vec3(position);
    gl_Position = projection * view * vec4(pos, 1.0);
}