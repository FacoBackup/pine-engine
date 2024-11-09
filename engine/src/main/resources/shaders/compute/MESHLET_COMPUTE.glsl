layout(local_size_x = 8, local_size_y = 1) in;

layout(std430, binding = 0) readonly buffer InfoBuffer {
    int info[];
};
layout(std430, binding = 1) readonly buffer IndexBuffer {
    int indices[];
};
layout(std430, binding = 2) readonly buffer TriangleBuffer {
    int triangles[];
};
layout(std430, binding = 3) readonly buffer Vertices {
    float vertices[];
};

layout (binding = 0) uniform writeonly image2D outputImage;

uniform int meshletCount;
#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

// Function to calculate barycentric coordinates
vec3 barycentric(vec2 p, vec2 a, vec2 b, vec2 c) {
    vec2 v0 = b - a, v1 = c - a, v2 = p - a;
    float d00 = dot(v0, v0);
    float d01 = dot(v0, v1);
    float d11 = dot(v1, v1);
    float d20 = dot(v2, v0);
    float d21 = dot(v2, v1);
    float denom = d00 * d11 - d01 * d01;
    vec3 bary;
    bary.y = (d11 * d20 - d01 * d21) / denom;
    bary.z = (d00 * d21 - d01 * d20) / denom;
    bary.x = 1.0 - bary.y - bary.z;
    return bary;
}

vec2 processVertex( vec3 vertex){
    return vec2(viewProjection * vec4(vertex, 1));
}
void main() {
    for (int i = 0; i < meshletCount; i++){
        int index = i * 4;
        int vertexIndexLength = info[index];
        int vertexIndexStart = info[index + 1];
        //        int trianglesLength = info[index + 2];
        //        int trianglesStart = info[index + 3];

        for (int j = 0; j < vertexIndexLength; ++j) {
            // Get the indices for the vertices of this triangle
            int vertexIndex = vertexIndexStart + j * 3;

            uint index0 = indices[vertexIndex];
            uint index1 = indices[vertexIndex + 1];
            uint index2 = indices[vertexIndex + 2];

            // Fetch the vertex positions using the indices
            vec2 vertex0 = processVertex(vec3(vertices[index0], vertices[index0 + 1], vertices[index0 + 2]));
            vec2 vertex1 = processVertex(vec3(vertices[index1], vertices[index1 + 1], vertices[index1 + 2]));
            vec2 vertex2 = processVertex(vec3(vertices[index2], vertices[index2 + 1], vertices[index2 + 2]));

            // Compute the bounding box of the triangle
            vec2 minBounds = min(vertex0, min(vertex1, vertex2));
            vec2 maxBounds = max(vertex0, max(vertex1, vertex2));

            // Iterate over each pixel within the bounding box
            for (int x = int(minBounds.x); x <= int(maxBounds.x); ++x) {
                for (int y = int(minBounds.y); y <= int(maxBounds.y); ++y) {
                    vec2 pixelPos = vec2(x, y);

                    // Calculate barycentric coordinates
                    vec3 bary = barycentric(pixelPos, vertex0, vertex1, vertex2);

                    // Check if the pixel is inside the triangle
                    if (bary.x >= 0.0 && bary.y >= 0.0 && bary.z >= 0.0) {
                        // Interpolate color or other attributes here
                        // For simplicity, using triangleColor as a uniform color

                        // Write the color to the output texture
                        imageStore(outputImage, ivec2(x, y), vec4(1, 0, 1, 1));
                    }
                }
            }
        }
    }
}

