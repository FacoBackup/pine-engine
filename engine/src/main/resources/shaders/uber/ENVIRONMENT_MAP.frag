#define PARALLAX_THRESHOLD 200.

flat in vec3 cameraPlacement;
smooth in vec2 initialUV;
smooth in vec3 normalVec;
smooth in vec3 worldSpacePosition;

uniform vec3 albedoColor;
uniform vec2 roughnessMetallic;
uniform vec4 useAlbedoRoughnessMetallicAO;
uniform bool useNormalTexture;

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
    vec3 albedoColorLocal = vec3(.5);
    vec3 rmao = vec3(.5, .5, 1);
    if (!fallbackMaterial){

        bool useMetallic = useAlbedoRoughnessMetallicAO.b != 0;
        bool useRoughness = useAlbedoRoughnessMetallicAO.g != 0;
        bool useAlbedo = useAlbedoRoughnessMetallicAO.r != 0;
        bool useAO = useAlbedoRoughnessMetallicAO.a != 0;

        albedoColorLocal = useAlbedo ? texture(albedo, initialUV).rgb : albedoColor;
        if (useNormalTexture){
            mat3 TBN = computeTBN(worldSpacePosition);
            N = vec3(normalize(TBN * ((texture(normal, initialUV).rgb * 2.0)- 1.0)));
        }
        rmao = vec3(useRoughness ? texture(roughness, initialUV).r : roughnessMetallic.r, useMetallic ? texture(metallic, initialUV).r : roughnessMetallic.g, useAO ? 1 - texture(ao, initialUV).r : 1);
    }

    finalColor = vec4(albedoColorLocal, 1);
}