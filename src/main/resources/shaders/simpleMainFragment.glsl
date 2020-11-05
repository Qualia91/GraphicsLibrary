#version 460 core

const int MAX_POINT_LIGHTS = 50;
const int MAX_SPOT_LIGHTS = 50;
const int MAX_DIRECTIONAL_LIGHTS = 50;

in vec2 passTextureCoord;
in vec3 passVertexNormal;
in vec3 passVertexPos;
in mat3 tbn;

out vec4 outColour;

struct Fog
{
    int isactive;
    vec3 colour;
    float density;
};

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
    int hasNormalMap;
};

uniform sampler2D tex;
uniform sampler2D normal_text_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform vec3 cameraPos;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform Fog fog;

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

void setupColours()
{
    ambientC = texture(tex, passTextureCoord);
    diffuseC = ambientC;
    speculrC = ambientC;

}

vec3 calcNormal(Material material, vec3 normal, vec2 text_coord, mat3 tbnMatrix) {

    vec3 newNormal = normal;

    if ( material.hasNormalMap == 1 ) {
        newNormal = texture(normal_text_sampler, text_coord).rgb;
        newNormal = newNormal * 2.0 - 1.0;
        newNormal = normalize(tbnMatrix * newNormal);
    }

    return newNormal;

}

void main() {
    setupColours();

    vec3 newNormal = calcNormal(material, passVertexNormal, passTextureCoord, tbn);

    vec4 diffuseSpecularComp = vec4(0, 0, 0, 0);

    outColour = ambientC * vec4(ambientLight, 1.0) + diffuseSpecularComp;

}