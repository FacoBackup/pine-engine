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

bool intersectNodeBoundingBox(OctreeNode node, vec3 rayOrigin, vec3 rayDirection) {
    // Implement ray-box intersection test
    // Return true if the ray intersects the bounding box of this node, false otherwise.
    // This would depend on the node's bounding box, which you may store as part of the octree structure.
    return false;
}

int getIntersectingChild(vec3 rayOrigin, vec3 rayDirection, OctreeNode node) {
    // Compute which of the 8 children (octants) the ray intersects first.
    // This depends on how you've stored the bounds for each octant.
    // Simplified example (assumes a centered box subdivision):

    int octant = 0;
    if (rayDirection.x > 0) octant |= 1;
    if (rayDirection.y > 0) octant |= 2;
    if (rayDirection.z > 0) octant |= 4;

    return octant;
}

void main() {
    // Ray data

    vec3 rayOrigin = placement.xyz;// Ray starting position
    vec3 rayDirection = createRay();// Normalized ray direction

    // Octree traversal
    uint currentNodeIndex = 0;// Start with the root node (usually index 0)

    bool hit = false;
    vec3 hitColor;// Store voxel color or data

    while (!hit) {
        // Fetch the current node
        OctreeNode currentNode = nodes[currentNodeIndex];

        // Ray-box intersection (optional, depending on how you store bounding volumes)
        // If the ray doesn't hit the node's bounding box, you can skip this node.
        bool intersects = intersectNodeBoundingBox(currentNode, rayOrigin, rayDirection);
        if (!intersects) {
            break;// Exit the loop if no intersection is found
        }

        // Check if it's a leaf node by checking if the childMask is 0
        if (currentNode.childMask == 0) {
            // Leaf node hit, fetch voxel data
            hitColor = voxelData[currentNode.voxelDataIndex];
            hit = true;// Mark that we found a hit
            break;// Exit the traversal
        }

        // Otherwise, it's an internal node; we need to determine which child to traverse
        int childIndex = getIntersectingChild(rayOrigin, rayDirection, currentNode);

        // Use the childMask to check if the selected child exists
        if ((currentNode.childMask & (uint(1) << childIndex)) == 0) {
            // No child in this octant, terminate the traversal or move to another branch
            break;
        }

        // Calculate the index of the child node
        uint offset = countSetBitsBefore(currentNode.childMask, childIndex);
        currentNodeIndex = currentNode.firstChildIndex + offset;// Traverse to child node
    }

    if (hit){
        ivec2 coords = ivec2(gl_GlobalInvocationID.xy);
        imageStore(outputImage, coords, vec4(1., 0., 1., 1.));
    }
}