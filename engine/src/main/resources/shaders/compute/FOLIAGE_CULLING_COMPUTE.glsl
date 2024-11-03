layout (local_size_x = 1, local_size_y = 1) in;

layout (rgba8, binding = 0) readonly uniform image2D instancingMask;
layout (binding = 1) uniform sampler2D heightMap;
layout (binding = 2, offset = 0) uniform atomic_uint globalIndex;

layout(std430, binding = 3) buffer MetadataBuffer {
    int metadata[];
};

layout(std430, binding = 4) writeonly buffer TransformationBuffer {
    mat4 transformations[];
};

uniform vec2 imageSize;
uniform float heightScale;

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"
#include "../util/UTIL.glsl"

vec3 heightMapToWorldSpace(vec2 uv, float planeSize) {
    float worldX = uv.x * planeSize - planeSize / 2.0;
    float worldZ = uv.y * planeSize - planeSize / 2.0;
    return vec3(worldX, 0, worldZ);
}

mat4 createTransformationMatrix(mat3 rotation, vec3 translation) {
    mat4 transformationMatrix = mat4(1);

    transformationMatrix[0].xyz = rotation[0];
    transformationMatrix[1].xyz = rotation[1];
    transformationMatrix[2].xyz = rotation[2];

    transformationMatrix[3].xyz = translation;
    transformationMatrix[3].w = 1.0;

    return transformationMatrix;
}

void main() {
    vec2 scaledTexCoord= vec2(gl_GlobalInvocationID.xy)/imageSize;
    vec3 worldSpaceCoord = heightMapToWorldSpace(scaledTexCoord, imageSize.x);
    // TODO - FRUSTUM CULLING BASED ON WORLD COORD
    ivec2 pixelPos = ivec2(gl_GlobalInvocationID.xy);
    if (imageLoad(instancingMask, pixelPos).r > 0){
        float localHeight = texture(heightMap, scaledTexCoord).r;
        worldSpaceCoord.y = localHeight * heightScale;

        vec3 normalVec = normalize(getNormalFromHeightMap(localHeight, heightMap, scaledTexCoord));
        mat3 rotation = getRotationFromNormal(normalVec);
        uint index = atomicCounterIncrement(globalIndex);
        transformations[index] = createTransformationMatrix(rotation, worldSpaceCoord);
        atomicAdd(metadata[0], 1);
    }
}