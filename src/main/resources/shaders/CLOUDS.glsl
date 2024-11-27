layout(binding = 0) uniform sampler3D uShapeNoise;
layout(binding = 1) uniform sampler3D uDetailNoise;

uniform float densityMultiplier;
uniform float cloudCoverage;
uniform float scale;
uniform float detailNoiseScale;
uniform float cloudErosionStrength;
uniform vec4 phaseParams;
uniform int numStepsLight;
uniform float rayOffsetStrength;
uniform vec3 boundsMin;
uniform vec3 boundsMax;
uniform float lightAbsorptionTowardSun;
uniform float lightAbsorptionThroughCloud;
uniform float baseSpeed;
uniform float detailSpeed;

float remap(float v, float minOld, float maxOld, float minNew, float maxNew) {
    return minNew + (v-minOld) * (maxNew - minNew) / (maxOld-minOld);
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

float sampleDensity(vec3 rayPos, bool sampleDetail) {
    // Constants:
    const int mipLevel = 0;
    const float baseScale = 1/1000.0;
    const float offsetSpeed = 1/100.0;

    vec3 size = boundsMax - boundsMin;
    vec3 boundsCentre = (boundsMin+boundsMax) * .5;
    vec3 uvw = (size * .5 + rayPos) * baseScale * scale;
    vec3 shapeSamplePos = uvw + offsetSpeed + vec3(timeOfDay, timeOfDay*0.1, timeOfDay*0.2) * baseSpeed;

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
    float shapeFBM = dot(shapeNoise, vec4(0.25)) * heightGradient;
    float baseShapeDensity = shapeFBM + cloudCoverage * .1;

    // Save sampling from detail tex if shape density <= 0
    if (baseShapeDensity > 0) {
        // Sample detail noise

        float detailFBM = 0;
        if(sampleDetail){
            vec3 detailSamplePos = uvw*detailNoiseScale + offsetSpeed + vec3(timeOfDay*.4, -timeOfDay, timeOfDay*0.1)*detailSpeed;
            vec3 detailNoise = texture(uDetailNoise, detailSamplePos, mipLevel).rgb;
            detailFBM = dot(detailNoise, vec3(.25));
        }

        // Subtract detail noise from base shape (weighted by inverse density so that edges get eroded more than centre)
        float oneMinusShape = 1 - shapeFBM;
        float detailErodeWeight = oneMinusShape * oneMinusShape * oneMinusShape;
        float cloudDensity = baseShapeDensity - (1-detailFBM) * detailErodeWeight * cloudErosionStrength;

        return cloudDensity * densityMultiplier * 0.1;
    }
    return 0;
}

// Calculate proportion of light that reaches the given point from the lightsource
float lightmarch(vec3 position) {
    vec3 dirToLight = normalize(sunLightDirection.xyz);
    float dstInsideBox = rayBoxDst(boundsMin, boundsMax, position, 1/dirToLight).y;

    float stepSize = dstInsideBox/numStepsLight;
    float totalDensity = 0;

    for (int step = 0; step < numStepsLight; step ++) {
        position += dirToLight * stepSize;
        totalDensity += max(0, sampleDensity(position, step < 2) * stepSize);
    }

    return exp(-totalDensity * lightAbsorptionTowardSun);
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



float beer_law(float density)
{
    float d = -density * lightAbsorptionTowardSun;
    return max(exp(d), exp(d * 0.5f) * 0.7f);
}

// ------------------------------------------------------------------
float u_HenyeyGreensteinGForward = 0.4f;
float u_HenyeyGreensteinGBackward = 0.179f;
float henyey_greenstein_phase(float cos_angle, float g)
{
    float g2 = g * g;
    return ((1.0f - g2) / pow(1.0f + g2 - 2.0f * g * cos_angle, 1.5f)) / 4.0f * 3.1415f;
}
float powder_effect(float _density, float _cos_angle)
{
    float powder = 1.0f - exp(-_density * 2.0f);
    return mix(1.0f, powder, clamp((-_cos_angle * 0.5f) + 0.5f, 0.0f, 1.0f));
}
float calculate_light_energy(float _density, float _cos_angle, float _powder_density)
{
    float beer_powder = 2.0f * beer_law(_density) * powder_effect(_powder_density, _cos_angle);
    float HG = max(henyey_greenstein_phase(_cos_angle, u_HenyeyGreensteinGForward), henyey_greenstein_phase(_cos_angle, u_HenyeyGreensteinGBackward)) * 0.07f + 0.8f;
    return beer_powder * HG;
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

    float randomOffset = blueNoise(texCoords) * rayOffsetStrength;

    float cosAngle = dot(rayDir, normalize(sunLightDirection.xyz));

    float dstTravelled = randomOffset;
    float dstLimit = min(dstToBox, dstInsideBox);


    const float stepSize = 11;

    // March through volume:
    float transmittance = 1;
    float alpha = 0;
    vec3 lightEnergy = vec3(0);
    while (dstTravelled < dstLimit) {
        rayPos = entryPoint + rayDir * dstTravelled;
        float density = sampleDensity(rayPos, true);
        if (density > 0) {
            float lightTransmittance = lightmarch(rayPos);
            float phaseVal = 1; // TODO
            alpha += (1.0f - transmittance) * (1.0f - alpha);
            lightEnergy += calculate_light_energy(density * stepSize, cosAngle, lightTransmittance * stepSize) * sunLightColor.rgb * alpha * transmittance * density;
            transmittance *= exp(-density * stepSize * lightAbsorptionThroughCloud);

            // Exit early if T is close to zero as further samples won't affect the result much
            if (transmittance < 0.01) {
                break;
            }
        }
        dstTravelled += stepSize;
    }
    return vec4(lightEnergy, 1 - transmittance);
}