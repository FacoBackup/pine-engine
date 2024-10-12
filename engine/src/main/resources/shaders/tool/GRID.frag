in vec3 worldPosition;
in vec3 cameraPosition;

uniform vec4 settings;

#include "../util/SCENE_DEPTH_UTILS.glsl"

layout (location = 0) out vec3  gBufferAlbedoSampler;
layout (location = 3) out ivec4 gBufferMaterialSampler;// R isEmission | G useSSR | B useGI | A useAO

float grid(float space, float gridWidth, float scale) {
    vec2 p = worldPosition.xz * scale - vec2(.5);
    vec2 size = vec2(gridWidth);

    vec2 a1 = mod(p - size, space);
    vec2 a2 = mod(p + size, space);
    vec2 a = a2 - a1;

    float g = min(a.x, a.y);
    return clamp(g, 0., 1.0);
}


void main() {
    vec2 quadUV = gl_FragCoord.xy / bufferResolution;
    float color = settings.x;
    float scale = settings.y * 4.5;
    float threshold = max(10., settings.z);
    //    float opacityScale = clamp(settings.w / 2., 0., 1.);

    float distanceFromCamera = length(cameraPosition - worldPosition);
    if (distanceFromCamera > threshold)
    discard;

    float depth = getLogDepth(quadUV);
    if (depth - gl_FragCoord.z <= .001 && depth > 0.) discard;

    //    float opacity = distanceFromCamera >= threshold/2. ? abs(distanceFromCamera - threshold) / ((distanceFromCamera + threshold) / 2.) : 1.;

    float smallerGrid = grid(10., 0.2, scale);
    float biggerGrid = grid(50., .4, scale);
    float gridValue = clamp(biggerGrid * smallerGrid, .1, 1.0);
    if (gridValue != .1){
        discard;
    }

    float lineScale = .4 / scale;
    float offset = .5 / scale;
    float Z = worldPosition.z - offset;
    float X = worldPosition.x - offset;

    if (Z < lineScale && Z > -lineScale)
    gBufferAlbedoSampler = vec3(1., 0., 0.);
    else if (X < lineScale && X > -lineScale)
    gBufferAlbedoSampler = vec3(0., 0., 1.);
    else {
        float s = abs(abs(biggerGrid) - abs(smallerGrid));
        if (s < .1 && s >= .0){
            gBufferAlbedoSampler = vec3(color/2.);
        } else {
            gBufferAlbedoSampler = vec3(color);
        }
    }
    gBufferMaterialSampler = ivec4(1, 0, 0, 0);
}