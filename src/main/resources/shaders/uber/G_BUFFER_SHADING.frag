in vec2 naturalTextureUV;
in vec3 naturalNormal;
in vec3 worldPosition;
in mat4 invModelMatrix;
in flat int renderIndex;


#define MAX_LIGHTS 310
#define PI 3.14159265359
#define FRAG_DEPTH_THRESHOLD .001
#define DECAL_DEPTH_THRESHOLD 1.
#define CLAMP_MIN .1
#define CLAMP_MAX .9
#define SEARCH_STEPS 5
#define DEPTH_THRESHOLD 1.2
#define PI2 6.2831853

#define ISOTROPIC 1
#define ANISOTROPIC 2
#define SHEEN 3
#define CLEAR_COAT 4
#define TRANSPARENCY 5

layout(binding = 0) uniform sampler2D gBufferAlbedoSampler;
layout(binding = 1) uniform sampler2D gBufferNormalSampler;
layout(binding = 2) uniform sampler2D gBufferRMAOSampler;
layout(binding = 3) uniform sampler2D gBufferMaterialSampler;
layout(binding = 4) uniform sampler2D brdfSampler;
layout(binding = 5) uniform sampler2D SSAO;
layout(binding = 6) uniform sampler2D sunShadows;
layout(binding = 7) uniform sampler2D previousFrame;
layout(binding = 8) uniform sampler2D sceneDepth;
layout(binding = 9) uniform sampler2D gBufferIndirect;

uniform float SSRFalloff;
uniform float stepSizeSSR;
uniform float maxSSSDistance;
uniform float SSSDepthThickness;
uniform float SSSEdgeAttenuation;
uniform float SSSDepthDelta;
uniform float SSAOFalloff;
uniform int maxStepsSSR;
uniform int maxStepsSSS;
uniform int lightCount;

uniform bool sunEnabled;
uniform bool useScreenSpaceShadows;

// MATERIAL SETTINGS
float anisotropicRotation;
float anisotropy;
float clearCoat;
float sheen;
float sheenTint;
int renderingMode;
bool ssrEnabled;

// LIGHTS
vec3 albedoOverPI;
vec3 VrN;
vec2 brdf;
vec3 F0 = vec3(0.04);
float NdotV;

// MATERIAL VALUES
float naturalAO;
float roughness;
float metallic;
vec3 albedo;


// GLOBAL DATA
vec2 quadUV;
float distanceFromCamera;
vec3 V;
vec2 texCoords;
vec3 viewSpacePosition;
vec3 worldSpacePosition;
vec3 N;
float depthData;

#include "../util/SCENE_DEPTH_UTILS.glsl"

#include "../util/RAY_MARCHER.glsl"

#include "../util/STRONG_BLUR.glsl"

#include "../uber/MATERIAL_INFO.glsl"

#include "../util/BRDF_FUNCTIONS.glsl"

#include "../util/SSS.glsl"

#include "../buffer_objects/LIGHT_METADATA_SSBO.glsl"

#include "../uber/LIGHTS.glsl"

out vec4 color;

void main() {
    quadUV = gl_FragCoord.xy / bufferResolution;

    depthData = getLogDepth(quadUV);
    if (depthData == 1.){
        discard;
    }
    vec4 albedoEmissive = texture(gBufferAlbedoSampler, quadUV);
    if (albedoEmissive.a > 0) { // EMISSION
        color = vec4(albedoEmissive.rgb, 1.);
        return;
    }

    vec3 valueMaterialSampler = texture(gBufferMaterialSampler, quadUV).rgb;
    unpackValues(
    valueMaterialSampler,
    anisotropicRotation,
    anisotropy,
    clearCoat,
    sheen,
    sheenTint,
    renderingMode,
    ssrEnabled
    );

    albedo = texture(gBufferAlbedoSampler, quadUV).rgb;
    N = normalize(texture(gBufferNormalSampler, quadUV).rgb);
    vec3 valueRMAOSampler = texture(gBufferRMAOSampler, quadUV).rgb;
    naturalAO = valueRMAOSampler.b;
    roughness = valueRMAOSampler.r;
    metallic = valueRMAOSampler.g;
    viewSpacePosition = viewSpacePositionFromDepth(depthData, quadUV);
    worldSpacePosition = vec3(invViewMatrix * vec4(viewSpacePosition, 1.));
    V = normalize(cameraWorldPosition.xyz - worldSpacePosition);
    distanceFromCamera = length(V);
    vec3 lightContribution = pbLightComputation(lightCount, sunEnabled, useScreenSpaceShadows);
    color = vec4(lightContribution + sampleIndirectLight(gBufferIndirect), 1.);
}


