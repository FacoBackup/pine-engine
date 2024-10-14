layout (local_size_x = 1, local_size_y = 1) in;

layout (binding = 0) uniform writeonly image2D outputImage;

struct OctreeNode {
    int metadata;// First 16 bits are the index pointing to the position of this voxel's children | 8 bits are the child mask | 8 bits indicate if the node is a leaf
    int color;// Compressed RGB value 10 bits for red 10 bits for green and 10 bits for blue
};

layout(std430, binding = 12) buffer OctreeBuffer {
    OctreeNode voxels[];
};

uniform vec4 centerScale;
uniform ivec3 settings;

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

struct Ray { vec3 o, d, invDir; };
struct Stack {
    uint index;
    vec3 center;
    float scale;
};

bool intersect(const vec3 boxMin, const vec3 boxMax, const Ray r) {
    vec3 tbot = r.invDir * (boxMin - r.o);
    vec3 ttop = r.invDir * (boxMax - r.o);
    vec3 tmin = min(ttop, tbot);
    vec3 tmax = max(ttop, tbot);
    vec2 t = max(tmin.xx, tmin.yz);
    float t0 = max(t.x, t.y);
    t = min(tmax.xx, tmax.yz);
    float t1 = min(t.x, t.y);
    return t1 > max(t0, 0.0);
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

    Stack stack[10];
    int stackPos = 1;
    if (!intersect(minBox, maxBox, ray)) return finalColor;
    uint index = 0u;
    scale *= 0.5f;
    stack[0] = Stack(0u, center, scale);
    finalColor.a = 1.;
    int rayTestCount = 0;
    int searchCount = 0;
    while (stackPos-- > 0) {
        if (showRaySearchCount){
            searchCount ++;
            finalColor.r = searchCount/20.;
        }
        center = stack[stackPos].center;
        index = stack[stackPos].index;
        scale = stack[stackPos].scale;
        OctreeNode voxel_node = voxels[index];
        uint metadata = uint(voxel_node.metadata);
        uint voxel_group_offset = metadata >> 16;
        uint voxel_child_mask = (metadata & 0x0000FF00u) >> 8u;
        uint voxel_leaf_mask = metadata & 0x000000FFu;
        uint accumulated_offset = 0u;
        for (uint i = 0u; i < 8u; ++i) {
            bool empty = (voxel_child_mask & (1u << i)) == 0u;
            if (empty){ //empty
                continue;
            }

            bool is_leaf = (voxel_leaf_mask & (1u << i)) != 0u;
            vec3 new_center = center + scale * POS[i];
            vec3 minBox = new_center - scale;
            vec3 maxBox = new_center + scale;

            if (showRayTestCount){
                rayTestCount++;
                finalColor.g = rayTestCount/20.;
            }
            if (!intersect(minBox, maxBox, ray)){
                if (!is_leaf){
                    accumulated_offset += 1u;
                }
                continue;
            }
            if (is_leaf){ //not empty, but a leaf
                if (randomColors){
                    return vec4(randomColor(float(index)), 1);
                } else {
                    return vec4(unpackColor(voxel_node.color), 1);
                }
            } else { //not empty and not a leaf
                stack[stackPos++] = Stack(voxel_group_offset+accumulated_offset, new_center, scale*0.5f);
                accumulated_offset += 1u;
            }
        }
    }
    return finalColor;
}


void main() {
    vec3 rayOrigin = placement.xyz;
    vec3 rayDirection = createRay();
    vec4 outColor = trace(
        Ray(rayOrigin, rayDirection, 1./rayDirection),
        settings.x == 1,
        settings.y == 1,
        settings.z == 1
    );


    if (outColor.a > 0){
        imageStore(outputImage, ivec2(gl_GlobalInvocationID.xy), outColor);
    }
}