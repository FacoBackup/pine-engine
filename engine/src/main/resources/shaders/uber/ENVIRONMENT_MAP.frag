#define PARALLAX_THRESHOLD 200.

flat in vec3 cameraPlacement;
smooth in vec2 initialUV;
smooth in vec3 normalVec;
smooth in vec3 worldSpacePosition;

layout (binding = 0) uniform sampler2D albedo;
layout (binding = 1) uniform sampler2D roughness;
layout (binding = 2) uniform sampler2D metallic;
layout (binding = 3) uniform sampler2D ao;
layout (binding = 4) uniform sampler2D normal;
layout (binding = 5) uniform sampler2D heightMap;

uniform bool fallbackMaterial;

out vec4 finalColor;

#include "../uber/G_BUFFER_UTIL.glsl"

#include "../uber/MATERIAL_INFO.glsl"

void main() {
    vec3 N = normalVec;
    vec3 albedoColor = vec3(.5);
    vec3 rmao = vec3(.5, .5, 1);
    if (!fallbackMaterial){
        mat3 TBN = computeTBN(worldSpacePosition);
        albedoColor = texture(albedo, initialUV).rgb;
        N = vec3(normalize(TBN * ((texture(normal, initialUV).rgb * 2.0)- 1.0)));
        rmao = vec3(texture(roughness, initialUV).r, texture(metallic, initialUV).r, 1 - texture(ao, initialUV).r);
    }
    finalColor = vec4(albedoColor, 1);
}