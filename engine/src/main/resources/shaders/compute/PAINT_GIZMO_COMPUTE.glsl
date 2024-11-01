layout (local_size_x = 4, local_size_y = 4) in;

layout (binding = 0) uniform writeonly image2D outputImage;
layout (binding = 1) uniform sampler2D sceneDepth;
layout (binding = 2) uniform sampler2D gBufferNormal;

uniform vec2 xy;
uniform vec2 viewportOrigin;
uniform vec2 viewportSize;
const vec3 UP_VEC = vec3(0.0, 1.0, 0.0);// Default dome "up" direction

#include "../util/SCENE_DEPTH_UTILS.glsl"

vec3 createRay() {
    vec2 pxNDS = (gl_GlobalInvocationID.xy/bufferResolution) * 2. - 1.;
    vec3 pointNDS = vec3(pxNDS, -1.);
    vec4 pointNDSH = vec4(pointNDS, 1.0);
    vec4 dirEye = invProjectionMatrix * pointNDSH;
    dirEye.w = 0.;
    vec3 dirWorld = (invViewMatrix * dirEye).xyz;
    return normalize(dirWorld);
}

float domeSDF(vec3 pos, vec3 domeCenter, float domeRadius, vec3 dir) {
    pos -= domeCenter;
    // Compute the rotation matrix
    vec3 axis = cross(UP_VEC, dir);// Axis to rotate around
    float angle = acos(dot(UP_VEC, dir));// Angle between up and direction

    // Use Rodrigues' rotation formula components
    float cosA = cos(angle);
    float sinA = sin(angle);
    mat3 rotationMatrix = mat3(
        cosA + axis.x * axis.x * (1.0 - cosA), axis.x * axis.y * (1.0 - cosA) - axis.z * sinA, axis.x * axis.z * (1.0 - cosA) + axis.y * sinA,
        axis.y * axis.x * (1.0 - cosA) + axis.z * sinA, cosA + axis.y * axis.y * (1.0 - cosA), axis.y * axis.z * (1.0 - cosA) - axis.x * sinA,
        axis.z * axis.x * (1.0 - cosA) - axis.y * sinA, axis.z * axis.y * (1.0 - cosA) + axis.x * sinA, cosA + axis.z * axis.z * (1.0 - cosA)
    );

    // Rotate the position
    pos = rotationMatrix * pos;

    const float t = 0.01;
    const float h = 0;
    vec2 q = vec2(length(pos.xz), -pos.y);
    float w = sqrt(domeRadius * domeRadius);
    return ((h * q.x < w * q.y) ? length(q - vec2(w, h)) : abs(length(q) - domeRadius)) - t;
}

bool renderDome(vec3 rayOrigin, vec3 rayDir, vec3 domeCenter, float domeRadius, vec3 normal) {
    const int maxSteps = 100;
    const float minDist = 0.001;
    const float maxDist = 100.0;

    float t = 0.0;
    for (int i = 0; i < maxSteps; i++) {
        vec3 p = rayOrigin + t * rayDir;
        float d = domeSDF(p,  domeCenter, domeRadius, normal);
        if (d < minDist) {
            return true;
        }
        if (t > maxDist) break;
        t += d;
    }
    return false;
}

void main() {
    vec3 rayOrigin = placement.xyz;
    vec3 rayDirection = createRay();

    vec2 textureCoord = (xy + viewportOrigin) / viewportSize;
    float depthData = getLogDepth(textureCoord);
    if (depthData == 1.){
        return;
    }else{

    }

    vec3 normal = normalize(texture(gBufferNormal, textureCoord).rgb);
    vec3 viewSpacePosition = viewSpacePositionFromDepth(depthData, textureCoord);
    vec3 worldSpacePosition = vec3(invViewMatrix * vec4(viewSpacePosition, 1.));


    if (renderDome(rayOrigin, rayDirection, worldSpacePosition, 1, normal)){
        imageStore(outputImage, ivec2(gl_GlobalInvocationID.xy), vec4(1, 0, 1, .5));
    }
}