#version 430 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;
in mat4 model;

out vec2 passTextureCoord;
out vec3 passVertexNormal;
out vec3 passVertexPos;

uniform mat4 view;
uniform mat4 projection;

void main() {
    gl_Position = projection * view * model * vec4(position, 1.0);
    passTextureCoord = textureCoord;
    passVertexNormal = normalize((view * model * vec4(normal, 0.0))).xyz;
    passVertexPos = (model * vec4(position, 1.0)).xyz;
}