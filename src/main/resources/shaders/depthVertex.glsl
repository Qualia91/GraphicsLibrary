#version 460 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;
in mat4 model;

uniform mat4 lightTransformationView;
uniform mat4 orthoProj;

void main() {
    gl_Position = orthoProj * lightTransformationView * model * vec4(position, 1.0);
}