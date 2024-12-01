#define PI 3.14159265
#define ATMOSPHERE_RADIUS 6420e3
#define PLANET_RADIUS 6360e3
#define THRESHOLD -.1f
#define RAYLEIGH_HEIGHT 7994.0
#define MIE_HEIGHT 700.
#define INTENSITY 20.
#define SAMPLES 10
#define RAYLEIGH_BETA vec3(3.8e-6, 13.5e-6, 33.1e-6)
#define MIE_BETA vec3(210e-5, 210e-5, 210e-5)
#define G 0.76

float mu;
float muSquared;
vec3 sunDirection;


float raySphereIntersect(vec3 rayOrigin, vec3 rayDirection, vec3 sphereCenter, float sphereRadius) {
    float a = dot(rayDirection, rayDirection);
    vec3 d = rayOrigin - sphereCenter;
    float b = 2.0 * dot(rayDirection, d);
    float c = dot(d, d) - (sphereRadius * sphereRadius);
    if (b * b - 4.0 * a * c < 0.0) return -1.0;
    return (-b + sqrt((b * b) - 4.0 * a * c)) / (2.0 * a);
}

float rayleighPhase() {
    float phase = (3.0 / (16.0 * PI)) * (1.0 + muSquared);
    return phase;
}

float miePhase() {
    float squaredG = G * G;
    float numerator = (1.0 - squaredG) * (1.0 + muSquared);
    float denominator = (2.0 + squaredG) * pow(1.0 + squaredG - 2.0 * G * mu, 3.0 / 2.0);
    return (3.0 / (8.0 * PI)) * numerator / denominator;
}

vec3 scatteringAtHeight(vec3 scatteringAtSea, float height, float heightScale) {
    return scatteringAtSea * exp(-height / heightScale);
}

float getSampleHeight(vec3 point) {
    return (length(point) - PLANET_RADIUS);
}

vec3 transmittance(vec3 pa, vec3 pb, int samples, float scaleHeight, vec3 scatCoeffs) {
    float opticalDepth = 0.0;
    float segmentLength = length(pb - pa) / float(samples);
    for (int i = 0; i < samples; i++) {
        vec3 samplePoint = mix(pa, pb, (float(i) + 0.5) / float(samples));
        float sampleHeight = getSampleHeight(samplePoint);
        opticalDepth += exp(-sampleHeight / scaleHeight) * segmentLength;
    }
    vec3 transmittance = exp(-1.0 * scatCoeffs * opticalDepth);
    return transmittance;
}


vec3 getSkyColor(vec3 pa, vec3 pb) {
    mu = dot(normalize(pb - pa), sunDirection);
    muSquared = pow(mu, 2.);

    float phaseR = rayleighPhase();
    float phaseM = miePhase();
    vec3 rayleighColor = vec3(0.0, 0.0, 0.0);
    vec3 mieColor = vec3(0.0, 0.0, 0.0);
    float fSamples = float(SAMPLES);
    float segmentLength = length(pb - pa) / fSamples;

    for (int i = 0; i < SAMPLES; i++) {

        vec3 samplePoint = mix(pa, pb, (float(i) + 0.5) / fSamples);
        float sampleHeight = getSampleHeight(samplePoint);
        float distanceToAtmosphere = raySphereIntersect(samplePoint, sunDirection, vec3(0.0, 0.0, 0.0), ATMOSPHERE_RADIUS);
        vec3 atmosphereIntersect = samplePoint + sunDirection * distanceToAtmosphere;

        vec3 trans1R = transmittance(pa, samplePoint, 10, RAYLEIGH_HEIGHT, RAYLEIGH_BETA);
        vec3 trans2R = transmittance(samplePoint, atmosphereIntersect, 10, RAYLEIGH_HEIGHT, RAYLEIGH_BETA);
        rayleighColor += trans1R * trans2R * scatteringAtHeight(RAYLEIGH_BETA, sampleHeight, RAYLEIGH_HEIGHT) * segmentLength;

        vec3 trans1M = transmittance(pa, samplePoint, 10, MIE_HEIGHT, MIE_BETA);
        vec3 trans2M = transmittance(samplePoint, atmosphereIntersect, 10, MIE_HEIGHT, MIE_BETA);
        mieColor += trans1M * trans2M * scatteringAtHeight(MIE_BETA, sampleHeight, MIE_HEIGHT) * segmentLength;
    }

    rayleighColor =  phaseR * rayleighColor;
    mieColor = phaseM * mieColor;

    return rayleighColor + mieColor;
}

vec3 computeAtmpshere(vec3 rayDir){
    sunDirection = normalize(sunLightDirection.xyz);

    if (rayDir.y >= THRESHOLD) {
        vec3 origin = vec3(0.0, PLANET_RADIUS + 1.0, 0.0);

        float distanceToAtmosphere = raySphereIntersect(origin, rayDir, vec3(0.0, 0.0, 0.0), ATMOSPHERE_RADIUS);
        vec3 atmosphereIntersect = origin + rayDir * distanceToAtmosphere;
        return getSkyColor(origin, atmosphereIntersect) * INTENSITY;
    }
    return vec3(0);
}
