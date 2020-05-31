#version 460 core

in vec2 passTextureCoord;

out vec4 outColour;

uniform sampler2D tex;
uniform vec3 ambientLight;

void main() {
    outColour = vec4(ambientLight, 1) * texture(tex, passTextureCoord);
}