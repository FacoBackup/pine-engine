layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba16f) uniform readonly image2D gBufferMetallicRoughnessAO;
layout (binding = 1, rgba16f) uniform readonly image2D gBufferAlbedoEmissive;
layout (binding = 2, rgba16f) uniform readonly image2D gBufferNormal;
layout (binding = 3, r16f) uniform readonly image2D gBufferDepth;
layout (binding = 4) uniform writeonly image2D outputImage;
layout (binding = 5, rg16f) uniform readonly image2D brdfSampler;
layout (binding = 6, r16f) uniform readonly image2D shadowAtlas;
layout (binding = 7) uniform samplerCube shadowCube;

#include "../buffer_objects/LIGHT_METADATA_SSBO.glsl"
#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"

float getLogDepth(vec2 uv) {
    float half_co = logDepthFC * .5;
    float exponent = imageLoad(gBufferDepth, ivec2(uv * bufferResolution)).r / half_co;
    return pow(2.0, exponent);
}

float fetchLogDepth(ivec2 coords) {
    float half_co = logDepthFC * .5;
    float exponent = imageLoad(gBufferDepth, coords).r / half_co;
    return pow(2.0, exponent);
}

vec3 viewSpacePositionFromDepth(float logarithimicDepth, vec2 texCoords) {
    float z = logarithimicDepth * 2.0 - 1.0;
    return viewSpacePositionFromDepthLinear(z);
}

vec3 viewSpacePositionFromDepthLinear(float z, vec2 texCoords) {
    vec4 clipSpacePosition = vec4(texCoords * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = invProjectionMatrix * clipSpacePosition;
    viewSpacePosition /= viewSpacePosition.w;

    return viewSpacePosition.rgb;
}

vec3 normalFromDepth(float logarithimicDepth, vec2 texCoords) {
    vec2 texelSize = 1. / bufferResolution;
    vec2 texCoords1 = texCoords + vec2(0., 1.) * texelSize;
    vec2 texCoords2 = texCoords + vec2(1., 0.) * texelSize;

    float depth1 = getLogDepth(texCoords1);
    float depth2 = getLogDepth(texCoords2);

    vec3 P0 = viewSpacePositionFromDepth(logarithimicDepth, texCoords);
    vec3 P1 = viewSpacePositionFromDepth(depth1, texCoords1);
    vec3 P2 = viewSpacePositionFromDepth(depth2, texCoords2);

    return normalize(cross(P2 - P0, P1 - P0));
}
#define MAX_LIGHTS 310

#define PI 3.14159265359
#define FRAG_DEPTH_THRESHOLD .001
#define DECAL_DEPTH_THRESHOLD 1.

#define PARALLAX_THRESHOLD 200.
#define CLAMP_MIN .1
#define CLAMP_MAX .9
#define SEARCH_STEPS 5
#define DEPTH_THRESHOLD 1.2
#define PI2 6.2831853

#define UNLIT 0
#define ISOTROPIC 1
#define ANISOTROPIC 2
#define SHEEN 3
#define CLEAR_COAT 4
#define TRANSPARENCY 5
#define SKY 6

uniform int lightCount;
uniform float pcfSamples;
uniform float elapsedTime;
uniform bool isDecalPass;
uniform float shadowMapsQuantity;
uniform float shadowMapResolution;
uniform float SSRFalloff;
uniform float stepSizeSSR;
uniform float maxSSSDistance;
uniform float SSSDepthThickness;
uniform float SSSEdgeAttenuation;
uniform float SSSDepthDelta;
uniform float SSAOFalloff;
uniform int maxStepsSSR;
uniform int maxStepsSSS;
uniform bool hasAmbientOcclusion;
uniform bool ssrEnabled;
uniform int renderingMode;
uniform float anisotropicRotation;
uniform float anisotropy;
uniform float clearCoat;
uniform float sheen;
uniform float sheenTint;
uniform bool useAlbedoDecal;
uniform bool useMetallicDecal;
uniform bool useRoughnessDecal;
uniform bool useNormalDecal;
uniform bool useOcclusionDecal;


float naturalAO = 1.;
float roughness = .5;
float metallic = .5;
float refractionIndex = 0.;
float alpha = 1.;
vec3 albedo = vec3(.5);

mat3 TBN;
vec3 T;
vec3 B;
vec3 N;

vec3 emission = vec3(0.);
vec3 albedoOverPI;
vec3 VrN;
vec2 brdf;
vec3 F0 = vec3(0.04);
float NdotV;
vec2 texelSize;
bool flatShading = false;
bool alphaTested;

vec2 quadUV;
vec3 viewDirection;
bool hasTBNComputed = false;
bool hasViewDirectionComputed = false;
float distanceFromCamera;
vec3 V;
vec2 texCoords;
vec3 viewSpacePosition;
vec3 worldSpacePosition;
vec3 normalVec;
float depthData;

#include "../util/STRONG_BLUR.glsl"
#include "../util/RAY_MARCHER.glsl"
#include "../util/SSS.glsl"
#include "../util/BRDF_FUNCTIONS.glsl"
#include "../uber/SHADOWS.glsl"
#include "../uber/LIGHTS.glsl"
#include "../util/PARALLAX_OCCLUSION_MAPPING.glsl"

void main() {
    ivec2 coords = ivec2(gl_GlobalInvocationID.xy);
    normalVec = imageLoad(gBufferNormal, coords).xyz;
    depthData = fetchLogDepth(coords);
    viewSpacePosition = viewSpacePositionFromDepth(depthData, quadUV);
    worldSpacePosition = invViewMatrix * vec4(viewSpacePosition, 1.);
    texCoords = naturalTextureUV;

    V = placement.xyz - worldSpacePosition;
    distanceFromCamera = length(V);
    V = normalize(V);

    imageStore(outputImage, coords, vec4(pbLightComputation(lightCount).rgb, 1.));
}

