// POINT

const vec3 sampleOffsetDirections[20] = vec3[]
(
vec3(1, 1, 1), vec3(1, -1, 1), vec3(-1, -1, 1), vec3(-1, 1, 1),
vec3(1, 1, -1), vec3(1, -1, -1), vec3(-1, -1, -1), vec3(-1, 1, -1),
vec3(1, 1, 0), vec3(1, -1, 0), vec3(-1, -1, 0), vec3(-1, 1, 0),
vec3(1, 0, 1), vec3(-1, 0, 1), vec3(1, 0, -1), vec3(-1, 0, -1),
vec3(0, 1, 1), vec3(0, -1, 1), vec3(0, -1, -1), vec3(0, 1, -1)
);
float pointLightShadow(float distanceFromCamera, float shadowFalloffDistance, vec3 lightPos, float bias, float zFar, int samples) {
//    float attenuation = clamp(mix(1., 0., shadowFalloffDistance - distanceFromCamera), 0., 1.);
//    if (attenuation == 1.) return 1.;
//
//    vec3 fragToLight = worldSpacePosition - lightPos;
//    float currentDepth = length(fragToLight) / zFar;
//    if (currentDepth > 1.)
//    currentDepth = 1.;
//
//    float shadow = 0.0;
//    float diskRadius = 0.05;
//    for (int i = 0; i < samples; ++i) {
//        float closestDepth = texture(shadowCube, fragToLight + sampleOffsetDirections[i] * diskRadius).r;
//        if (currentDepth - bias > closestDepth)
//        shadow += 1.0;
//    }
//    shadow /= float(samples);
//
//    float response = 1. - shadow;
//    if (response < 1.)
//    return min(1., response + attenuation);
    return 1;
}
