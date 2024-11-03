layout (local_size_x = 4, local_size_y = 4) in;

layout (rgba8, binding = 0) uniform image2D outputImage;
layout (rgba8, binding = 1) writeonly uniform image2D targetImage;
layout (binding = 2) uniform sampler2D sceneDepth;

uniform vec3 xyDown;
uniform vec2 targetImageSize;
uniform vec3 radiusDensityMode;
uniform vec2 viewportOrigin;
uniform vec2 viewportSize;

#include "../util/SCENE_DEPTH_UTILS.glsl"
#include "../util/UTIL.glsl"

float hash(float seed) {
    return fract(sin(seed * 0.1) * 43758.5453);
}
bool randomBoolean(float density) {
    float uniqueSeed = gl_GlobalInvocationID.x + gl_GlobalInvocationID.y * 10000.0;
    float randValue = hash(uniqueSeed);
    float scaledValue = pow(randValue, 1.0 / (1.0 - density));
    return scaledValue < density;
}

void main() {
    // RECONSTRUCT POSITION BASED ON MOUSE POSITION
    vec2 textureCoord = (xyDown.xy + viewportOrigin) / viewportSize;
    float depthData = getLogDepth(textureCoord);
    if (depthData == 1.){
        return;
    }
    vec3 viewSpacePositionMouse = viewSpacePositionFromDepth(depthData, textureCoord);
    vec3 worldSpacePositionMouse = vec3(invViewMatrix * vec4(viewSpacePositionMouse, 1.));


    // RECONSTRUCT POSITION BASED ON FRAGMENT POSITION
    textureCoord = vec2(gl_GlobalInvocationID.xy / bufferResolution);
    vec4 depthIdUVData = texture(sceneDepth, textureCoord);
    depthData = getLogDepthFromSampler(depthIdUVData);
    if (depthData == 1.){
        return;
    }
    vec3 viewSpacePosition = viewSpacePositionFromDepth(depthData, textureCoord);
    vec3 worldSpacePosition = vec3(invViewMatrix * vec4(viewSpacePosition, 1.));

    worldSpacePositionMouse.y = worldSpacePosition.y = 0;

    // COMPARE THE DISTANCE BETWEEN THE PIXEL WORLD POSITION AND THE MOUSE WORLD POSITION
    float distToCenter = length(worldSpacePosition - worldSpacePositionMouse);
    if (distToCenter > radiusDensityMode.r) {
        return;
    }

    vec4 srcColor = vec4(1, .5, .5, radiusDensityMode.y);
    ivec2 pixelPos = ivec2(gl_GlobalInvocationID.xy);
    vec4 dstColor = vec4(imageLoad(outputImage, pixelPos).rgb, 1);

    vec3 blendedRGB = srcColor.rgb * srcColor.a + dstColor.rgb * (1.0 - srcColor.a);
    float blendedAlpha = srcColor.a + dstColor.a * (1.0 - srcColor.a);
    vec4 outColor = vec4(blendedRGB, blendedAlpha);

    imageStore(outputImage, pixelPos, outColor);
    if (xyDown.b == 1.){
        if(radiusDensityMode.y == 1 || randomBoolean(radiusDensityMode.y * radiusDensityMode.y)){
            ivec2 uvScaled = ivec2(targetImageSize * depthIdUVData.ba);
            imageStore(targetImage, uvScaled, radiusDensityMode.z < 1 ? vec4(0, 0, 0, 1) : vec4(1, 0, 0, 1));
        }
    }
}