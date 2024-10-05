layout(local_size_x = 8, local_size_y = 8, local_size_z = 8) in;

layout(std430, binding = 12) writeonly buffer VoxelBlock {
    float voxelData[];
};

layout(std430, binding = 13) buffer VoxelMetadataBlock {
    float voxelMetadata[];
};

layout (std430, binding = 0) readonly buffer VertexBuffer {
    vec3 vertices[];
};
layout (std430, binding = 1) readonly buffer IndexBuffer {
    int indices[];
};

uniform int numTriangles;

#include "../buffer_objects/MODEL_SSBO.glsl"



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

void main() {
    ivec3 voxelPos = ivec3(gl_GlobalInvocationID);
    ivec3 gridSize = ivec3(128, 128, 128);

    int indexV = (voxelPos.x + voxelPos.y + voxelPos.z) * 3;
    vec3 voxelWorldPos = (vec3(voxelPos) / vec3(gridSize)) * 2.0 - 1.0;
    mat4 model= modelMatrices[0];
    bool isFilled = false;
    for (int i = 0; i < numTriangles; ++i) {
        int idx0 = indices[i * 3];
        int idx1 = indices[i * 3 + 1];
        int idx2 = indices[i * 3 + 2];


        vec3 v0 = vec3(model * vec4(vertices[idx0], 1.));
        vec3 v1 = vec3(model * vec4(vertices[idx1], 1.));
        vec3 v2 = vec3(model * vec4(vertices[idx2], 1.));

        if (pointInTriangle(voxelWorldPos, v0, v1, v2)) {
            isFilled = true;
            break;
        }
    }

    if (isFilled) {
        uint index = (gl_GlobalInvocationID.x + gl_GlobalInvocationID.y + gl_GlobalInvocationID.z) * 3;
        voxelData[index] = voxelWorldPos.x;
        voxelData[index + 1] = voxelWorldPos.y;
        voxelData[index + 2] = voxelWorldPos.z;
    }
}