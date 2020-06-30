#version 460 core

const int MAX_POINT_LIGHTS = 50;
const int MAX_SPOT_LIGHTS = 50;
const int MAX_DIRECTIONAL_LIGHTS = 50;
const int MAX_TERRAIN_TEXTURE_OBJECTS = 10;

in vec2 passTextureCoord;
in vec3 passVertexNormal;
in vec3 passVertexPos;
in mat3 tbn;
in float vertexHeight;

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

uniform sampler2D texture_array[MAX_TERRAIN_TEXTURE_OBJECTS];
uniform sampler2D normal_array[MAX_TERRAIN_TEXTURE_OBJECTS];
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform vec3 cameraPos;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform Fog fog;

uniform float heights[MAX_TERRAIN_TEXTURE_OBJECTS];
uniform float transitionWidths[MAX_TERRAIN_TEXTURE_OBJECTS];

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

vec3 calcNormal(vec2 text_coord, mat3 tbnMatrix, sampler2D normalMap) {
    vec3 newNormal = texture(normalMap, text_coord).rgb;
    newNormal = newNormal * 2.0 - 1.0;
    return normalize(tbnMatrix * newNormal);
}

vec3 calcNormalMix(float height, float transitionWidth, vec3 newNormal, sampler2D nextTex, mat3 tbnMatrix, vec2 text_coord) {
    // work out the mix value
    // find the diff between vertex x value and snow height
    float del = vertexHeight - height;

    // get this as a fraction of the transition width
    del = del / transitionWidth;

    // cap this between -1 and 1
    del = clamp(del, -1.0, 1.0);

    // make this between 0 and 1 by dividing by 2 and adding 0.5
    del = (del * 0.5) + 0.5;

    vec3 nextNormal = calcNormal(text_coord, tbnMatrix, nextTex);

    return mix(newNormal, nextNormal, del);
}


vec3 calcNormal(vec2 text_coord, mat3 tbnMatrix) {

    vec3 newNormal = calcNormal(text_coord, tbnMatrix, normal_array[0]);

    for (int i=1; i<MAX_TERRAIN_TEXTURE_OBJECTS; i++)
    {
        if ( transitionWidths[i] > 0 )
        {
            newNormal = calcNormalMix(heights[i], transitionWidths[i], newNormal, normal_array[i], tbnMatrix, text_coord);
        }
    }

    return newNormal;
}

vec4 calcAmbientMix(float height, float transitionWidth, vec4 currentAmbientC, sampler2D nextTex) {
    // work out the mix value
    // find the diff between vertex x value and snow height
    float del = vertexHeight - height;

    // get this as a fraction of the transition width
    del = del / transitionWidth;

    // cap this between -1 and 1
    del = clamp(del, -1.0, 1.0);

    // make this between 0 and 1 by dividing by 2 and adding 0.5
    del = (del * 0.5) + 0.5;

    return mix(currentAmbientC, texture(nextTex, passTextureCoord), del);
}

void setupColours()
{

    ambientC = texture(texture_array[0], passTextureCoord);
    for (int i=1; i<MAX_TERRAIN_TEXTURE_OBJECTS; i++)
    {
        if ( transitionWidths[i] > 0 )
        {
            ambientC = calcAmbientMix(heights[i], transitionWidths[i], ambientC, texture_array[i]);
        }
    }

    diffuseC = ambientC;
    speculrC = ambientC;

}

vec4 calcLightColour(vec3 vertexPosition, vec3 vertexNormal, float lightIntensity, vec3 toLightDirection, vec3 lightColour) {
    // diffuse
    float diffuseFactor = max(dot(vertexNormal, toLightDirection), 0.0);
    vec4 diffuseLight = diffuseC * vec4(lightColour, 1.0) * lightIntensity * diffuseFactor;

    // check if its the backside of the object as reflect is insensitive to sign of normal:
    // reflect(I, N) = I - 2.0 * dot(N, I) * N
    vec4 specularLight = vec4(0.0, 0.0, 0.0, 0.0);
    if (diffuseFactor > 0.0) {

        // specular
        vec3 fromLightDir = -toLightDirection;
        vec3 reflectedLight = reflect(fromLightDir, vertexNormal);
        vec3 cameraDirection = normalize(cameraPos - vertexPosition);
        float specularFactor = pow(max(dot(cameraDirection, reflectedLight), 0.0), specularPower);
        specularLight = speculrC * vec4(lightColour, 1.0) * lightIntensity * specularFactor;

    }

    return (diffuseLight + specularLight);
}

vec4 calcPointLight(vec3 vertexPosition, vec3 vertexNormal, PointLight pointLight) {
    vec3 toLight = pointLight.position - vertexPosition;
    vec3 toLightDir = normalize(toLight);

    // attenuation
    float dist = length(toLight);
    float attenuation = 1.0 / (1 + pointLight.att.constant + pointLight.att.linear*dist + pointLight.att.exponent*dist*dist);

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

vec4 calcFog(vec3 pos, vec4 colour, Fog fog) {
    float distance = length(pos);
    float fogFactor = 1.0 / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0.0, 1.0);

    vec3 resultColour = mix(fog.colour, colour.xyz, fogFactor);
    return vec4(resultColour, colour.w);
}

void main() {

    setupColours();

    vec3 newNormal = calcNormal(passTextureCoord, tbn);

    vec4 diffuseSpecularComp = vec4(0, 0, 0, 0);
    for (int i=0; i<MAX_POINT_LIGHTS; i++)
    {
        if ( pointLights[i].intensity > 0 )
        {
            diffuseSpecularComp += calcPointLight(passVertexPos, newNormal, pointLights[i]);
        }
    }

    for (int i=0; i<MAX_SPOT_LIGHTS; i++)
    {
        if ( spotLights[i].pointLight.intensity > 0 )
        {
            diffuseSpecularComp += calcSpotLight(passVertexPos, newNormal, spotLights[i]);
        }
    }


    for (int i=0; i<MAX_DIRECTIONAL_LIGHTS; i++)
    {
        if ( directionalLights[i].intensity > 0 )
        {
            diffuseSpecularComp += calcDirectionalLight(passVertexPos, newNormal, directionalLights[i]);
        }
    }

    outColour = ambientC * vec4(ambientLight, 1.0) + diffuseSpecularComp;

    if ( fog.isactive == 1 )
    {
        outColour = calcFog(cameraPos - passVertexPos, outColour, fog);
    }
}