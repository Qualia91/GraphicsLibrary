#version 330 core

in vec3 passColour;
in vec3 passAmbientLight;
in vec2 passTextureCoord;
out vec4 outColour;

uniform sampler2D tex;

struct Light{
    float intensity;
    vec3 color;
    vec3 position;
    vec3 direction;
};

void main() {
    outColour = texture(tex, passTextureCoord) * vec4(passAmbientLight, 1);
}