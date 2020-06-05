#version 460 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;
in mat4 model;

out vec2 passTextureCoord;
out vec3 passVertexNormal;
out vec3 passVertexPos;
out mat4 modelViewMatrix;

uniform mat4 view;
uniform mat4 projection;

void main() {
    modelViewMatrix = view * model;
    gl_Position = projection * modelViewMatrix * vec4(position, 1.0);
    passTextureCoord = textureCoord;
    passVertexNormal = normalize(model * vec4(normal, 0.0)).xyz;
    passVertexPos = (model * vec4(position, 1.0)).xyz;
}