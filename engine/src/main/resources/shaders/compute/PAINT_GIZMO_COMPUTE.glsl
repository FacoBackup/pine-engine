layout (local_size_x = 4, local_size_y = 4) in;

layout (rgba8, binding = 0) uniform image2D outputImage;
layout (rgba8, binding = 1) uniform image2D targetImage;
layout (binding = 2) uniform sampler2D sceneDepth;
layout (binding = 3) uniform sampler2D gBufferNormal;

uniform vec3 xyDown;
uniform vec2 targetImageSize;
uniform vec2 radiusDensity;
uniform vec2 viewportOrigin;
uniform vec2 viewportSize;
const vec3 UP_VEC = vec3(0.0, 1.0, 0.0);// Default dome "up" direction

#include "../util/SCENE_DEPTH_UTILS.glsl"
#include "../util/UTIL.glsl"

vec3 createRay() {
    vec2 pxNDS = (gl_GlobalInvocationID.xy/bufferResolution) * 2. - 1.;
    vec3 pointNDS = vec3(pxNDS, -1.);
    vec4 pointNDSH = vec4(pointNDS, 1.0);
    vec4 dirEye = invProjectionMatrix * pointNDSH;
    dirEye.w = 0.;
    vec3 dirWorld = (invViewMatrix * dirEye).xyz;
    return normalize(dirWorld);
}

float domeSDF(vec3 pos, vec3 domeCenter, float domeRadius, mat3 rotationMatrix) {
    pos -= domeCenter;

    // Rotate the position
    pos = rotationMatrix * pos;

    const float t = 0.01;
    const float h = 0;
    vec2 q = vec2(length(pos.xz), -pos.y);
    float w = sqrt(domeRadius * domeRadius);
    return ((h * q.x < w * q.y) ? length(q - vec2(w, h)) : abs(length(q) - domeRadius)) - t;
}

bool renderDome(vec3 rayOrigin, vec3 rayDir, vec3 domeCenter, float domeRadius, mat3 rotationMatrix) {
    const int maxSteps = 100;
    const float minDist = 0.001;
    const float maxDist = 100.0;

    float t = 0.0;
    for (int i = 0; i < maxSteps; i++) {
        vec3 p = rayOrigin + t * rayDir;
        float d = domeSDF(p, domeCenter, domeRadius, rotationMatrix);
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

    vec2 textureCoord = (xyDown.xy + viewportOrigin) / viewportSize;
    vec4 depthIdUVData = texture(sceneDepth, textureCoord);
    float depthData = getLogDepthFromSampler(depthIdUVData);
    if (depthData == 1.){
        return;
    }

    vec3 normal = normalize(texture(gBufferNormal, textureCoord).rgb);
    vec3 viewSpacePosition = viewSpacePositionFromDepth(depthData, textureCoord);
    vec3 worldSpacePosition = vec3(invViewMatrix * vec4(viewSpacePosition, 1.));

    if (renderDome(rayOrigin, rayDirection, worldSpacePosition, radiusDensity.x, getRotationFromNormal(normal))){
        vec4 srcColor = vec4(1, .5, .5, radiusDensity.y);
        ivec2 pixelPos = ivec2(gl_GlobalInvocationID.xy);
        vec4 dstColor = vec4(imageLoad(outputImage, pixelPos).rgb, 1);

        vec3 blendedRGB = srcColor.rgb * srcColor.a + dstColor.rgb * (1.0 - srcColor.a);
        float blendedAlpha = srcColor.a + dstColor.a * (1.0 - srcColor.a);
        vec4 outColor = vec4(blendedRGB, blendedAlpha);

        imageStore(outputImage, pixelPos, outColor);

        if(xyDown.b == 1.){
            ivec2 uvScaled = ivec2(targetImageSize * depthIdUVData.ba);
            imageStore(targetImage, uvScaled, vec4(1, 0, 0, 1));
        }
    }
}