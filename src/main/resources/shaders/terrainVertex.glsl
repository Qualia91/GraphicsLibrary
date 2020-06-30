#version 460 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;
layout (location = 2) in vec3 normal;
layout (location = 3) in mat4 model;
layout (location = 8) in vec3 tangent;
layout (location = 9) in vec3 bitangent;

out vec2 passTextureCoord;
out vec3 passVertexNormal;
out vec3 passVertexPos;
out mat3 tbn;
out float vertexHeight;

uniform mat4 view;
uniform mat4 projection;
uniform vec4 clippingPlane;

void main() {
    vec4 worldPos = model * vec4(position, 1.0);
    gl_ClipDistance[0] = dot(worldPos, clippingPlane);
    gl_Position = projection * view * worldPos;
    passTextureCoord = textureCoord;
    passVertexNormal = normalize(model * vec4(normal, 0.0)).xyz;
    vec3 passVertexTangent = normalize(model * vec4(tangent, 0.0)).xyz;
    vec3 passVertexBitangent = normalize(model * vec4(bitangent, 0.0)).xyz;
    passVertexPos = worldPos.xyz;
    vertexHeight = position.z;
    tbn = mat3(passVertexTangent, passVertexBitangent, passVertexNormal);
}