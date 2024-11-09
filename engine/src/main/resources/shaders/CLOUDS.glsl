layout(binding = 0) uniform sampler3D uShapeNoise;
layout(binding = 1) uniform sampler3D uDetailNoise;

uniform float densityMultiplier;
uniform float densityOffset;
uniform float scale;
uniform float detailNoiseScale;
uniform float detailNoiseWeight;
uniform vec3 detailWeights;
uniform vec4 shapeNoiseWeights;
uniform vec4 phaseParams;
uniform int numStepsLight;
uniform float rayOffsetStrength;
uniform vec3 boundsMin;
uniform vec3 boundsMax;
uniform vec3 shapeOffset;
uniform vec3 detailOffset;
uniform float lightAbsorptionTowardSun;
uniform float lightAbsorptionThroughCloud;
uniform float darknessThreshold;
uniform float baseSpeed;
uniform float detailSpeed;

float remap(float v, float minOld, float maxOld, float minNew, float maxNew) {
    return minNew + (v-minOld) * (maxNew - minNew) / (maxOld-minOld);
}

vec2 squareUV(vec2 uv) {
    float width = bufferResolution.x;
    float height = bufferResolution.y;
    //float minDim = min(width, height);
    float scale = 1000;
    float x = uv.x * width;
    float y = uv.y * height;
    return vec2 (x/scale, y/scale);
}

// Returns (dstToBox, dstInsideBox). If ray misses box, dstInsideBox will be zero
// Adapted from: http://jcgt.org/published/0007/03/04/
vec2 rayBoxDst(vec3 boundsMin, vec3 boundsMax, vec3 rayOrigin, vec3 invRaydir) {

    vec3 t0 = (boundsMin - rayOrigin) * invRaydir;
    vec3 t1 = (boundsMax - rayOrigin) * invRaydir;
    vec3 tmin = min(t0, t1);
    vec3 tmax = max(t0, t1);

    float dstA = max(max(tmin.x, tmin.y), tmin.z);
    float dstB = min(tmax.x, min(tmax.y, tmax.z));

    float dstToBox = max(0, dstA);
    float dstInsideBox = max(0, dstB - dstToBox);
    return vec2(dstToBox, dstInsideBox);
}

// Henyey-Greenstein
float hg(float a, float g) {
    float g2 = g*g;
    return (1-g2) / (4*3.1415*pow(1+g2-2*g*(a), 1.5));
}

float phase(float a) {
    float blend = .5;
    float hgBlend = hg(a, phaseParams.x) * (1-blend) + hg(a, -phaseParams.y) * blend;
    return phaseParams.z + hgBlend*phaseParams.w;
}

float remap01(float v, float low, float high) {
    return (v-low)/(high-low);
}

float sampleDensity(vec3 rayPos) {
    // Constants:
    const int mipLevel = 0;
    const float baseScale = 1/1000.0;
    const float offsetSpeed = 1/100.0;

    vec3 size = boundsMax - boundsMin;
    vec3 boundsCentre = (boundsMin+boundsMax) * .5;
    vec3 uvw = (size * .5 + rayPos) * baseScale * scale;
    vec3 shapeSamplePos = uvw + shapeOffset * offsetSpeed + vec3(timeOfDay, timeOfDay*0.1, timeOfDay*0.2) * baseSpeed;

    // Calculate falloff at along x/z edges of the cloud container
    const float containerEdgeFadeDst = 50;
    float dstFromEdgeX = min(containerEdgeFadeDst, min(rayPos.x - boundsMin.x, boundsMax.x - rayPos.x));
    float dstFromEdgeZ = min(containerEdgeFadeDst, min(rayPos.z - boundsMin.z, boundsMax.z - rayPos.z));
    float edgeWeight = min(dstFromEdgeZ, dstFromEdgeX)/containerEdgeFadeDst;

    // Calculate height gradient from weather map
    float gMin = .2;
    float gMax = .7;
    float heightPercent = (rayPos.y - boundsMin.y) / size.y;
    float heightGradient = clamp(remap(heightPercent, 0.0, gMin, 0, 1), 0.0, 1.0) * clamp(remap(heightPercent, 1, gMax, 0, 1), 0.0, 1.0);
    heightGradient *= edgeWeight;

    // Calculate base shape density
    vec4 shapeNoise = texture(uShapeNoise, shapeSamplePos, mipLevel);
    vec4 normalizedShapeWeights = shapeNoiseWeights / dot(shapeNoiseWeights, vec4(1));
    float shapeFBM = dot(shapeNoise, normalizedShapeWeights) * heightGradient;
    float baseShapeDensity = shapeFBM + densityOffset * .1;

    // Save sampling from detail tex if shape density <= 0
    if (baseShapeDensity > 0) {
        // Sample detail noise

        vec3 detailSamplePos = uvw*detailNoiseScale + detailOffset * offsetSpeed + vec3(timeOfDay*.4, -timeOfDay, timeOfDay*0.1)*detailSpeed;
        vec3 detailNoise = texture(uDetailNoise, detailSamplePos, mipLevel).rgb;
        vec3 normalizedDetailWeights = detailWeights / dot(detailWeights, vec3(1));
        float detailFBM = dot(detailNoise, normalizedDetailWeights);

        // Subtract detail noise from base shape (weighted by inverse density so that edges get eroded more than centre)
        float oneMinusShape = 1 - shapeFBM;
        float detailErodeWeight = oneMinusShape * oneMinusShape * oneMinusShape;
        float cloudDensity = baseShapeDensity - (1-detailFBM) * detailErodeWeight * detailNoiseWeight;

        return cloudDensity * densityMultiplier * 0.1;
    }
    return 0;
}

