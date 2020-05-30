#version 460 core

layout (points) in;
layout (triangle_strip, max_vertices = 8) out;

in vec3 baseColour[];

out vec3 colour;

uniform mat4 modelMatrix;
uniform mat4 prjectionMatrix;
uniform mat4 veiwMatrix;

void main() {


}
