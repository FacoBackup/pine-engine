#define ISOTROPIC 1
#define ANISOTROPIC 2
#define SHEEN 3
#define CLEAR_COAT 4
#define TRANSPARENCY 5

#define DECAL_DEPTH_THRESHOLD 1.

in mat4 invModelMatrix;
flat in int isDecalPass;
flat in int renderingIndex;
smooth in vec2 initialUV;
smooth in vec3 normalVec;
smooth in vec3 worldSpacePosition;

layout (binding = 4) uniform sampler2D roughness;
layout (binding = 5) uniform sampler2D metallic;
layout (binding = 6) uniform sampler2D ao;
layout (binding = 8) uniform sampler2D heightMap;

layout (binding = 3) uniform sampler2D albedo;
layout (binding = 7) uniform sampler2D normal;
layout (binding = 9) uniform sampler2D sceneDepth;

uniform vec3 albedoColor;
uniform vec2 roughnessMetallic;
uniform vec4 useAlbedoRoughnessMetallicAO;
uniform bool useNormalTexture;

uniform float parallaxHeightScale;
uniform int parallaxLayers;
uniform bool useParallax;

uniform bool fallbackMaterial;
uniform float anisotropicRotation;
uniform float anisotropy;
uniform float clearCoat;
uniform float sheen;
uniform float sheenTint;
uniform int renderingMode;
uniform bool ssrEnabled;

#include "../util/SCENE_DEPTH_UTILS.glsl"

#include "../uber/MATERIAL_INFO.glsl"

#include "../uber/G_BUFFER_UTIL.glsl"

void main() {
    vec2 UV = initialUV;
    vec3 W = worldSpacePosition;
    vec3 N = normalVec;
    float depth = encode(logDepthFC, gl_FragCoord.z);
    if (isDecalPass == 1){
        vec2 quadUV = gl_FragCoord.xy/bufferResolution;
        depth = getLogDepth(quadUV);
        if (depth == 1.) discard;

        vec3 viewSpacePosition = viewSpacePositionFromDepth(depth, quadUV);
        N = normalize(vec3(invViewMatrix * vec4(normalFromDepth(depth, quadUV), 0.)));
        W = vec3(invViewMatrix * vec4(viewSpacePosition, 1.));
        vec3 objectSpacePosition = vec3(invModelMatrix * vec4(W, 1.));
        UV = objectSpacePosition.xz * .5 + .5;

        bool inRange =
        objectSpacePosition.x >= -DECAL_DEPTH_THRESHOLD &&
        objectSpacePosition.x <= DECAL_DEPTH_THRESHOLD &&

        objectSpacePosition.y >= -DECAL_DEPTH_THRESHOLD &&
        objectSpacePosition.y <= DECAL_DEPTH_THRESHOLD &&

        objectSpacePosition.z >= -DECAL_DEPTH_THRESHOLD &&
        objectSpacePosition.z <= DECAL_DEPTH_THRESHOLD;
        depth = encode(logDepthFC, depth);
        if (!inRange) discard;
    }

    vec3 V = cameraWorldPosition.xyz - W;
    float distanceFromCamera = length(V);
    mat3 TBN = computeTBN(W, UV, N, isDecalPass);
    if (useParallax){
        UV = parallaxOcclusionMapping(UV, W, heightMap, parallaxHeightScale, parallaxLayers, distanceFromCamera, TBN);
    }
    gBufferDepthSampler = vec4(depth, renderingIndex + 1, UV);
    if (!fallbackMaterial){
        bool useMetallic = useAlbedoRoughnessMetallicAO.b != 0;
        bool useRoughness = useAlbedoRoughnessMetallicAO.g != 0;
        bool useAlbedo = useAlbedoRoughnessMetallicAO.r != 0;
        bool useAO = useAlbedoRoughnessMetallicAO.a != 0;

        vec4 al = texture(albedo, UV);
        if(al.a < .5){
            discard;
        }
        gBufferAlbedoSampler = vec4(useAlbedo ? al.rgb : albedoColor, 0);
        if (useNormalTexture){
            N = vec3(normalize(TBN * ((texture(normal, UV).rgb * 2.0)- 1.0)));
        }
        gBufferRMAOSampler = vec4(useRoughness ? texture(roughness, UV).r : roughnessMetallic.r, useMetallic ? texture(metallic, UV).r : roughnessMetallic.g, useAO ? 1 - texture(ao, UV).r : 1, 1);
        gBufferMaterialSampler = vec4(packValues(anisotropicRotation, anisotropy, clearCoat, sheen, sheenTint, renderingMode, ssrEnabled), 1);
    } else {
        gBufferAlbedoSampler = vec4(vec3(1), 0);
        gBufferRMAOSampler = vec4(1, 0, 0, 1);
        gBufferMaterialSampler = vec4(packValues(0, 0, 0, 0, 0, ISOTROPIC, false), 1);
    }
    gBufferNormalSampler = vec4(N, 1);

    sampleIndirectIllumination(V, N);

    processDebugFlags(UV, W, renderingIndex, distanceFromCamera, vec4(0));
}