#include "../buffer_objects/LIGHT_METADATA_SSBO.glsl"

uniform int shadingModel;

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"
#include "../util/SCENE_DEPTH_UTILS.glsl"
#include "../uber/ATTRIBUTES.glsl"
#include "../util/STRONG_BLUR.glsl"
#include "../util/RAY_MARCHER.glsl"
#include "../util/SSS.glsl"
#include "../util/BRDF_FUNCTIONS.glsl"
#include "../uber/SHADOWS.glsl"
#include "../uber/LIGHTS.glsl"
#include "../util/PARALLAX_OCCLUSION_MAPPING.glsl"

#define ALBEDO 0
#define NORMAL 1
#define TANGENT 2
#define DEPTH 3
#define AO 4
#define DETAIL 5
#define LIGHT_ONLY 6
#define METALLIC 7
#define ROUGHNESS 8
#define G_AO 9
#define AMBIENT 10
#define POSITION 11
#define UV 12
#define RANDOM 13
#define OVERDRAW 14
#define LIGHT_COMPLEXITY 15
#define LIGHT_QUANTITY 16


float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 randomColor(float seed) {
    float r = rand(vec2(seed));
    float g = rand(vec2(seed + r));
    return vec3(r, g, rand(vec2(seed + g)));
}

void main() {
    if (isDecalPass) {
        if (depthData == 0.) discard;

        viewSpacePosition = viewSpacePositionFromDepth(depthData, quadUV);
        normalVec = normalize(vec3(invViewMatrix * vec4(normalFromDepth(depthData, quadUV), 0.)));
        worldSpacePosition = vec3(invViewMatrix * vec4(viewSpacePosition, 1.));
        vec3 objectSpacePosition = vec3(invModelMatrix * vec4(worldSpacePosition, 1.));
        texCoords = objectSpacePosition.xz * .5 + .5;

        bool inRange =
        objectSpacePosition.x >= -DECAL_DEPTH_THRESHOLD &&
        objectSpacePosition.x <= DECAL_DEPTH_THRESHOLD &&

        objectSpacePosition.y >= -DECAL_DEPTH_THRESHOLD &&
        objectSpacePosition.y <= DECAL_DEPTH_THRESHOLD &&

        objectSpacePosition.z >= -DECAL_DEPTH_THRESHOLD &&
        objectSpacePosition.z <= DECAL_DEPTH_THRESHOLD;

        if (!inRange) discard;
    } else {
        normalVec = naturalNormal;
        viewSpacePosition = viewSpacePositionFromDepth(gl_FragCoord.z, quadUV);
        worldSpacePosition = worldPosition;
        texCoords = naturalTextureUV;
    }

    V = placement.xyz - worldSpacePosition;
    distanceFromCamera = length(V);
    V = normalize(V);

    if (shadingModel == LIGHT_ONLY){
        albedo = vec3(1.);
        fragColor = pbLightComputation(lightCount);
    }
    else {
        switch (shadingModel) {
            case ALBEDO:
                fragColor = vec4(albedo, 1.);
                break;
            case NORMAL:
                fragColor = vec4(N, 1.);
                break;
            case DEPTH:
                fragColor = vec4(vec3(depthData), 1.);
                break;
            case G_AO:
                fragColor = vec4(vec3(naturalAO), 1.);
                break;
            case METALLIC:
                fragColor = vec4(vec3(metallic), 1.);
                break;
            case ROUGHNESS:
                fragColor = vec4(vec3(roughness), 1.);
                break;
            case AO:
                fragColor = vec4(vec3(hasAmbientOcclusion ? texture(SSAO, quadUV).r : 1.), 1.);
                break;
            case POSITION:
                fragColor = vec4(vec3(worldSpacePosition), 1.);
                break;
            case UV:
                fragColor = vec4(texCoords, 0., 1.);
                break;
            case RANDOM:
                fragColor = vec4(randomColor(length(renderIndex)), 1.);
                break;
            case LIGHT_QUANTITY:
            case LIGHT_COMPLEXITY:{
                    bool isLightQuantity = shadingModel == LIGHT_QUANTITY;
                    float total = isLightQuantity ? float(lightCount) : float(MAX_LIGHTS * 3);
                    float contribution = 0.;

                    if (!flatShading) {
                        viewSpacePosition = viewSpacePositionFromDepth(depthData, quadUV);
                        albedoOverPI = vec3(1.);
                        F0 = mix(F0, albedoOverPI, 0.);

                        int attributeOffset = 0;
                        for (int i = 0; i < lightCount; i++) {
                            if (length(processLight(attributeOffset)) > 0.){
                                contribution++;
                            }
                        }
                    }
                    if (total > 0.){
                        fragColor = vec4(mix(vec3(1., 0., 0.), vec3(0., .0, 1.), 1. - contribution / total), 1.);
                    }
                    else {
                        fragColor = vec4(0., 0., 1., 1.);
                    }
                    break;
                }
            case OVERDRAW:{
                    vec2 a = floor(gl_FragCoord.xy);
                    float checkerVal = 4.;

                    if (!alphaTested && abs(depthData - gl_FragCoord.z) > FRAG_DEPTH_THRESHOLD) {
                        fragColor = vec4(1., 0., 0., 1.);
                        checkerVal = 2.;
                    }
                    else
                    fragColor = vec4(0., 0., 1., 1.);

                    bool checker = mod(a.x + a.y, checkerVal) > 0.0;
                    if (checker) discard;

                    break;
                }
        }
    }
}


