#version 460 core

const int MAX_POINT_LIGHTS = 36;
const int MAX_SPOT_LIGHTS = 36;
const int MAX_DIRECTIONAL_LIGHTS = 36;

in vec2 passTextureCoord;
in vec3 passVertexNormal;
in vec3 passVertexPos;
in vec4 mlightviewVertexPos;
in mat4 outModelViewMatrix;

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
uniform sampler2D shadowMap;

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

void setupColours()
{
    ambientC = texture(tex, passTextureCoord);
    diffuseC = ambientC;
    speculrC = ambientC;
}

vec4 calcLightColour(vec3 vertexPosition, vec3 vertexNormal, float lightIntensity, vec3 toLightDirection, vec3 lightColour) {
    // diffuse
    float diffuseFactor = max(dot(vertexNormal, toLightDirection), 0.0);
    vec4 diffuseLight = diffuseC * vec4(lightColour, 1.0) * lightIntensity * diffuseFactor;

    // specular
    vec3 fromLightDir = -toLightDirection;
    vec3 reflectedLight = reflect(fromLightDir, vertexNormal);
    vec3 cameraDirection = normalize(cameraPos - vertexPosition);
    float specularFactor = pow(max(dot(cameraDirection, reflectedLight), 0.0), specularPower);
    vec4 specularLight = speculrC * vec4(lightColour, 1.0) * lightIntensity * specularFactor;

    return (diffuseLight + specularLight);
}

vec4 calcPointLight(vec3 vertexPosition, vec3 vertexNormal, PointLight pointLight) {
    vec3 toLight = pointLight.position - vertexPosition;
    vec3 toLightDir = normalize(toLight);

    // attenuation
    float dist = length(toLight);
    float attenuation = 1.0 / (pointLight.att.constant + pointLight.att.linear*dist + pointLight.att.exponent*dist*dist);

    float intensity = pointLight.intensity;
    vec3 colour = pointLight.colour;

    return calcLightColour(vertexPosition, vertexNormal, intensity, toLightDir, colour) * attenuation;
}

vec4 calcSpotLight(vec3 vertexPosition, vec3 vertexNormal, SpotLight spotLight) {

    // can cone see object
    vec3 toLight = spotLight.pointLight.position - vertexPosition;
    vec3 toLightDir = normalize(toLight);
    vec3 fromLigthDir = -toLightDir;
    float spot_alpha = dot(fromLigthDir, normalize(spotLight.coneDirection));

    vec4 returnColour = vec4(0, 0, 0, 0);

    if (spot_alpha > spotLight.coneAngleCosine) {

        // attenuation
        float attenuationFactor = (1 - (1 - spot_alpha)/(1 - spotLight.coneAngleCosine));

        returnColour = calcPointLight(vertexPosition, vertexNormal, spotLight.pointLight) * attenuationFactor;

    }

    return returnColour;
}

vec4 calcDirectionalLight(vec3 vertexPosition, vec3 vertexNormal, DirectionalLight directionalLight) {
    vec3 toLight = -directionalLight.direction;
    vec3 toLightDir = normalize(toLight);

    float intensity = directionalLight.intensity;
    vec3 colour = directionalLight.colour;

    return calcLightColour(vertexPosition, vertexNormal, intensity, toLightDir, colour);
}

float calcShadow(vec4 position)
{
    float shadowFactor = 1.0;
    vec3 projCoords = position.xyz;
    // Transform from screen coordinates to texture coordinates
    projCoords = projCoords * 0.5 + 0.5;
    if ( projCoords.z < texture(shadowMap, projCoords.xy).r )
    {
        // Current fragment is not in shade
        shadowFactor = 0;
    }

    return 1 - shadowFactor;
}

void main() {
    setupColours();
    vec4 diffuseSpecularComp = vec4(0, 0, 0, 0);
    for (int i=0; i<MAX_POINT_LIGHTS; i++)
    {
        if ( pointLights[i].intensity > 0 )
        {
            diffuseSpecularComp += calcPointLight(passVertexPos, passVertexNormal, pointLights[i]);
        }
    }

    for (int i=0; i<MAX_SPOT_LIGHTS; i++)
    {
        if ( spotLights[i].pointLight.intensity > 0 )
        {
            diffuseSpecularComp += calcSpotLight(passVertexPos, passVertexNormal, spotLights[i]);
        }
    }


    for (int i=0; i<MAX_DIRECTIONAL_LIGHTS; i++)
    {
        if ( directionalLights[i].intensity > 0 )
        {
            diffuseSpecularComp += calcDirectionalLight(passVertexPos, passVertexNormal, directionalLights[i]);
        }
    }

    //float shadow = calcShadow(mlightviewVertexPos);

    //outColour = clamp(ambientC * vec4(ambientLight, 1.0) + diffuseSpecularComp * shadow, 0, 1);
    outColour = ambientC * vec4(ambientLight, 1.0) + diffuseSpecularComp;
   // outColour = ambientC;
}