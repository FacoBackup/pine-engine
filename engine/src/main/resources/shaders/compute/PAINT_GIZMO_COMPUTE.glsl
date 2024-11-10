layout (local_size_x = 4, local_size_y = 4) in;

#define TERRAIN 0
#define FOLIAGE 1
#define MATERIAL 2

layout (rgba8, binding = 0) uniform image2D outputImage;
layout (rgba8, binding = 1) uniform image2D targetImage;
layout (binding = 2) uniform sampler2D sceneDepth;

uniform int paintMode;
uniform float heightScale;
uniform vec2 xyMouse;
uniform vec3 colorForPainting;
uniform vec2 targetImageSize;
uniform vec3 radiusDensityMode;
uniform vec3 terrainLocation;


const vec4 NONE = vec4(0, 0, 0, 1);
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
    vec2 textureCoord = xyMouse.xy;
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

    int wX = int(floor(worldSpacePosition.x/terrainLocation.z));
    int wZ = int(floor(worldSpacePosition.z/terrainLocation.z));
    if (wX == int(terrainLocation.x) && wZ == int(terrainLocation.y)){
        float originalYPosition = worldSpacePosition.y;
        worldSpacePositionMouse.y = worldSpacePosition.y = 0;

        // COMPARE THE DISTANCE BETWEEN THE PIXEL WORLD POSITION AND THE MOUSE WORLD POSITION
        float distToCenter = length(worldSpacePosition - worldSpacePositionMouse);
        if (distToCenter > radiusDensityMode.r) {
            return;
        }

        ivec2 uvScaled = ivec2(targetImageSize * depthIdUVData.ba);
        if (paintMode == TERRAIN){
            float scale =  (distToCenter/radiusDensityMode.x) * radiusDensityMode.y;
            imageStore(targetImage, uvScaled, radiusDensityMode.z < 1 ? vec4(vec3(originalYPosition - scale) / heightScale, 1) : vec4(vec3(originalYPosition + .1 * scale)/heightScale, 1));
        } else if (paintMode == FOLIAGE && randomBoolean(radiusDensityMode.y * radiusDensityMode.y)){
            imageStore(targetImage, uvScaled, radiusDensityMode.z < 1 ? NONE : vec4(colorForPainting, 1));
        }
    }
}