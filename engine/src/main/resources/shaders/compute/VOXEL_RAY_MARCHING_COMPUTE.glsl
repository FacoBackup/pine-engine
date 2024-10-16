layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0) uniform writeonly image2D outputImage;

layout(std430, binding = 12) buffer OctreeBuffer {
// NON LEAF NODES - First 16 bits are the index pointing to the position of this voxel's children | 8 bits are the child mask | 8 bits indicate if the node is a leaf
// LEAF NODES - Compressed RGB value 10 bits for red 10 bits for green and 10 bits for blue
    int voxels[];
};

uniform vec4 centerScale;
uniform ivec3 settings;

#define COUNT 64.
#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"

const vec3 NNN = vec3(-1, -1, -1);
const vec3 PNN = vec3(1, -1, -1);
const vec3 NPN = vec3(-1, 1, -1);
const vec3 PPN = vec3(1, 1, -1);
const vec3 NNP = vec3(-1, -1, 1);
const vec3 PNP = vec3(1, -1, 1);
const vec3 NPP = vec3(-1, 1, 1);
const vec3 PPP = vec3(1, 1, 1);
const vec3 POS[8] = vec3[8](NNN, PNN, NPN, PPN, NNP, PNP, NPP, PPP);

float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}
vec3 randomColor(float seed) {
    float r = rand(vec2(seed));
    float g = rand(vec2(seed + r));
    return vec3(r, g, rand(vec2(seed + g)));
}

vec3 createRay() {
    vec2 pxNDS = (gl_GlobalInvocationID.xy/bufferResolution) * 2. - 1.;
    vec3 pointNDS = vec3(pxNDS, -1.);
    vec4 pointNDSH = vec4(pointNDS, 1.0);
    vec4 dirEye = invProjectionMatrix * pointNDSH;
    dirEye.w = 0.;
    vec3 dirWorld = (invViewMatrix * dirEye).xyz;
    return normalize(dirWorld);
}

struct Ray { vec3 o, d, invDir, oXd; };
struct Stack {
    uint index;
    vec3 center;
    float scale;
};

bool intersect(const vec3 boxMin, const vec3 boxMax, const Ray r) {
    vec3 tMin = boxMin * r.invDir - r.oXd;
    vec3 tMax = boxMax * r.invDir - r.oXd;

    vec3 t1 = min(tMin, tMax);
    vec3 t2 = max(tMin, tMax);

    float tEnter = max(max(t1.x, t1.y), t1.z);
    float tExit = min(min(t2.x, t2.y), t2.z);

    return tEnter <= tExit && tExit > 0.0;
}

vec3 unpackColor(int color) {
    int rInt = (color >> 20) & 0x3FF;// 10 bits for r (mask: 0x3FF is 1023 in binary)
    int gInt = (color >> 10) & 0x3FF;// 10 bits for g
    int bInt = color & 0x3FF;// 10 bits for b

    // Convert the quantized integers back to floats in the range [0, 1]
    float r = rInt / 1023.0f;
    float g = gInt / 1023.0f;
    float b = bInt / 1023.0f;

    // Scale back to the original [-1, 1] range
    r = r * 2.0f - 1.0f;
    g = g * 2.0f - 1.0f;
    b = b * 2.0f - 1.0f;

    return vec3(r, g, b);
}
const uint sortedOrder[8][8] = {
{3, 2, 1, 0, 7, 6, 5, 4}, // rayDirSign = (1, 1, 0)
{5, 4, 7, 6, 1, 0, 3, 2}, // rayDirSign = (1, 0, 1)
{1, 0, 3, 2, 5, 4, 7, 6}, // rayDirSign = (1, 0, 0)
{7, 6, 5, 4, 3, 2, 1, 0},  // rayDirSign = (1, 1, 1)

{2, 3, 0, 1, 6, 7, 4, 5}, // rayDirSign = (0, 1, 0)
{4, 5, 6, 7, 0, 1, 2, 3}, // rayDirSign = (0, 0, 1)
{0, 1, 2, 3, 4, 5, 6, 7}, // rayDirSign = (0, 0, 0)
{6, 7, 4, 5, 2, 3, 0, 1}, // rayDirSign = (0, 1, 1)
};


uint countSetBitsBefore(uint mask, uint childIndex) {
    // Count how many bits are set in the childMask before the childIndex.
    uint count = 0;
    for (uint i = 0; i < childIndex; ++i) {
        if ((mask & (1u << i)) != 0) {
            count++;
        }
    }
    return count;
}

// Based on https://www.shadertoy.com/view/MlBfRV
vec4 trace(
Ray ray,
bool randomColors,
bool showRaySearchCount,
bool showRayTestCount
) {
    vec3 center = centerScale.xyz;
    float scale = centerScale.w;
    vec3 minBox = center - scale;
    vec3 maxBox = center + scale;
    vec4 finalColor = vec4(0);
    ivec3 rayDirSign = ivec3(
    ray.d.x > 0.0 ? 1 : 0,
    ray.d.y > 0.0 ? 1 : 0,
    ray.d.z > 0.0 ? 1 : 0
    );

    Stack stack[10];
    int stackPos = 1;
    if (!intersect(minBox, maxBox, ray)) return finalColor;
    uint index = 0u;
    scale *= 0.5f;
    stack[0] = Stack(0u, center, scale);
    int rayTestCount = 0;
    int searchCount = 0;
    while (stackPos-- > 0) {
        if (showRaySearchCount){
            searchCount ++;
            finalColor.r = searchCount/COUNT;
        }
        center = stack[stackPos].center;
        index = stack[stackPos].index;
        scale = stack[stackPos].scale;

        uint voxel_node = uint(voxels[index]);
        uint childGroupIndex = (voxel_node >> 9) & 0x7FFFFFu;
        uint childMask =  (voxel_node & 0xFFu);
        bool isLeafGroup = ((voxel_node >> 8) & 0x1u) == 1u;

        const uint[8] sortedIndices = sortedOrder[rayDirSign.x + rayDirSign.y * 2 + rayDirSign.z * 4];
        for (uint i = 0u; i < 8u; ++i) {
            uint sortedChild = sortedIndices[i];
            bool empty = (childMask & (1u << sortedChild)) == 0u;
            if (empty){
                continue;
            }

            vec3 newCenter = center + scale * POS[sortedChild];
            vec3 minBox = newCenter - scale;
            vec3 maxBox = newCenter + scale;

            if (showRayTestCount){
                rayTestCount++;
                finalColor.g = rayTestCount/COUNT;
            }
            if (!intersect(minBox, maxBox, ray)){
                continue;
            }
            if (isLeafGroup){ //not empty, but a leaf
                //                if (randomColors){
                return vec4(randomColor(float(index)), 1);
                //                } else {
                //                    return vec4(unpackColor(int(voxel_node)), 1);
                //                }
            } else { //not empty and not a leaf
                stack[stackPos++] = Stack(childGroupIndex+countSetBitsBefore(childMask, sortedChild), newCenter, scale*0.5f);
            }
        }
    }

    finalColor.a = showRayTestCount || showRaySearchCount ? 1 : 0;

    return finalColor;
}

void main() {
    vec3 rayOrigin = placement.xyz;
    vec3 rayDirection = createRay();
    Ray ray = Ray(rayOrigin, rayDirection, 1./rayDirection, rayOrigin);
    ray.oXd *= ray.invDir;
    vec4 outColor = trace(
    ray,
    settings.x == 1,
    settings.y == 1,
    settings.z == 1
    );


    if (outColor.a > 0){
        imageStore(outputImage, ivec2(gl_GlobalInvocationID.xy), outColor);
    }
}