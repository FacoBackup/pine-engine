layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0) uniform writeonly image2D outputImage;

layout(std430, binding = 12) buffer OctreeBuffer {
    // NON LEAF NODES - First 23 bits are the index pointing to the position of this voxel's children | 1 bits indicate if the node is a leaf | 8 bits are the child mask
    // LEAF NODES - Not present, the parent will contain the child block color stored in the first 23 bits of its value
    int voxels[];
};

uniform vec4 centerScale;
uniform ivec3 settings;

#define COUNT 64.
#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

const vec3 NNN = vec3(-1, -1, -1);
const vec3 PNN = vec3(1, -1, -1);
const vec3 NPN = vec3(-1, 1, -1);
const vec3 PPN = vec3(1, 1, -1);
const vec3 NNP = vec3(-1, -1, 1);
const vec3 PNP = vec3(1, -1, 1);
const vec3 NPP = vec3(-1, 1, 1);
const vec3 PPP = vec3(1, 1, 1);
const vec3 POS[8] = vec3[8](NNN, PNN, NPN, PPN, NNP, PNP, NPP, PPP);


vec3 randomColor(uint seed) {
    float hash = fract(sin(float(seed)) * 43758.5453);

    float r = fract(hash * 13.756);
    float g = fract(hash * 15.734);
    float b = fract(hash * 17.652);

    return vec3(r, g, b);
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

struct Ray { vec3 o, d, invDir; };
struct Stack {
    uint index;
    vec3 center;
    float scale;
};

bool intersect(inout vec3 boxMin, inout vec3 boxMax, inout Ray r) {
    vec3 t1 = (boxMin - r.o) * r.invDir;
    vec3 t2 = (boxMax - r.o) * r.invDir;

    vec3 tMin = min(t1, t2);
    vec3 tMax = max(t1, t2);

    float tEnter = max(max(tMin.x, tMin.y), tMin.z);
    float tExit = min(min(tMax.x, tMax.y), tMax.z);

    return tEnter <= tExit && tExit > 0.0;
}

bool intersectWithDistance(inout vec3 boxMin, inout vec3 boxMax, inout Ray r, out float entryDist) {
    vec3 t1 = (boxMin - r.o) * r.invDir;
    vec3 t2 = (boxMax - r.o) * r.invDir;

    vec3 tMin = min(t1, t2);
    vec3 tMax = max(t1, t2);

    entryDist = max(max(tMin.x, tMin.y), tMin.z);// Closest entry point along the ray
    float exitDist = min(min(tMax.x, tMax.y), tMax.z);// Furthest exit point along the ray

    return entryDist <= exitDist && exitDist > 0.0;// Ensure valid intersection and that exit is in front of the ray origin
}


vec3 unpackColor(int compressedColor) {
    int r = (compressedColor >> 16) & 0x7F;
    int g = (compressedColor >> 8) & 0xFF;
    int b = compressedColor & 0x7F;

    // Scale back up
    r = r * 2;
    b = b * 2;
    return vec3(r/255., g/255., b/255.);
}

uint countSetBitsBefore(inout uint mask, inout uint childIndex) {
    uint maskBefore = mask & ((1u << childIndex) - 1u);
    return bitCount(maskBefore);
}

// Based on https://www.shadertoy.com/view/MlBfRV
vec4 trace(
Ray ray,
bool randomColors,
bool showRaySearchCount,
bool showRayTestCount
) {
    vec3 center = centerScale.xyz;
    float scale = centerScale.w/2;
    vec3 minBox = center - scale;
    vec3 maxBox = center + scale;
    float minDistance = 1e10;// Large initial value
    if (!intersect(minBox, maxBox, ray)) return vec4(0);

    Stack stack[10];
    scale *= 0.5f;
    stack[0] = Stack(0u, center, scale);

    uint index = 0u;
    int rayTestCount = 0;
    int searchCount = 0;
    vec4 finalColor = vec4(0);
    int stackPos = 1;

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

        for (uint i = 0u; i < 8u; ++i) {
            if ((childMask & (1u << i)) == 0u){
                continue;
            }
            vec3 newCenter = center + scale * POS[i];
            vec3 minBox = newCenter - scale;
            vec3 maxBox = newCenter + scale;

            if (showRayTestCount){
                rayTestCount++;
                finalColor.g = rayTestCount/COUNT;
            }

            float entryDist;
            if (!intersectWithDistance(minBox, maxBox, ray, entryDist)) {
                continue;
            }
            if (entryDist < minDistance) {
                if (isLeafGroup) {
                    if (randomColors){
                        finalColor.rgb = randomColor(index + countSetBitsBefore(childMask, i));
                    } else {
                        finalColor.rgb = unpackColor(int(childGroupIndex));
                    }
                    finalColor.a = 1;
                    minDistance = entryDist;
                } else {
                    stack[stackPos++] = Stack(childGroupIndex + countSetBitsBefore(childMask, i), newCenter, scale * 0.5);
                }
            }
        }
    }
    finalColor.a = showRayTestCount || showRaySearchCount ? 1 : finalColor.a;
    return finalColor;
}

void main() {
    vec3 rayOrigin = cameraWorldPosition.xyz;
    vec3 rayDirection = createRay();
    vec4 outColor = trace(
    Ray(rayOrigin, rayDirection, 1./rayDirection),
    settings.x == 1,
    settings.y == 1,
    settings.z == 1
    );


    if (length(outColor) > 0){
        imageStore(outputImage, ivec2(gl_GlobalInvocationID.xy), outColor);
    }
}