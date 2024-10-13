#include "../uber/SHADOWS.glsl"

#define DIRECTIONAL 1
#define SPOT 2
#define POINT 3
#define SPHERE 4
#define DISK 5
#define PLANE 6

#define COMMON_LIGHT_INFO_OFFSET 12
#define DIRECTIONAL_SPACE 32
#define SPOT_SPACE 15
#define POINT_SPACE 15
#define SPHERE_SPACE 13
#define DISK_SPACE 12
#define PLANE_SPACE 12


struct LightSharedInfo{
    mat4 viewProjection;
    vec3 translation;
    vec3 lightDirection;
    vec3 color;
    vec2 lightAttenuation;
    vec2 atlasFace;
    float outerCutoff;
    float cutoff;
    float areaLightRadius;
    float spotLightRadius;
    float shadowBias;
    float shadowAttenuationMinDistance;
    bool screenSpaceShadows;
    bool shadowMap;
    int pointShadowSamples;
    float zFar;
};

LightSharedInfo unifiedLightSharedInfo = LightSharedInfo(mat4(0.), vec3(0.), vec3(0.), vec3(0.), vec2(0.), vec2(0.), 0., 0., 0., 0., 0., 0., false, false, 0, 0.);

vec4 precomputeContribution(vec3 lightPosition) {
    vec3 L = normalize(lightPosition - worldSpacePosition);
    float NdotL = max(dot(N, L), 0.0);
    if (NdotL <= 0.) return vec4(0.);
    return vec4(L, NdotL);
}

vec3 computeLightContribution(vec4 baseContribution, LightSharedInfo info, vec3 lightPosition){
    float occlusion = info.screenSpaceShadows ? screenSpaceShadows(lightPosition) : 1.;
    if (occlusion == 0.){
        return vec3(0.);
    }

    float distanceFromFrag = length(info.translation - worldSpacePosition);
    float intensity = 1.;
    if (distanceFromFrag > info.cutoff) {
        intensity = clamp(mix(1., 0., (distanceFromFrag - info.cutoff) / (info.outerCutoff - info.cutoff)), 0., 1.);
    }
    vec2 att = 1./info.lightAttenuation;
    float attFactor = intensity / (1. + (att.x * distanceFromFrag) + (att.y * pow(distanceFromFrag, 2.)));
    if (attFactor == 0.){
        return vec3(0.);
    }
    return computeBRDF(baseContribution.rgb, baseContribution.a, info.color) * attFactor;
}

vec3 computeDirectionalLight(LightSharedInfo info){
    vec4 baseContribution = precomputeContribution(info.translation);
    if (baseContribution.a == 0.) return vec3(0.);

    float shadows = 1.;
    //    if (info.shadowMap){
    //        vec4 lightSpacePosition  = info.viewProjection * vec4(worldSpacePosition, 1.0);
    //        shadows = directionalLightShadows(distanceFromCamera, info.shadowAttenuationMinDistance, info.shadowBias, lightSpacePosition, info.atlasFace, shadowAtlas, shadowMapsQuantity, shadowMapResolution, pcfSamples);
    //    }
    if (shadows == 0.) {
        return vec3(0.);
    }

    return computeLightContribution(baseContribution, info, info.translation);
}


vec3 computeSpotLights (LightSharedInfo info) {
    vec3 offset = info.translation - worldSpacePosition;
    vec3 L = normalize(offset);

    vec4 baseContribution = precomputeContribution(info.translation);
    if (baseContribution.a == 0.) return vec3(0.);

    float theta = dot(L, normalize(info.lightDirection));
    if (theta <= info.spotLightRadius) return vec3(0.);

    return computeLightContribution(baseContribution, info, info.lightDirection);
}

vec3 computePointLights(LightSharedInfo info) {
    vec4 baseContribution = precomputeContribution(info.translation);
    if (baseContribution.a == 0.) return vec3(0.);

    if (info.shadowMap) {
        float shadows = pointLightShadow(distanceFromCamera, info.shadowAttenuationMinDistance, info.translation, info.shadowBias, info.zFar, info.pointShadowSamples);
        if (shadows == 0.) return vec3(0.);
    }

    return computeLightContribution(baseContribution, info, info.translation - worldSpacePosition);
}

vec3 computeSphereLight(LightSharedInfo info){
    vec3 L = info.translation - worldSpacePosition;
    vec3 centerToRay        = dot(L, VrN) * VrN - L;
    vec3 closestPoint        = L + centerToRay * clamp(info.areaLightRadius / length(centerToRay), 0.0, 1.0);
    vec4 baseContribution = precomputeContribution(closestPoint + worldSpacePosition);

    if (baseContribution.a == 0.){
        return vec3(0.);
    }

    return computeLightContribution(baseContribution, info, L);
}

