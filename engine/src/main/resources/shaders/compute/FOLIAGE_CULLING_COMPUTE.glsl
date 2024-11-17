layout (local_size_x = 1, local_size_y = 1) in;

layout (binding = 0) uniform sampler2D foliageMask;
layout (binding = 1) uniform sampler2D heightMap;
layout (binding = 2, offset = 0) uniform atomic_uint globalIndex;

layout(std430, binding = 3) writeonly buffer TransformationBuffer {
    vec3 transformations[];
};

uniform vec2 terrainOffset;
uniform vec3 colorToMatch;
uniform vec4 settings;
uniform vec2 imageSize;
uniform float heightScale;

#define MAX_DISTANCE_FROM_CAMERA settings.x
#define MAX_ITERATIONS settings.y
#define INSTANCE_OFFSET_X settings.z
#define INSTANCE_OFFSET_Y settings.w
#define MAX_INSTANCING  500000

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"
#include "../util/UTIL.glsl"

vec3 heightMapToWorldSpace(vec2 uv, vec2 planeSize) {
    float worldX = uv.x * planeSize.x + terrainOffset.x;
    float worldZ = uv.y * planeSize.y + terrainOffset.y;
    return vec3(worldX, 0, worldZ);
}

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

vec2 randomOffset(vec2 inputV, float rangeX, float rangeY) {
    float randomX = hash(inputV.xy / rangeX);
    float randomY = hash(inputV.yx / rangeY);
    return vec2(randomX* rangeX, randomY* rangeY);
}

void addTransform(vec3 pos){
    if (atomicCounter(globalIndex) + 1 < MAX_INSTANCING){
        uint index = atomicCounterIncrement(globalIndex);
        transformations[index] = pos;
    }
}

void main() {
    vec2 scaledTexCoord= vec2(gl_GlobalInvocationID.xy) / imageSize;
    vec3 worldSpaceCoord = heightMapToWorldSpace(scaledTexCoord, imageSize);

    // TODO - FRUSTUM CULLING BASED ON WORLD COORD
    vec3 pixelColor = texture(foliageMask, scaledTexCoord).rgb;
    if (pixelColor == colorToMatch){
        float localHeight = texture(heightMap, scaledTexCoord).r * heightScale;
        worldSpaceCoord.y = localHeight;
        float distanceFromCamera = MAX_DISTANCE_FROM_CAMERA/length(worldSpaceCoord.xyz - cameraWorldPosition.xyz);
        int N = int(clamp(distanceFromCamera, 0, MAX_ITERATIONS));

        vec2 prefOffset = vec2(0);
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                prefOffset = randomOffset(vec2(i, j), INSTANCE_OFFSET_X, INSTANCE_OFFSET_Y);
                addTransform(worldSpaceCoord + vec3(prefOffset.x, localHeight, prefOffset.y));
            }
        }
    }
}