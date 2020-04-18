#version 460 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;
in mat4 model;

out vec2 passTextureCoord;
out vec3 passVertexNormal;
out vec3 passVertexPos;
out vec4 mlightviewVertexPos;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;

void main() {
    gl_Position = projection * view * model * vec4(position, 1.0);
    passTextureCoord = textureCoord;
    passVertexNormal = normalize(model * vec4(normal, 0.0)).xyz;
    passVertexPos = (model * vec4(position, 1.0)).xyz;
    mlightviewVertexPos = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0);
}