#version 430 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;
in vec3 tangent;
in vec3 bitangent;
in mat4 model;

out vec4 passColour;

uniform mat4 view;
uniform mat4 projection;
uniform int inInstanceColourID;

void main() {
    vec4 worldPos = model * vec4(position, 1.0);
    gl_Position = projection * view * worldPos;
    passColour = vec4(inInstanceColourID, gl_InstanceID, 0.0, 1.0);
}