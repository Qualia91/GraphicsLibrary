#version 460 core

in vec3 position;
in vec3 colour;
in vec2 textureCoord;
in vec3 normals;

out vec3 passColour;
out vec2 passTextureCoord;
out vec3 passAmbientLight;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 ambientLight;
uniform vec3 pointLightPosition;
uniform vec3 pointLightColour;

void main() {
    gl_Position = projection * view * model * vec4(position, 1.0);
    passColour = colour;
    passTextureCoord = textureCoord;
    vec3 vectorTowardsLight = normalize(pointLightPosition - position);
    vec3 fromLightSource = -vectorTowardsLight;
    vec3 camera_direction = normalize(-position);
    vec3 reflected_light = normalize(reflect(fromLightSource, normals));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, 2);
    passAmbientLight = ambientLight + (max(dot(vectorTowardsLight, normals), 0.0) * pointLightColour) + (pointLightColour * specularFactor);
}