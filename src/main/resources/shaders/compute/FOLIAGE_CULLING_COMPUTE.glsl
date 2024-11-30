layout (local_size_x = 4, local_size_y = 4) in;

layout (binding = 0) uniform sampler2D materialMask;
layout (binding = 1) uniform sampler2D heightMap;
layout (binding = 2, offset = 0) uniform atomic_uint globalIndex;

layout(std430, binding = 3) writeonly buffer TransformationBuffer {
    vec3 transformations[];
};

layout(std430, binding = 4) writeonly buffer IndirectBuffer {
    uint count;// Number of indices
    uint instanceCount;// Number of instances
    uint firstIndex;// First index
    uint baseVertex;// Base vertex
    uint baseInstance;// Base instance ID
} drawCommand;

uniform vec2 terrainOffset;
uniform vec4 colorToMatch;
uniform vec4 settings;
uniform vec2 imageSize;
uniform float heightScale;

#define MAX_DISTANCE_FROM_CAMERA settings.x
#define MAX_INSTANCES uint(settings.y)

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

shared vec4 l;
shared vec4 r;
shared vec4 b;
shared vec4 t;
shared vec4 n;
shared vec4 f;
shared int rows_per_thread;
shared int cols_per_thread;
const int N = 1024;
const ivec2 total_threads = ivec2(1024);

vec3 getWorldPosition(vec2 uv, vec2 planeSize) {
    float worldX = uv.x * planeSize.x + terrainOffset.x;
    float worldZ = uv.y * planeSize.y + terrainOffset.y;
    return vec3(worldX, 0, worldZ);
}

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

vec2 hash(vec2 a) {
    a = fract(a * vec2(.8));
    a += dot(a, a.yx + 19.19);
    return fract((a.xx + a.yx)*a.xy);
}

bool check(float v, float v1){
    return v != 0 && v1 != 0;
}

void doWork(int col, int row){
    vec2 scaledTexCoord= (vec2(round(cameraWorldPosition.x), round(cameraWorldPosition.z)) + vec2(row, col)) / imageSize;
    if (scaledTexCoord.x <= 1 && scaledTexCoord.x >= 0 && scaledTexCoord.y <= 1 && scaledTexCoord.y >= 0){
        vec4 pixelColor = texture(materialMask, scaledTexCoord);
        if (check(pixelColor.r, colorToMatch.r) || check(pixelColor.g, colorToMatch.g) || check(pixelColor.b, colorToMatch.b) || check(pixelColor.a, colorToMatch.a)){
            vec3 worldSpaceCoord = getWorldPosition(scaledTexCoord, imageSize);
            worldSpaceCoord.xz += hash(worldSpaceCoord.xz);
            if (length(worldSpaceCoord.xz - cameraWorldPosition.xz) < MAX_DISTANCE_FROM_CAMERA){
                scaledTexCoord = (worldSpaceCoord.xz - terrainOffset)/imageSize;
                worldSpaceCoord.y = texture(heightMap, scaledTexCoord).r * heightScale;
                if (isPointInsideFrustum(worldSpaceCoord)){
                    uint index = atomicCounterIncrement(globalIndex);
                    if (index < MAX_INSTANCES){
                        transformations[index] = worldSpaceCoord;
                    }
                }
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

    if (ivec2(gl_GlobalInvocationID.xy) == (total_threads - 1)){
        drawCommand.instanceCount = atomicCounter(globalIndex);
        drawCommand.count = uint(settings.z);
        drawCommand.firstIndex= 0;
        drawCommand.baseVertex= 0;
        drawCommand.baseInstance = 0;
    }
}