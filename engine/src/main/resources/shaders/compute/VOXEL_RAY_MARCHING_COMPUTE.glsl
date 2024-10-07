layout (local_size_x = 1, local_size_y = 1) in;

layout (binding = 0) uniform writeonly image2D outputImage;

struct OctreeNode {
    uint childMask;// 8-bit mask for children
    uint firstChildIndex;// Index of the first child in the SSBO
    uint voxelDataIndex;// Index to voxel data, only valid for leaf nodes
    uint octant;
};

layout(std430, binding = 12) buffer OctreeBuffer {
    OctreeNode nodes[];
};

layout(std430, binding = 13) buffer VoxelDataBuffer {
    vec4 voxelData[];// Position + size
};

uniform vec3 sceneMinBoundingBox;
uniform vec3 sceneMaxBoundingBox;

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"

vec3 createRay() {
    vec2 pxNDS = (gl_GlobalInvocationID.xy/bufferResolution) * 2. - 1.;
    vec3 pointNDS = vec3(pxNDS, -1.);
    vec4 pointNDSH = vec4(pointNDS, 1.0);
    vec4 dirEye = invProjectionMatrix * pointNDSH;
    dirEye.w = 0.;
    vec3 dirWorld = (invViewMatrix * dirEye).xyz;
    return normalize(dirWorld);
}

// REMOVES EMPTY CHILDREN FROM INFLUENCING INDEX
uint countSetBitsBefore(uint mask, int childIndex) {
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

void computeNodeBounds(in vec3 parentMinBounds, in vec3 parentMaxBounds, uint octant, out vec3 minBounds, out vec3 maxBounds) {
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
    vec3 minBoundingBox = sceneMinBoundingBox;
    vec3 maxBoundingBox = sceneMaxBoundingBox;

    int currentOctant = 0;
    float t = 0;
    while (!hit) {
        OctreeNode currentNode = nodes[currentNodeIndex];
        vec3 minBounds = vec3(0.);
        vec3 maxBounds = vec3(0.);
        computeNodeBounds(minBoundingBox, maxBoundingBox, currentNode.octant, minBounds, maxBounds);
        bool intersects = intersectNodeBoundingBox(rayOrigin + rayDirection * t, rayDirection, minBounds, maxBounds);
        if (intersects) {
            if (currentNode.childMask == 0) {
                //            voxelData[currentNode.voxelDataIndex]
                hitColor = vec3(randomColor(float(currentNodeIndex)));
                hit = true;
                break;
            } else if ((currentNode.childMask & (uint(1) << uint(currentNode.firstChildIndex))) != 0) {
                t += .01;
                vec3 center = (minBounds + maxBounds) * 0.5;
                currentOctant = getIntersectingChild(rayOrigin, rayDirection, center);
                uint offset = countSetBitsBefore(currentNode.childMask, currentOctant);
                currentNodeIndex = currentNode.firstChildIndex + offset;
                minBoundingBox = minBounds;
                maxBoundingBox = maxBounds;
            } else {
                break;
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