#version 460 core

in vec2 passTextureCoord;
in vec3 passVertexNormal;
in vec3 passVertexPos;
in vec4 clipSpace;
in vec3 passToCamera;

out vec4 outColour;

struct Fog
{
    int isactive;
    vec3 colour;
    float density;
};

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvmap;
uniform float moveFactor;
uniform vec3 cameraPos;
uniform Fog fog;

const float distStrength = 0.005;

vec4 calcFog(vec3 pos, vec4 colour, Fog fog) {
    float distance = length(pos);
    float fogFactor = 1.0 / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0.0, 1.0);

    vec3 resultColour = mix(fog.colour, colour.xyz, fogFactor);
    return vec4(resultColour, colour.w);
}

void main() {

    // convert clip space to normilised devices space using perspective division
    vec2 ndc = clipSpace.xy/clipSpace.w;

    // put in tex coord unis (0 -> 1)
    ndc =  ndc / 2 + 0.5;

    // above is refraction tex coords. reflection tex coors, you invert x
    vec2 reflectionTextCoord = vec2(-ndc.x, ndc.y);
    vec2 refractionTextCoord = vec2(ndc.x, ndc.y);

    // get dudv map
    vec2 distortion1 = (texture(dudvmap, vec2(passTextureCoord.x + moveFactor, passTextureCoord.y)).rg * 2 - 1) * distStrength;
    vec2 distortion2 = (texture(dudvmap, vec2(passTextureCoord.x, passTextureCoord.y - moveFactor)).rg * 2 - 1) * distStrength;
    vec2 distortion = distortion1 + distortion2;

    reflectionTextCoord += distortion;
    refractionTextCoord += distortion;

    // need to clamp the above tex coords otherwise at points where the coord is 0, the distortion
    // makes it warp around to the top of the texture (Screen in this case)
    refractionTextCoord = clamp(refractionTextCoord, 0.001, 0.999);

    // clamping needs to be flipped for the direction we flipped the coords in (x)
    reflectionTextCoord.x = clamp(reflectionTextCoord.x, -0.999, -0.001);
    reflectionTextCoord.y = clamp(reflectionTextCoord.y, 0.001, 0.999);

    vec4 refletionColour = texture(reflectionTexture, reflectionTextCoord);
    vec4 refrationColour = texture(refractionTexture, refractionTextCoord);

    // fresnel effect
    float factor = dot(passToCamera, passVertexNormal);

    outColour = mix(refletionColour, refrationColour, pow(factor, 3));

    if ( fog.isactive == 1 )
    {
        outColour = calcFog(cameraPos - passVertexPos, outColour, fog);
    }
}