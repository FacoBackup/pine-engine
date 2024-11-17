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


shared vec4 l;
shared vec4 r;
shared vec4 b;
shared vec4 t;
shared vec4 n;
shared vec4 f;

bool isPointInsideFrustum(vec3 point) {
    vec4 p4D = vec4(point, 1.);
    return (dot(l, p4D) >= 0) &&
    (dot(r, p4D) <= 0) &&
    (dot(t, p4D) <= 0) &&
    (dot(b, p4D) >= 0) &&
    (dot(n, p4D) >= 0) &&
    (dot(f, p4D) <= 0);
}

void main() {
    if (gl_LocalInvocationIndex == 0){
        mat4 m = transpose(viewProjection);
        vec4 Row1 = vec4(m[0][0], m[0][1],m[0][2],m[0][3]);
        vec4 Row2 = vec4(m[1][0], m[1][1],m[1][2],m[1][3]);
        vec4 Row3 = vec4(m[2][0], m[2][1],m[2][2],m[2][3]);
        vec4 Row4 = vec4(m[3][0], m[3][1],m[3][2],m[3][3]);

        l = Row1 + Row4;
        r = Row1 - Row4;
        b = Row2 + Row4;
        t = Row2 - Row4;
        n = Row3 + Row4;
        f = Row3 - Row4;
    }

    vec2 scaledTexCoord= vec2(gl_GlobalInvocationID.xy) / imageSize;
    vec3 worldSpaceCoord = heightMapToWorldSpace(scaledTexCoord, imageSize);
    if (isPointInsideFrustum(worldSpaceCoord)){
        vec3 pixelColor = texture(foliageMask, scaledTexCoord).rgb;
        if (pixelColor == colorToMatch){
            float localHeight = texture(heightMap, scaledTexCoord).r * heightScale;
            worldSpaceCoord.y = localHeight;
            float distanceFromCamera = length(worldSpaceCoord.xyz - cameraWorldPosition.xyz);
            int N = 0;

            if (distanceFromCamera < MAX_DISTANCE_FROM_CAMERA/8){
                N = int(MAX_ITERATIONS);
            } else if (distanceFromCamera < MAX_DISTANCE_FROM_CAMERA/4){
                N = int(max(MAX_ITERATIONS, 2)/2);
            } else if (distanceFromCamera < MAX_DISTANCE_FROM_CAMERA/2){
                N = int(max(MAX_ITERATIONS, 4)/4);
            } else if (distanceFromCamera < MAX_DISTANCE_FROM_CAMERA){
                N = int(max(MAX_ITERATIONS, 8)/8);
            }

            vec2 prefOffset = vec2(0);
            for (int i = 0; i < N; i++){
                for (int j = 0; j < N; j++){
                    prefOffset = randomOffset(vec2(i, j), INSTANCE_OFFSET_X, INSTANCE_OFFSET_Y);
                    vec3 localPos = worldSpaceCoord + vec3(prefOffset.x, 0, prefOffset.y);
                    float localHeight = texture(heightMap, vec2(localPos.x - terrainOffset.x, localPos.z - terrainOffset.y)/imageSize).r * heightScale;
                    localPos.y = localHeight;
                    if (isPointInsideFrustum(localPos)){
                        addTransform(localPos);
                    }
                }
            }
        }
    }
}