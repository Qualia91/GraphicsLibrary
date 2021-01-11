#version 430 core

in vec2 passTextureCoord;
in vec3 passVertexNormal;
in vec3 passVertexPos;

out vec4 outColour;

struct Material
{
    vec4 diffuse;
    vec4 specular;
    vec4 shininess;
    float reflectance;
};

struct Camera
{
    vec3 cameraPos;
};

uniform sampler2D tex;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform vec3 cameraPos;

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

void main() {
    outColour = vec4(253.0/255.0, 208.0/255.0, 35.0/255.0, 0.7) * length(passVertexNormal);;
}