#version 460 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;
in mat4 model;

out vec2 passTextureCoord;
out vec3 passVertexNormal;
out vec3 passVertexPos;

uniform mat4 view;
uniform mat4 projection;
uniform vec4 clippingPlane;

void main() {
    vec4 worldPos = model * vec4(position, 1.0);
    gl_ClipDistance[0] = dot(worldPos, clippingPlane);
    gl_Position = projection * view * worldPos;
    passTextureCoord = textureCoord;
    passVertexNormal = normalize(model * vec4(normal, 0.0)).xyz;
    passVertexPos = worldPos.xyz;
}