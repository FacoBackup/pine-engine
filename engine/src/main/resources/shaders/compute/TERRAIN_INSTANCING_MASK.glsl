layout (local_size_x = 1, local_size_y = 1) in;

layout (binding = 0) uniform sampler2D instancingMask;
layout (binding = 1) uniform sampler2D heightMap;
layout (binding = 2, offset = 0) uniform atomic_uint globalIndex;

layout(std430, binding = 3) writeonly buffer MetadataBuffer {
    int metadata[];
};

layout(std430, binding = 4) writeonly buffer TransformationBuffer {
    mat4 transformations[];
};

uniform float planeSize;
uniform float heightScale;

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"
#include "../util/UTIL.glsl"

vec3 heightMapToWorldSpace(vec2 uv) {
    float worldX = uv.x * planeSize - planeSize / 2.0;
    float worldZ = uv.y * planeSize - planeSize / 2.0;
    return vec3(worldX, 0, worldZ);
}

mat4 createTransformationMatrix(inout mat3 rotation, inout vec3 translation) {
    mat4 transformationMatrix = mat4(1.0);

    transformationMatrix[0].xyz = rotation[0];
    transformationMatrix[1].xyz = rotation[1];
    transformationMatrix[2].xyz = rotation[2];

    transformationMatrix[3].xyz = translation;
    transformationMatrix[3].w = 1.0;

    return transformationMatrix;
}

void main() {
    vec2 texCoords = gl_GlobalInvocationID.xy/bufferResolution;
    vec3 worldSpaceCoord = heightMapToWorldSpace(texCoords);
    // TODO - FRUSTUM CULLING BASED ON WORLD COORD

    int instanceId = int(texture(instancingMask, texCoords).r);
    if (instanceId != 0){
        uint bufferIndex = atomicCounterIncrement(globalIndex);

        float localHeight = texture(heightMap, texCoords).r;
        worldSpaceCoord.y = localHeight * heightScale;


        vec3 normalVec = getNormalFromHeightMap(localHeight, heightMap, initialUV, normalOffset);
        mat3 rotation = getRotationFromNormal(normalVec);

        transformations[bufferIndex] = createTransformationMatrix(rotation, worldSpaceCoord);

        atomicAdd(metadata[0], 1);
    }
}