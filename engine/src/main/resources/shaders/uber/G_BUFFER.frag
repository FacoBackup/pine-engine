#define PARALLAX_THRESHOLD 200.

#define ALBEDO 0
#define NORMAL 1
#define DEPTH 3
#define AO 4
#define LIGHT_ONLY 6
#define METALLIC 7
#define ROUGHNESS 8
#define POSITION 11
#define RANDOM 13
#define WIREFRAME 17
#define LIT -1

#define ISOTROPIC 1
#define ANISOTROPIC 2
#define SHEEN 3
#define CLEAR_COAT 4
#define TRANSPARENCY 5

flat in vec3 cameraPlacement;
flat in int renderingIndex;
flat in float depthFunc;
smooth in vec2 initialUV;
smooth in vec3 normalVec;
smooth in vec3 worldSpacePosition;

uniform sampler2D albedo;
uniform sampler2D roughness;
uniform sampler2D metallic;
uniform sampler2D ao;
uniform sampler2D normal;
uniform sampler2D heightMap;

uniform float parallaxHeightScale;
uniform int parallaxLayers;
uniform int debugShadingMode;
uniform bool useParallax;

uniform bool fallbackMaterial;
uniform float anisotropicRotation;
uniform float anisotropy;
uniform float clearCoat;
uniform float sheen;
uniform float sheenTint;
uniform int renderingMode;
uniform bool ssrEnabled;

layout (location = 0) out vec4 gBufferAlbedoSampler;
layout (location = 1) out vec4 gBufferNormalSampler;
layout (location = 2) out vec4 gBufferRMAOSampler;

// X channel: 16 bits for anisotropicRotation + 16 bits for anisotropy
// Y channel: 16 bits for clearCoat + 16 bits for sheen
// Z channel: 16 bits for sheenTint + 15 bits for renderingMode + 1 bit for ssrEnabled
layout (location = 3) out vec4 gBufferMaterialSampler;
layout (location = 4) out vec4 gBufferDepthSampler;

float encode() {
    float half_co = depthFunc * 0.5;
    float clamp_z = max(0.000001, gl_FragCoord.z);
    return log2(clamp_z) * half_co;
}

mat3 computeTBN(vec3 worldPosition) {
    vec3 dp1 = dFdx(worldPosition);
    vec3 dp2 = dFdy(worldPosition);
    vec2 duv1 = dFdx(initialUV);
    vec2 duv2 = dFdy(initialUV);

    vec3 dp2perp = cross(dp2, normalVec);
    vec3 dp1perp = cross(normalVec, dp1);
    vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
    vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;

    float invmax = inversesqrt(max(dot(T, T), dot(B, B)));
    return mat3(T * invmax, B * invmax, normalVec);
}


vec2 parallaxOcclusionMapping(sampler2D heightMap, float heightScale, int layers, float distanceFromCamera, mat3 TBN) {
    if (distanceFromCamera > PARALLAX_THRESHOLD) return initialUV;
    mat3 transposed = transpose(TBN);
    vec3 viewDirection = normalize(transposed * (cameraPlacement.xyz - worldSpacePosition.xyz));
    float fLayers = float(max(layers, 1));
    float layerDepth = 1.0 / fLayers;
    float currentLayerDepth = 0.0;
    vec2 P = viewDirection.xy / viewDirection.z * max(heightScale, .00000001);
    vec2 deltaTexCoords = P / fLayers;

    vec2 currentUVs = initialUV;
    float currentDepthMapValue = texture(heightMap, currentUVs).r;
    while (currentLayerDepth < currentDepthMapValue) {
        currentUVs -= deltaTexCoords;
        currentDepthMapValue = texture(heightMap, currentUVs).r;
        currentLayerDepth += layerDepth;
    }

    vec2 prevTexCoords = currentUVs + deltaTexCoords;
    float afterDepth = currentDepthMapValue - currentLayerDepth;
    float beforeDepth = texture(heightMap, prevTexCoords).r - currentLayerDepth + layerDepth;


    float weight = afterDepth / (afterDepth - beforeDepth);
    return prevTexCoords * weight + currentUVs * (1.0 - weight);
}

float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 randomColor(float seed) {
    float r = rand(vec2(seed));
    float g = rand(vec2(seed + r));
    return vec3(r, g, rand(vec2(seed + g)));
}

#include "../uber/MATERIAL_INFO.glsl"

void main() {
    vec2 UV = initialUV;
    vec3 V = cameraPlacement.xyz - worldSpacePosition;
    float distanceFromCamera = length(V);
    mat3 TBN = computeTBN(worldSpacePosition);
    if (useParallax){
        UV = parallaxOcclusionMapping(heightMap, parallaxHeightScale, parallaxLayers, distanceFromCamera, TBN);
    }

    gBufferDepthSampler = vec4(encode(), 0, 0, 1);
    if (!fallbackMaterial){
        gBufferAlbedoSampler = vec4(texture(albedo, UV).rgb, 0);
        gBufferNormalSampler = vec4(vec3(normalize(TBN * ((texture(normal, UV).rgb * 2.0)- 1.0))), 1);
        gBufferRMAOSampler = vec4(texture(roughness, UV).r, texture(metallic, UV).r, 1 - texture(ao, UV).r, 1);
        gBufferMaterialSampler = vec4(packValues(anisotropicRotation, anisotropy, clearCoat, sheen, sheenTint, renderingMode, ssrEnabled), 1);
    } else {
        gBufferAlbedoSampler = vec4(vec3(.5), 0);
        gBufferNormalSampler = vec4(normalVec, 1.);
        gBufferRMAOSampler = vec4(.5, .5, 1, 1);
        gBufferMaterialSampler = vec4(packValues(0, 0, 0, 0, 0, ISOTROPIC, false), 1);
    }
    if (debugShadingMode != LIT){
        switch (debugShadingMode) {
            case RANDOM:
            gBufferAlbedoSampler.rgb = randomColor(renderingIndex + 1);
            break;
            case WIREFRAME:
            gBufferAlbedoSampler.rgb = vec3(1., 0., 1.);
            break;
            case LIGHT_ONLY:
            gBufferAlbedoSampler.rgb = vec3(.5);
            break;
            case NORMAL:
            gBufferAlbedoSampler = gBufferNormalSampler;
            break;
            case DEPTH:
            gBufferAlbedoSampler.rgb = vec3(gl_FragCoord.z);
            break;
            case AO:
            gBufferAlbedoSampler.rgb = vec3(gBufferRMAOSampler.b);
            break;
            case METALLIC:
            gBufferAlbedoSampler.rgb = vec3(gBufferRMAOSampler.g);
            break;
            case ROUGHNESS:
            gBufferAlbedoSampler.rgb = vec3(gBufferRMAOSampler.r);
            break;
            case POSITION:
            gBufferAlbedoSampler.rgb = vec3(worldSpacePosition);
            break;
        }
        gBufferAlbedoSampler.a = debugShadingMode != LIGHT_ONLY ? 1 : 0;
    }
}