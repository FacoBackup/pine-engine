layout (local_size_x = 4, local_size_y = 4) in;

layout (rgba8, binding = 0) uniform image2D outputImage;
layout (rgba8, binding = 1) uniform image2D targetImage;
layout (binding = 2) uniform sampler2D sceneDepth;
layout (binding = 3) uniform sampler2D gBufferNormal;

uniform vec3 xyDown;
uniform vec2 targetImageSize;
uniform vec3 radiusDensityMode;
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

float hash(float seed) {
    return fract(sin(seed * 0.1) * 43758.5453);
}

// Function to return true or false based on the density value
bool randomBoolean(float density) {
    // Create a unique seed based on the global invocation ID
    float uniqueSeed = gl_GlobalInvocationID.x + gl_GlobalInvocationID.y * 10000.0; // Modify as needed for uniqueness
    float randValue = hash(uniqueSeed); // Generate random value

    // Scale the random value to match the density
    float scaledValue = pow(randValue, 1.0 / (1.0 - density)); // Exponential scaling

    return scaledValue < density; // Return true if the scaled value is less than density
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

    vec3 rayOrigin = placement.xyz;
    vec3 rayDirection = createRay();
    vec3 normal = texture(gBufferNormal, textureCoord).rgb;
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
            imageStore(targetImage, uvScaled, radiusDensityMode.b == 0 ? vec4(0) : vec4(1, 0, 0, 1));
        }
    }
}