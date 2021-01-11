#version 430 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;
in vec3 tangent;
in vec3 bitangent;
in mat4 model;

out vec2 passTextureCoord;

uniform mat4 view;
uniform mat4 projection;

void main() {
    gl_Position = projection * view * model * vec4(position, 1.0);
    passTextureCoord = textureCoord;
}