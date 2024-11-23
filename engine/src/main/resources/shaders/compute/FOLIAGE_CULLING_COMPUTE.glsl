layout (local_size_x = 4, local_size_y = 4) in;

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
shared int rows_per_thread;
shared int cols_per_thread;
const int N = 1024;

bool isPointInsideFrustum(vec3 point) {
    vec4 p4D = vec4(point, 1.);
    return (dot(l, p4D) >= 0) &&
    (dot(r, p4D) <= 0) &&
    (dot(t, p4D) <= 0) &&
    (dot(b, p4D) >= 0) &&
    (dot(n, p4D) >= 0) &&
    (dot(f, p4D) <= 0);
}

bool isEqual(float u, float v){
    return u == 0 || u == v;
}

void doWork(int col, int row){
    vec2 scaledTexCoord= (cameraWorldPosition.xz + vec2(row, col)) / imageSize;
    if (scaledTexCoord.x <= 1 && scaledTexCoord.x >= 0 && scaledTexCoord.y <= 1 && scaledTexCoord.y >= 0){
        vec3 pixelColor = texture(foliageMask, scaledTexCoord).rgb;
        if (pixelColor == colorToMatch){
            vec3 worldSpaceCoord = heightMapToWorldSpace(scaledTexCoord, imageSize);
            worldSpaceCoord.y = texture(heightMap, scaledTexCoord).r * heightScale;
            if(isPointInsideFrustum(worldSpaceCoord)){
                addTransform(worldSpaceCoord);
            }
        }
    }
}

void main() {
    if (gl_LocalInvocationIndex == 0){
        mat4 m = transpose(viewProjection);
        vec4 Row1 = vec4(m[0][0], m[0][1], m[0][2], m[0][3]);
        vec4 Row2 = vec4(m[1][0], m[1][1], m[1][2], m[1][3]);
        vec4 Row3 = vec4(m[2][0], m[2][1], m[2][2], m[2][3]);
        vec4 Row4 = vec4(m[3][0], m[3][1], m[3][2], m[3][3]);

        l = Row1 + Row4;
        r = Row1 - Row4;
        b = Row2 + Row4;
        t = Row2 - Row4;
        n = Row3 + Row4;
        f = Row3 - Row4;

        ivec2 total_threads = ivec2(1024);
        rows_per_thread = (N + total_threads.y - 1) / total_threads.y;
        cols_per_thread = (N + total_threads.x - 1) / total_threads.x;
    }

    ivec2 global_id = ivec2(gl_GlobalInvocationID.xy);
    int start_row = global_id.y * rows_per_thread;
    int start_col = global_id.x * cols_per_thread;

    for (int row = start_row; row < min(start_row + rows_per_thread, N); ++row) {
        for (int col = start_col; col < min(start_col + cols_per_thread, N); ++col) {
            doWork(col, row);
        }
    }
}