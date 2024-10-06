layout(local_size_x = 1, local_size_y = 1) in;

layout (binding = 0) uniform writeonly image2D outputImage;

struct OctreeNode {
    uint childMask;// 8-bit mask for children
    uint firstChildIndex;// Index of the first child in the SSBO
    uint voxelDataIndex;// Index to voxel data, only valid for leaf nodes
};

layout(std430, binding = 12) buffer OctreeBuffer {
    OctreeNode nodes[];
};

layout(std430, binding = 13) buffer VoxelDataBuffer {
    vec3 voxelData[];// Color, material or other voxel data
};


#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"


bool pointInTriangle(vec3 P, vec3 A, vec3 B, vec3 C) {
    vec3 v0 = C - A;
    vec3 v1 = B - A;
    vec3 v2 = P - A;

    float dot00 = dot(v0, v0);
    float dot01 = dot(v0, v1);
    float dot02 = dot(v0, v2);
    float dot11 = dot(v1, v1);
    float dot12 = dot(v1, v2);

    float invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
    float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    return (u >= 0) && (v >= 0) && (u + v <= 1);
}

shared int voxelIndex = 0;

vec3 createRay() {
    vec2 pxNDS = (gl_GlobalInvocationID.xy/bufferResolution) * 2. - 1.;
    vec3 pointNDS = vec3(pxNDS, -1.);
    vec4 pointNDSH = vec4(pointNDS, 1.0);
    vec4 dirEye = invProjectionMatrix * pointNDSH;
    dirEye.w = 0.;
    vec3 dirWorld = (invViewMatrix * dirEye).xyz;
    return normalize(dirWorld);
}

uint countSetBitsBefore(uint mask, int childIndex) {
    // Count how many bits are set in the childMask before the childIndex.
    uint count = 0;
    for (int i = 0; i < childIndex; ++i) {
        if ((mask & (uint(1) << i)) != 0) {
            count++;
        }
    }
    return count;
}

bool intersectNodeBoundingBox(vec3 rayOrigin, vec3 rayDirection, vec3 minBounds, vec3 maxBounds) {
    vec3 invDir = 1.0 / rayDirection;

    vec3 tMin = (minBounds - rayOrigin) * invDir;
    vec3 tMax = (maxBounds - rayOrigin) * invDir;

    vec3 t1 = min(tMin, tMax);
    vec3 t2 = max(tMin, tMax);

    float tEnter = max(max(t1.x, t1.y), t1.z);
    float tExit = min(min(t2.x, t2.y), t2.z);

    return tEnter <= tExit && tExit > 0.0;
}

int getIntersectingChild(vec3 rayOrigin, vec3 rayDirection, vec3 nodeCenter) {
    int childIndex = 0;
    if (rayOrigin.x > nodeCenter.x) {
        childIndex |= 1;// Positive X octant
    }
    if (rayOrigin.y > nodeCenter.y) {
        childIndex |= 2;// Positive Y octant
    }
    if (rayOrigin.z > nodeCenter.z) {
        childIndex |= 4;// Positive Z octant
    }
    return childIndex;
}

void computeNodeBounds(in vec3 parentMinBounds, in vec3 parentMaxBounds, int octant, out vec3 minBounds, out vec3 maxBounds) {
    vec3 center = (parentMinBounds + parentMaxBounds) * 0.5;

    switch (octant) {
        case 0:// Lower-left-front
        minBounds = parentMinBounds;
        maxBounds = center;
        break;
        case 1:// Lower-right-front
        minBounds = vec3(center.x, parentMinBounds.y, parentMinBounds.z);
        maxBounds = vec3(parentMaxBounds.x, center.y, center.z);
        break;
        case 2:// Upper-left-front
        minBounds = vec3(parentMinBounds.x, center.y, parentMinBounds.z);
        maxBounds = vec3(center.x, parentMaxBounds.y, center.z);
        break;
        case 3:// Upper-right-front
        minBounds = vec3(center.x, center.y, parentMinBounds.z);
        maxBounds = vec3(parentMaxBounds.x, parentMaxBounds.y, center.z);
        break;
        case 4:// Lower-left-back
        minBounds = vec3(parentMinBounds.x, parentMinBounds.y, center.z);
        maxBounds = vec3(center.x, center.y, parentMaxBounds.z);
        break;
        case 5:// Lower-right-back
        minBounds = vec3(center.x, parentMinBounds.y, center.z);
        maxBounds = vec3(parentMaxBounds.x, center.y, parentMaxBounds.z);
        break;
        case 6:// Upper-left-back
        minBounds = vec3(parentMinBounds.x, center.y, center.z);
        maxBounds = vec3(center.x, parentMaxBounds.y, parentMaxBounds.z);
        break;
        case 7:// Upper-right-back
        minBounds = center;
        maxBounds = parentMaxBounds;
        break;
    }
}

float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}
vec3 randomColor(float seed) {
    float r = rand(vec2(seed));
    float g = rand(vec2(seed + r));
    return vec3(r, g, rand(vec2(seed + g)));
}

void main() {
    vec3 rayOrigin = placement.xyz;
    vec3 rayDirection = createRay();

    uint currentNodeIndex = 0;

    bool hit = false;
    vec3 hitColor = vec3(0);
    vec3 minBoundingBox = vec3(-64);
    vec3 maxBoundingBox = vec3(64.);

    int currentOctant = 0;
    while (!hit) {
        OctreeNode currentNode = nodes[currentNodeIndex];
        computeNodeBounds(minBoundingBox, maxBoundingBox, currentOctant, minBoundingBox, maxBoundingBox);
        bool intersects = intersectNodeBoundingBox(rayOrigin, rayDirection, minBoundingBox, maxBoundingBox);
        if (intersects) {
            if (currentNode.childMask == 0) {
                //            voxelData[currentNode.voxelDataIndex]
                if(currentOctant == 0){
                    hitColor = vec3(randomColor(float(currentNodeIndex)));
                } else if(currentOctant == 1){
                    hitColor = vec3(1., 0., 0.);
                }else if(currentOctant == 2){
                    hitColor = vec3(1., 1., 0.);
                }else if(currentOctant == 3){
                    hitColor = vec3(1., 1., 1.);
                }else if(currentOctant == 4){
                    hitColor = vec3(1., 0., 1.);
                }else if(currentOctant == 5){
                    hitColor = vec3(0, 1., 1.);
                }else if(currentOctant == 6){
                    hitColor = vec3(0, 1., 0.);
                }else if(currentOctant == 7){
                    hitColor = vec3(1., 0., 0.);
                }
                hit = true;
            } else {
                vec3 center = (minBoundingBox + maxBoundingBox) * 0.5;
                int childIndex = getIntersectingChild(rayOrigin, rayDirection, center);
                if ((currentNode.childMask & (uint(1) << childIndex)) != 0) {
                    currentOctant = childIndex;
                    uint offset = countSetBitsBefore(currentNode.childMask, childIndex);
                    currentNodeIndex = currentNode.firstChildIndex + offset;
                } else {
                    break;
                }
            }
        } else {
            break;
        }
    }

    if (hit){
        ivec2 coords = ivec2(gl_GlobalInvocationID.xy);
        imageStore(outputImage, coords, vec4(hitColor, 1.));
    }
}