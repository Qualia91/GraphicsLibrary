#version 460 core

in vec4 clipSpace;

out vec4 outColour;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform vec3 ambientLight;

void main() {

    // convert clip space to normilised devices space using perspective division
    vec2 ndc = clipSpace.xy/clipSpace.w;

    // put in tex coord unis (0 -> 1)
    ndc =  ndc / 2 + 0.5;

    // above is refraction tex coords. reflection tex coors, you invert y
    vec2 reflectionTextCoord = vec2(ndc.x, ndc.y);
    vec2 refractionTextCoord = vec2(ndc.x, ndc.y);

    vec4 refletionColour = texture(reflectionTexture, reflectionTextCoord);
    vec4 refrationColour = texture(refractionTexture, refractionTextCoord);

    //outColour = mix(refletionColour, refrationColour, 0.5);
    outColour = refletionColour;
}