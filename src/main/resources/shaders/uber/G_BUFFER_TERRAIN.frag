#define ISOTROPIC 1
#define ANISOTROPIC 2
#define SHEEN 3
#define CLEAR_COAT 4
#define TRANSPARENCY 5

#define DECAL_DEPTH_THRESHOLD 1.

flat in int renderingIndex;
smooth in vec2 initialUV;
smooth in vec3 normalVec;
smooth in vec3 worldSpacePosition;

layout (binding = 4) uniform sampler2D materialMask;

layout (binding = 5) uniform sampler2D albedo;
layout (binding = 6) uniform sampler2D roughness;
layout (binding = 7) uniform sampler2D metallic;
layout (binding = 8) uniform sampler2D normal;
uniform vec3 material0;

layout (binding = 9) uniform sampler2D albedo1;
layout (binding = 10) uniform sampler2D roughness1;
layout (binding = 11) uniform sampler2D metallic1;
layout (binding = 12) uniform sampler2D normal1;
uniform vec3 material1;

layout (binding = 13) uniform sampler2D albedo2;
layout (binding = 14) uniform sampler2D roughness2;
layout (binding = 15) uniform sampler2D metallic2;
layout (binding = 16) uniform sampler2D normal2;
uniform vec3 material2;

layout (binding = 17) uniform sampler2D albedo3;
layout (binding = 18) uniform sampler2D roughness3;
layout (binding = 19) uniform sampler2D metallic3;
layout (binding = 20) uniform sampler2D normal3;
uniform vec3 material3;

uniform vec3 albedoColor;
uniform vec2 roughnessMetallic;

uniform float parallaxHeightScale;
uniform int parallaxLayers;
uniform bool useParallax;
uniform bool fallbackMaterial;

uniform int renderingMode;
uniform bool ssrEnabled;

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

#include "../uber/MATERIAL_INFO.glsl"

#include "../uber/G_BUFFER_UTIL.glsl"

void blendMaterial(inout vec3 color, vec3 colorToMatch, inout vec2 UV, sampler2D albedo, sampler2D roughness, sampler2D metallic, sampler2D normal){
    if (colorToMatch.r != 0 && color.r != 0 || colorToMatch.g != 0 && color.g != 0 || colorToMatch.b != 0 && color.b != 0){
        gBufferAlbedoSampler = vec4(texture(albedo, UV).rgb, 0);
        gBufferRMAOSampler = vec4(texture(roughness, UV).r, texture(metallic, UV).r, 0, 1);
        gBufferMaterialSampler = vec4(packValues(0, 0, 0, 0, 0, ISOTROPIC, false), 1);
        gBufferNormalSampler = vec4(vec3(normalize(computeTBN(worldSpacePosition, UV, normalVec, 0) * ((texture(normal, UV).rgb * 2.0)- 1.0))), 1);
    }
}

void main() {
    vec3 W = worldSpacePosition;
    vec2 UV = W.xz * .1;
    vec3 V = cameraWorldPosition.xyz - W;
    vec3 N = normalVec;
    vec3 materialId = texture(materialMask, initialUV).rgb;
    gBufferDepthSampler = vec4(encode(logDepthFC, gl_FragCoord.z), renderingIndex + 1, initialUV);

    if (length(materialId) == 0){
        gBufferAlbedoSampler = vec4(vec3(1), 0);
        gBufferRMAOSampler = vec4(1, 0, 0, 1);
        gBufferMaterialSampler = vec4(packValues(0, 0, 0, 0, 0, ISOTROPIC, false), 1);
        gBufferNormalSampler = vec4(N, 1);
    }else{
        blendMaterial(materialId, material0, UV, albedo, roughness, metallic, normal);
       blendMaterial(materialId, material1, UV, albedo1, roughness1, metallic1, normal1);
       blendMaterial(materialId, material2, UV, albedo2, roughness2, metallic2, normal2);
       blendMaterial(materialId, material3, UV, albedo3, roughness3, metallic3, normal3);
    }

    sampleIndirectIllumination(V, gBufferNormalSampler.rgb);
    processDebugFlags(UV, W, renderingIndex, length(V), materialId * 100.);
}