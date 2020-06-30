#version 460 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;
in mat4 model;

out vec2 passTextureCoord;
out vec3 passVertexNormal;
out vec3 passVertexPos;
out vec4 clipSpace;
out vec3 passToCamera;

uniform mat4 view;
uniform mat4 projection;
uniform vec3 cameraPos;

const float tiling = 5.0;

void main() {
    vec4 worldPos = model * vec4(position, 1.0);
    gl_Position = projection * view * worldPos;
    clipSpace = gl_Position;
    passTextureCoord = textureCoord * tiling;
    passVertexNormal = normalize(model * vec4(normal, 0.0)).xyz;
    passVertexPos = worldPos.xyz;
    // need to work out fresnel effect
    // mix value will be the dot prod value between water normal and vector from water to camera position
    passToCamera = normalize(cameraPos - worldPos.xyz);
}