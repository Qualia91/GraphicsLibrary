#version 460 core

const int MAX_POINT_LIGHTS = 60;
const int MAX_SPOT_LIGHTS = 60;
const int MAX_DIRECTIONAL_LIGHTS = 60;

in vec2 passTextureCoord;
in vec3 passVertexNormal;
in vec3 passVertexPos;

out vec4 outColour;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation att;
};

struct SpotLight
{
    vec3 coneDirection;
    float coneAngleCosine;
    PointLight pointLight;
};

struct DirectionalLight
{
    vec3 colour;
    vec3 direction;
    float intensity;
};

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
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform vec3 cameraPos;
uniform DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

void setupColours()
{
    ambientC = texture(tex, passTextureCoord);
    diffuseC = ambientC;
    speculrC = ambientC;
}

void main() {
    setupColours();
    outColour = ambientC;
}