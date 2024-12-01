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
#define UV_FLAG 18
#define INDIRECT 19
#define TRIANGLE_ID 20
#define HEIGHT 21
#define MATERIAL_MASK 22
#define LIT -1


layout (location = 0) out vec4 gBufferAlbedoSampler;
layout (location = 1) out vec4 gBufferNormalSampler;
layout (location = 2) out vec4 gBufferRMAOSampler;

// X channel: 16 bits for anisotropicRotation + 16 bits for anisotropy
// Y channel: 16 bits for clearCoat + 16 bits for sheen
// Z channel: 16 bits for sheenTint + 15 bits for renderingMode + 1 bit for ssrEnabled
// W channel: 32 bit render index
layout (location = 3) out vec4 gBufferMaterialSampler;
layout (location = 4) out vec4 gBufferDepthSampler;
layout (location = 5) out vec4 gBufferIndirect;

uniform float probeFilteringLevels;
uniform int debugShadingMode;
layout (binding = 0) uniform samplerCube specularProbe;
layout (binding = 1) uniform samplerCube irradianceProbe;
layout (binding = 2) uniform samplerCube irradianceProbe1;

#define PARALLAX_THRESHOLD 200.

void sampleIndirectIllumination(inout vec3 V, inout vec3 N){
    vec3 R = reflect(-V, N);
    float specularFactor = 1.0 - gBufferRMAOSampler.r;
    vec3 combinedReflection = mix(
    texture(irradianceProbe, R).rgb,
    textureLod(specularProbe, R, gBufferRMAOSampler.r * probeFilteringLevels).rgb,
    specularFactor);
    gBufferIndirect = vec4(combinedReflection, 1);
}

vec3 randomColor(int seed) {
    float hash = fract(sin(float(seed)) * 43758.5453);

    float r = fract(hash * 13.756);
    float g = fract(hash * 15.734);
    float b = fract(hash * 17.652);

    return vec3(r, g, b);
}

void processDebugFlags(inout vec2 UV, inout vec3 W, int renderingIndex, float distanceFromCamera, vec4 materialMask){
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
            gBufferAlbedoSampler.rgb = vec3(distanceFromCamera) / 10;
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
            gBufferAlbedoSampler.rgb = vec3(W);
            break;
            case UV_FLAG:
            gBufferAlbedoSampler.rgb = vec3(UV, 0);
            break;
            case INDIRECT:
            gBufferAlbedoSampler.rgb = gBufferIndirect.rgb;
            break;
            case TRIANGLE_ID:
            gBufferAlbedoSampler.rgb = randomColor(gl_PrimitiveID);
            break;
            case HEIGHT:
            gBufferAlbedoSampler.rgb = vec3(W.y/10);
            break;
            case MATERIAL_MASK:
            gBufferAlbedoSampler.rgb = materialMask.rgb;
            break;
        }

        gBufferAlbedoSampler.a = debugShadingMode != LIGHT_ONLY ? 1 : 0;
    }
}

float encode(float depthFunc, float val) {
    float half_co = depthFunc * 0.5;
    float clamp_z = max(0.000001, val);
    return log2(clamp_z) * half_co;
}

mat3 computeTBN(vec3 worldPosition, vec2 initialUV, vec3 normalVec, int isDecalPass) {
    if (isDecalPass == 1) {
        vec3 N = abs(normalVec);
        vec3 T = vec3(0., 0., 1.);
        if (N.z > N.x && N.z > N.y){
            T = vec3(1., 0., 0.);
        }

        T = normalize(T - N * dot(T, N));
        vec3 B = cross(T, N);
        return mat3(T, B, N);
    }
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

vec2 parallaxOcclusionMapping(vec2 initialUV, vec3 worldSpacePosition, sampler2D heightMap, float heightScale, int layers, float distanceFromCamera, mat3 TBN) {
    if (distanceFromCamera > PARALLAX_THRESHOLD) return initialUV;
    mat3 transposed = transpose(TBN);
    vec3 viewDirection = normalize(transposed * (cameraWorldPosition.xyz - worldSpacePosition.xyz));
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