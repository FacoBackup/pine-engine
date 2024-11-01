layout (local_size_x = 1, local_size_y = 1) in;

layout (binding = 0) uniform writeonly image2D outputImage;
layout (binding = 1) uniform sampler2D gBufferDepth;
layout (binding = 2) uniform sampler2D gBufferNormal;

uniform vec2 xy;
uniform vec2 viewportOrigin;
uniform vec2 viewportSize;

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"

vec3 createRay() {
    vec2 pxNDS = (gl_GlobalInvocationID.xy/bufferResolution) * 2. - 1.;
    vec3 pointNDS = vec3(pxNDS, -1.);
    vec4 pointNDSH = vec4(pointNDS, 1.0);
    vec4 dirEye = invProjectionMatrix * pointNDSH;
    dirEye.w = 0.;
    vec3 dirWorld = (invViewMatrix * dirEye).xyz;
    return normalize(dirWorld);
}

float domeSDF(vec3 pos, vec3 domeCenter, float domeRadius) {
    const float t = 0.01;
    const float h = 0;
    vec2 q = vec2(length(pos.xz), -pos.y);
    float w = sqrt(domeRadius * domeRadius);
    return ((h * q.x < w * q.y) ? length(q - vec2(w, h)) : abs(length(q) -domeRadius)) - t;
}

vec4 renderDome(vec3 rayOrigin, vec3 rayDir, vec3 domeCenter, float domeRadius) {
    const int maxSteps = 100;
    const float minDist = 0.001;
    const float maxDist = 100.0;

    float t = 0.0;
    for (int i = 0; i < maxSteps; i++) {
        vec3 p = rayOrigin + t * rayDir;
        float d = domeSDF(p, domeCenter, domeRadius);
        if (d < minDist) {
            return vec4(1.0, 0.5, 0.3, 1.0);// Color when hitting dome
        }
        if (t > maxDist) break;
        t += d;
    }
    return vec4(0.0);// Background color
}

void main() {
    vec3 rayOrigin = placement.xyz;
    vec3 rayDirection = createRay();

    vec2 aspect = viewportSize / bufferResolution;
    vec2 fragCoord = gl_GlobalInvocationID.xy * aspect;
    float dist = length(fragCoord - xy - viewportOrigin);

    if (dist < 10){
        //    vec4 outColor = renderDome(rayOrigin, rayDirection, vec3(10, 5, 0), 10);

        vec2 textureCoord = (xy + viewportOrigin) / viewportSize;

        vec3 color = texture(gBufferNormal, textureCoord).rgb;
        imageStore(outputImage, ivec2(gl_GlobalInvocationID.xy), vec4(color, 1));
    }
}