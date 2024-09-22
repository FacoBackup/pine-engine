//BASED ON https://www.shadertoy.com/view/wsfGWH

#define PI 3.14159265
#define ONLY_MIE 0
#define ONLY_RAYLEIGH 1
#define COMBINED  2

#include "./buffer_objects/CAMERA_VIEW_INFO.glsl"

in vec2 texCoords;
uniform mat4 invSkyProjectionMatrix;
uniform int type;
uniform float elapsedTime;
uniform vec3 rayleighBeta;
uniform vec3 mieBeta;
uniform float intensity;
uniform float atmosphereRadius;
uniform float planetRadius;
uniform float rayleighHeight;
uniform float mieHeight;
uniform float threshold;
uniform int samples;
out vec4 fragColor;

float mu;
float muSquared;

vec3 sunDirection;
vec3 rayleighBeta1 = vec3(rayleighBeta);
vec3 mieBeta1 = vec3(mieBeta);
float atmosphereRadius1 = atmosphereRadius;
float planetRadius1 = planetRadius;

float G = 0.76;


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
    return (length(point) - planetRadius1);
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
    float segmentLength = length(pb - pa) / float(samples);

    for (int i = 0; i < samples; i++) {

        vec3 samplePoint = mix(pa, pb, (float(i) + 0.5) / float(samples));
        float sampleHeight = getSampleHeight(samplePoint);
        float distanceToAtmosphere = raySphereIntersect(samplePoint, sunDirection, vec3(0.0, 0.0, 0.0), atmosphereRadius1);
        vec3 atmosphereIntersect = samplePoint + sunDirection * distanceToAtmosphere;

        if (type == ONLY_RAYLEIGH || type == COMBINED) {
            vec3 trans1R = transmittance(pa, samplePoint, 10, rayleighHeight, rayleighBeta1);
            vec3 trans2R = transmittance(samplePoint, atmosphereIntersect, 10, rayleighHeight, rayleighBeta1);
            rayleighColor += trans1R * trans2R * scatteringAtHeight(rayleighBeta1, sampleHeight, rayleighHeight) * segmentLength;
        }

        if (type == ONLY_MIE || type == COMBINED) {
            vec3 trans1M = transmittance(pa, samplePoint, 10, mieHeight, mieBeta1);
            vec3 trans2M = transmittance(samplePoint, atmosphereIntersect, 10, mieHeight, mieBeta1);
            mieColor += trans1M * trans2M * scatteringAtHeight(mieBeta1, sampleHeight, mieHeight) * segmentLength;
        }
    }

    rayleighColor = intensity * phaseR * rayleighColor;
    mieColor = intensity * phaseM * mieColor;

    return rayleighColor + mieColor;

}

vec3 createRay() {
    vec2 pxNDS = texCoords * 2. - 1.;
    vec3 pointNDS = vec3(pxNDS, -1.);
    vec4 pointNDSH = vec4(pointNDS, 1.0);
    vec4 dirEye = invSkyProjectionMatrix * pointNDSH;
    dirEye.w = 0.;
    vec3 dirWorld = (invViewMatrix * dirEye).xyz;
    return normalize(dirWorld);
}

void main() {
    rayleighBeta1.x *= 263157.;
    rayleighBeta1.y *= 74074.;
    rayleighBeta1.z *= 30211.;

    rayleighBeta1 = 1. / rayleighBeta1;

    mieBeta1 *= 476.;
    mieBeta1 = 1. / mieBeta1;

    atmosphereRadius1 *= 6420e3;
    planetRadius1 *= 6360e3;

    sunDirection = normalize(vec3(sin(elapsedTime), cos(elapsedTime), 1.0f));
    vec3 dir = createRay();

    fragColor = vec4(0., 0., 0., 1.);
    if (dir.y >= threshold) {
        vec3 origin = vec3(0.0, planetRadius1 + 1.0, 0.0);

        float distanceToAtmosphere = raySphereIntersect(origin, dir, vec3(0.0, 0.0, 0.0), atmosphereRadius1);
        vec3 atmosphereIntersect = origin + dir * distanceToAtmosphere;
        fragColor.rgb = getSkyColor(origin, atmosphereIntersect);
    }
}