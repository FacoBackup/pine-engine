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
} unifiedLightSharedInfo;

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
    float attFactor = intensity / (1. + (info.lightAttenuation.x * distanceFromFrag) + (info.lightAttenuation.y * pow(distanceFromFrag, 2.)));
    if (attFactor == 0.){
        return vec3(0.);
    }
    return computeBRDF(baseContribution.rgb, baseContribution.a, info.color) * attFactor;
}

vec3 computeDirectionalLight(LightSharedInfo info){
    vec4 baseContribution = precomputeContribution(info.translation);
    if (baseContribution.a == 0.) return vec3(0.);

    float shadows = 1.;
    if (info.shadowMap){
        vec4 lightSpacePosition  = info.viewProjection * vec4(worldSpacePosition, 1.0);
        shadows = directionalLightShadows(distanceFromCamera, info.shadowAttenuationMinDistance, info.shadowBias, lightSpacePosition, info.atlasFace, shadowAtlas, shadowMapsQuantity, shadowMapResolution, pcfSamples);
    }
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
    lightMetadata++;
    vec3 directIllumination = vec3(0.);

    int type = int(lightMetadata[attributeOffset]);
    unifiedLightSharedInfo.color.x = lightMetadata[attributeOffset + 1];
    unifiedLightSharedInfo.color.y = lightMetadata[attributeOffset + 2];
    unifiedLightSharedInfo.color.z = lightMetadata[attributeOffset + 3];
    unifiedLightSharedInfo.translation.x = lightMetadata[attributeOffset + 4];
    unifiedLightSharedInfo.translation.y = lightMetadata[attributeOffset + 5];
    unifiedLightSharedInfo.translation.z = lightMetadata[attributeOffset + 6];
    unifiedLightSharedInfo.outerCutoff = lightMetadata[attributeOffset + 7];
    unifiedLightSharedInfo.lightAttenuation.x = lightMetadata[attributeOffset + 8];
    unifiedLightSharedInfo.lightAttenuation.y = lightMetadata[attributeOffset + 9];
    unifiedLightSharedInfo.screenSpaceShadows = lightMetadata[attributeOffset + 10] == 1.? true : false;
    unifiedLightSharedInfo.cutoff = lightMetadata[attributeOffset + 11];

    if (type == DIRECTIONAL){
        directIllumination += computeDirectionalLight(unifiedLightSharedInfo);
        float array[16];

        unifiedLightSharedInfo.atlasFace.x = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 0];
        unifiedLightSharedInfo.atlasFace.y = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 1];
        unifiedLightSharedInfo.shadowMap = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 2] == 1.;
        unifiedLightSharedInfo.shadowBias = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 3];
        unifiedLightSharedInfo.shadowAttenuationMinDistance = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 4];

        unifiedLightSharedInfo.viewProjection[0][0] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 5];
        unifiedLightSharedInfo.viewProjection[0][1] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 6];
        unifiedLightSharedInfo.viewProjection[0][2] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 7];
        unifiedLightSharedInfo.viewProjection[0][3] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 8];

        unifiedLightSharedInfo.viewProjection[1][0] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 9];
        unifiedLightSharedInfo.viewProjection[1][1] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 10];
        unifiedLightSharedInfo.viewProjection[1][2] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 11];
        unifiedLightSharedInfo.viewProjection[1][3] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 12];

        unifiedLightSharedInfo.viewProjection[2][0] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 13];
        unifiedLightSharedInfo.viewProjection[2][1] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 14];
        unifiedLightSharedInfo.viewProjection[2][2] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 15];
        unifiedLightSharedInfo.viewProjection[2][3] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 16];

        unifiedLightSharedInfo.viewProjection[3][0] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 17];
        unifiedLightSharedInfo.viewProjection[3][1] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 18];
        unifiedLightSharedInfo.viewProjection[3][2] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 19];
        unifiedLightSharedInfo.viewProjection[3][3] = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 20];

        attributeOffset += DIRECTIONAL_SPACE;
    }
    else if (type == POINT) {
        directIllumination += computePointLights(unifiedLightSharedInfo);

        unifiedLightSharedInfo.zFar = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 0];
        unifiedLightSharedInfo.shadowMap = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 1] == 1.;
        unifiedLightSharedInfo.shadowAttenuationMinDistance = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 2];
        unifiedLightSharedInfo.shadowBias = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 3];

        attributeOffset += POINT_SPACE;
    }
    else if (type == SPOT){
        directIllumination += computeSpotLights(unifiedLightSharedInfo);

        unifiedLightSharedInfo.lightDirection.x = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 0];
        unifiedLightSharedInfo.lightDirection.y = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 1];
        unifiedLightSharedInfo.lightDirection.z = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 2];
        unifiedLightSharedInfo.spotLightRadius = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET + 3];

        attributeOffset += SPOT_SPACE;
    }
    else if (type == SPHERE){
        directIllumination += computeSphereLight(unifiedLightSharedInfo);

        unifiedLightSharedInfo.areaLightRadius = lightMetadata[attributeOffset + COMMON_LIGHT_INFO_OFFSET];

        attributeOffset += SPHERE_SPACE;
    }

    return directIllumination;
}

vec4 pbLightComputation(int lightCount) {
    if (flatShading) return vec4(albedo + emission, 1.);
    VrN = reflect(-V, N);
    albedoOverPI = albedo / PI;
    vec3 indirectIllumination = vec3(0.0);
    float ao = hasAmbientOcclusion && distanceFromCamera < SSAOFalloff ? naturalAO * texture(SSAO, quadUV).r : naturalAO;
    if (renderingMode == ANISOTROPIC)
    computeTBN();
    NdotV = clamp(dot(N, V), 0., 1.);
    brdf = texture(brdfSampler, vec2(NdotV, roughness)).rg;
    F0 = mix(F0, albedo, metallic);

    vec3 directIllumination = vec3(0.0);
    int attributeOffset = 0;
    for (int i = 0; i < lightCount; i++) {
        directIllumination += processLight(attributeOffset);
    }

    indirectIllumination = sampleIndirectLight();

    return vec4((directIllumination + indirectIllumination) * ao + emission * albedo, alpha);
}

