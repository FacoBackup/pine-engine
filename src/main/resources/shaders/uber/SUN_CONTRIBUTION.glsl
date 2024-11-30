// DIRECTIONAL
float sampleShadowMap (vec2 coord, float compare, sampler2D shadowMapTexture){
    return step(compare, texture(shadowMapTexture, coord.xy).r);
}

float sampleShadowMapLinear (vec2 coord, float compare, sampler2D shadowMapTexture, vec2 shadowTexelSize){
    vec2 pixelPos = coord.xy/shadowTexelSize + vec2(0.5);
    vec2 fracPart = fract(pixelPos);
    vec2 startTexel = (pixelPos - fracPart) * shadowTexelSize;

    float bottomLeftTexel = sampleShadowMap(startTexel, compare, shadowMapTexture);
    float bottomRightTexel = sampleShadowMap(startTexel + vec2(shadowTexelSize.x, 0.0), compare, shadowMapTexture);
    float topLeftTexel = sampleShadowMap(startTexel + vec2(0.0, shadowTexelSize.y), compare, shadowMapTexture);
    float topRightTexel = sampleShadowMap(startTexel + vec2(shadowTexelSize.x, shadowTexelSize.y), compare, shadowMapTexture);


    float mixOne = mix(bottomLeftTexel, topLeftTexel, fracPart.y);
    float mixTwo = mix(bottomRightTexel, topRightTexel, fracPart.y);

    return mix(mixOne, mixTwo, fracPart.x);
}

float sampleSoftShadows(vec2 coord, float compare, sampler2D shadowMapTexture, float shadowMapResolution, float pcfSamples){
    float SAMPLES_START = (pcfSamples -1.0)/2.0;
    float SAMPLES_SQUARED = pcfSamples * pcfSamples;

    vec2 shadowTexelSize = vec2(1.0/shadowMapResolution, 1.0/shadowMapResolution);
    float response = 0.0;

    for (float y= -SAMPLES_START; y <= SAMPLES_START; y+=1.0){
        for (float x= -SAMPLES_START; x <= SAMPLES_START; x+=1.0){
            vec2 coordsOffset = vec2(x, y)*shadowTexelSize;
            response += sampleShadowMapLinear(coord + coordsOffset, compare, shadowMapTexture, shadowTexelSize);
        }
    }
    return response/SAMPLES_SQUARED;
}

float directionalLightShadows(float bias, vec4 lightSpacePosition, sampler2D shadowMapTexture, float shadowMapResolution, float pcfSamples){
    float response = 1.0;
    vec3 pos = (lightSpacePosition.xyz / lightSpacePosition.w)* 0.5 + 0.5;

    if (pos.z > 1.0)
    pos.z = 1.0;

    float compare = pos.z - bias;
    response = sampleSoftShadows(pos.xy, compare, shadowMapTexture, shadowMapResolution, pcfSamples);
    if (response < 1.){
        return min(1., response);
    }
    return response;
}

vec3 computeDirectionalLight(bool useScreenSpaceShadows){
    vec4 baseContribution = precomputeContribution(sunLightDirection.xyz);
    if (baseContribution.a == 0.) return vec3(0.);

    vec4 lightSpacePosition  = lightSpaceMatrix * vec4(sunLightDirection.xyz, 1.0);
    float shadows = directionalLightShadows(.01, lightSpacePosition, sunShadows, sunShadowsResolution, 2);
    if (shadows == 0.) {
        return vec3(0.);
    }

    float occlusion = useScreenSpaceShadows ? screenSpaceShadows(sunLightDirection.xyz) : 1.;
    if (occlusion == 0.){
        return vec3(0.);
    }
    return computeBRDF(baseContribution.rgb, baseContribution.a, sunLightColor.rgb * sunLightIntensity);
}