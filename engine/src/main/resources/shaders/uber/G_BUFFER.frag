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
#define UV_FLAG 18
#define INDIRECT_DIFFUSE 19
#define INDIRECT_SPECULAR 20
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

layout (binding = 0) uniform samplerCube specularProbe;
layout (binding = 1) uniform samplerCube diffuseProbe0;
layout (binding = 2) uniform samplerCube diffuseProbe1;
layout (binding = 3) uniform sampler2D albedo;
layout (binding = 4) uniform sampler2D roughness;
layout (binding = 5) uniform sampler2D metallic;
layout (binding = 6) uniform sampler2D ao;
layout (binding = 7) uniform sampler2D normal;
layout (binding = 8) uniform sampler2D heightMap;


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

#include "../uber/G_BUFFER_UTIL.glsl"

#include "../uber/MATERIAL_INFO.glsl"

void main() {
    vec2 UV = initialUV;
    vec3 V = cameraPlacement.xyz - worldSpacePosition;
    float distanceFromCamera = length(V);
    mat3 TBN = computeTBN(worldSpacePosition);
    if (useParallax){
        UV = parallaxOcclusionMapping(heightMap, parallaxHeightScale, parallaxLayers, distanceFromCamera, TBN);
    }
    vec3 N = normalVec;
    gBufferDepthSampler = vec4(encode(depthFunc), 0, 0, 1);
    if (!fallbackMaterial){
        gBufferAlbedoSampler = vec4(texture(albedo, UV).rgb, 0);
        N = vec3(normalize(TBN * ((texture(normal, UV).rgb * 2.0)- 1.0)));
        gBufferRMAOSampler = vec4(texture(roughness, UV).r, texture(metallic, UV).r, 1 - texture(ao, UV).r, 1);
        gBufferMaterialSampler = vec4(packValues(anisotropicRotation, anisotropy, clearCoat, sheen, sheenTint, renderingMode, ssrEnabled), 1);
    } else {
        gBufferAlbedoSampler = vec4(vec3(.5), 0);
        gBufferRMAOSampler = vec4(.5, .5, 1, 1);
        gBufferMaterialSampler = vec4(packValues(0, 0, 0, 0, 0, ISOTROPIC, false), 1);
    }
    gBufferNormalSampler = vec4(N, 1);
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
            case UV_FLAG:
            gBufferAlbedoSampler.rgb = vec3(UV, 0);
            break;
            case INDIRECT_SPECULAR:
            gBufferAlbedoSampler.rgb = texture(specularProbe, reflect(-V, N)).rgb;
            break;
            case INDIRECT_DIFFUSE:
            gBufferAlbedoSampler.rgb = texture(diffuseProbe0, reflect(-V, N)).rgb * .5;
            break;
        }
        gBufferAlbedoSampler.a = debugShadingMode != LIGHT_ONLY ? 1 : 0;
    }
}