vec3 processLight(inout int attributeOffset) {
    int type = int(lightMetadata[attributeOffset]);
    attributeOffset++;
    unifiedLightSharedInfo.color.x = lightMetadata[attributeOffset];
    attributeOffset++;
    unifiedLightSharedInfo.color.y = lightMetadata[attributeOffset];
    attributeOffset++;
    unifiedLightSharedInfo.color.z = lightMetadata[attributeOffset];
    attributeOffset++;
    unifiedLightSharedInfo.translation.x = lightMetadata[attributeOffset];
    attributeOffset++;
    unifiedLightSharedInfo.translation.y = lightMetadata[attributeOffset];
    attributeOffset++;
    unifiedLightSharedInfo.translation.z = lightMetadata[attributeOffset];
    attributeOffset++;
    unifiedLightSharedInfo.outerCutoff = lightMetadata[attributeOffset];
    attributeOffset++;
    unifiedLightSharedInfo.lightAttenuation.x = lightMetadata[attributeOffset];
    attributeOffset++;
    unifiedLightSharedInfo.lightAttenuation.y = lightMetadata[attributeOffset];
    attributeOffset++;
    unifiedLightSharedInfo.screenSpaceShadows = lightMetadata[attributeOffset] == 1.? true : false;
    attributeOffset++;
    unifiedLightSharedInfo.cutoff = lightMetadata[attributeOffset];
    attributeOffset++;

    switch (type){
        case DIRECTIONAL: {
            float array[16];

            unifiedLightSharedInfo.atlasFace.x = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.atlasFace.y = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.shadowMap = lightMetadata[attributeOffset] == 1.;
            attributeOffset++;
            unifiedLightSharedInfo.shadowBias = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.shadowAttenuationMinDistance = lightMetadata[attributeOffset];
            attributeOffset++;

            unifiedLightSharedInfo.viewProjection[0][0] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[0][1] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[0][2] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[0][3] = lightMetadata[attributeOffset];
            attributeOffset++;

            unifiedLightSharedInfo.viewProjection[1][0] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[1][1] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[1][2] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[1][3] = lightMetadata[attributeOffset];
            attributeOffset++;

            unifiedLightSharedInfo.viewProjection[2][0] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[2][1] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[2][2] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[2][3] = lightMetadata[attributeOffset];
            attributeOffset++;

            unifiedLightSharedInfo.viewProjection[3][0] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[3][1] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[3][2] = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.viewProjection[3][3] = lightMetadata[attributeOffset];
            attributeOffset++;
            return computeDirectionalLight(unifiedLightSharedInfo);
        }
        case POINT: {

            unifiedLightSharedInfo.zFar = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.shadowMap = lightMetadata[attributeOffset] == 1.;
            attributeOffset++;
            unifiedLightSharedInfo.shadowAttenuationMinDistance = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.shadowBias = lightMetadata[attributeOffset];
            attributeOffset++;
            return computePointLights(unifiedLightSharedInfo);
        }
        case SPOT:{

            unifiedLightSharedInfo.lightDirection.x = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.lightDirection.y = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.lightDirection.z = lightMetadata[attributeOffset];
            attributeOffset++;
            unifiedLightSharedInfo.spotLightRadius = lightMetadata[attributeOffset];
            attributeOffset++;
            return computeSpotLights(unifiedLightSharedInfo);
        }
        case SPHERE:{
            unifiedLightSharedInfo.areaLightRadius = lightMetadata[attributeOffset];
            attributeOffset++;
            return computeSphereLight(unifiedLightSharedInfo);
        }
    }

    return vec3(0);
}

vec3 pbLightComputation(int lightCount) {
    VrN = reflect(-V, N);
    albedoOverPI = albedo / PI;
    vec3 indirectIllumination = vec3(0.0);
    float ao = distanceFromCamera < SSAOFalloff ? (1 - naturalAO) * texture(SSAO, quadUV).r : naturalAO;
    NdotV = clamp(dot(N, V), 0., 1.);
    brdf = texture(brdfSampler, vec2(NdotV, roughness)).rg;
    F0 = mix(F0, albedo, metallic);

    vec3 directIllumination = vec3(0.0);
    int attributeOffset = 0;
    for (int i = 0; i < lightCount; i++) {
        directIllumination += processLight(attributeOffset);
    }

    indirectIllumination = sampleIndirectLight();
    return vec3(directIllumination);
}

