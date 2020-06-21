#version 460 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;
layout (location = 2) in vec3 normal;
layout (location = 3) in mat4 model;
layout (location = 8) in vec3 tangent;
layout (location = 9) in vec3 bitangent;

out vec4 passColour;

uniform mat4 view;
uniform mat4 projection;
uniform int inInstanceColourID;

void main() {
    vec4 worldPos = model * vec4(position, 1.0);
    gl_Position = projection * view * worldPos;
    passColour = vec4(inInstanceColourID, gl_InstanceID, 0, 1);
}