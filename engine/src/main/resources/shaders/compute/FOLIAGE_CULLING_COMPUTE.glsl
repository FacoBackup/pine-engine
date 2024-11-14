layout (local_size_x = 1, local_size_y = 1) in;

layout (binding = 0) uniform sampler2D foliageMask;
layout (binding = 1) uniform sampler2D heightMap;
layout (binding = 2, offset = 0) uniform atomic_uint globalIndex;

layout(std430, binding = 3) writeonly buffer TransformationBuffer {
    mat4 transformations[];
};

uniform vec3 colorToMatch;
uniform vec2 imageSize;
uniform vec2 tileOffset;
uniform float heightScale;

#define MAX_INSTANCING  500000

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"
#include "../util/UTIL.glsl"

vec3 heightMapToWorldSpace(vec2 uv, float planeSize) {
    float worldX = uv.x * planeSize ;
    float worldZ = uv.y * planeSize ;
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
    vec2 scaledTexCoord= vec2(gl_GlobalInvocationID.xy) / imageSize;
    vec3 worldSpaceCoord = heightMapToWorldSpace(scaledTexCoord, imageSize.x);

    // TODO - FRUSTUM CULLING BASED ON WORLD COORD
    vec3 pixelColor = texture(foliageMask, scaledTexCoord).rgb;
    if (pixelColor == colorToMatch){
        uint index = atomicCounterIncrement(globalIndex);
        if (index < MAX_INSTANCING){
            float localHeight = texture(heightMap, scaledTexCoord).r;
            vec3 localWorld = worldSpaceCoord;
            localWorld.y = localHeight * heightScale;
            localWorld.x += tileOffset.x;
            localWorld.z += tileOffset.y;
            vec3 normalVec = normalize(getNormalFromHeightMap(localHeight, heightMap, scaledTexCoord));
            mat3 rotation = getRotationFromNormal(normalVec);
            transformations[index] = createTransformationMatrix(rotation, localWorld);
        }
    }
}