// Calculate proportion of light that reaches the given point from the lightsource
float lightmarch(vec3 position) {
    vec3 dirToLight = sunLightDirection.xyz;
    float dstInsideBox = rayBoxDst(boundsMin, boundsMax, position, 1/dirToLight).y;

    float stepSize = dstInsideBox/numStepsLight;
    float totalDensity = 0;

    for (int step = 0; step < numStepsLight; step ++) {
        position += dirToLight * stepSize;
        totalDensity += max(0, sampleDensity(position) * stepSize);
    }

    float transmittance = exp(-totalDensity * lightAbsorptionTowardSun);
    return darknessThreshold + transmittance * (1-darknessThreshold);
}

// Hash function for pseudo-random numbers
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

// Blue noise approximation function
float blueNoise(vec2 uv) {
    vec2 i = floor(uv);
    vec2 f = fract(uv);

    // Weighted random values around the pixel
    float a = hash(i + vec2(0.0, 0.0));
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));

    // Bilinear interpolation to smooth transitions
    vec2 blend = smoothstep(0.0, 1.0, f);
    return mix(mix(a, b, blend.x), mix(c, d, blend.x), blend.y);
}

vec4 computeClouds(vec3 rayDir){
    // Create ray
    vec3 rayPos = cameraWorldPosition.xyz;

    vec2 rayToContainerInfo = rayBoxDst(boundsMin, boundsMax, rayPos, 1/rayDir);
    float dstToBox = rayToContainerInfo.x;
    float dstInsideBox = rayToContainerInfo.y;

    if (dstInsideBox == 0){
        return vec4(0);
    }

    vec3 entryPoint = rayPos + rayDir * dstToBox;

    float randomOffset = blueNoise(texCoords);
    randomOffset *= rayOffsetStrength;

    float cosAngle = dot(rayDir, sunLightDirection.xyz);
    float phaseVal = phase(cosAngle);

    float dstTravelled = randomOffset;
    float dstLimit = min(dstToBox, dstInsideBox);


    const float stepSize = 11;

    // March through volume:
    float transmittance = 1;
    vec3 lightEnergy = vec3(0);

    while (dstTravelled < dstLimit) {
        rayPos = entryPoint + rayDir * dstTravelled;
        float density = sampleDensity(rayPos);

        if (density > 0) {
            float lightTransmittance = lightmarch(rayPos);
            lightEnergy += density * stepSize * transmittance * lightTransmittance * phaseVal;
            transmittance *= exp(-density * stepSize * lightAbsorptionThroughCloud);

            // Exit early if T is close to zero as further samples won't affect the result much
            if (transmittance < 0.01) {
                break;
            }
        }
        dstTravelled += stepSize;
    }
    return vec4(lightEnergy * sunLightColor, 1 - transmittance);
}