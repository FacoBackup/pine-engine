layout (local_size_x = 1, local_size_y = 1) in;

uniform sampler2D instancingMask;
uniform sampler2D heightMap;

layout(std430, binding = 3) buffer MetadataBuffer {
    int metadata[];
};

layout(std430, binding = 4) writeonly buffer TransformationBuffer {
    mat4 transformations[];
};

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
    vec2 size = textureSize(instancingMask, 0);
    vec2 texCoords = vec2(gl_GlobalInvocationID.xy)/size;
    vec3 worldSpaceCoord = heightMapToWorldSpace(texCoords, size.x);
    // TODO - FRUSTUM CULLING BASED ON WORLD COORD

    if (length(texture(instancingMask, texCoords).rgb) > 0){
        float localHeight = texture(heightMap, texCoords).r;
        worldSpaceCoord.y = localHeight * heightScale;

        vec3 normalVec = normalize(getNormalFromHeightMap(localHeight, heightMap, texCoords));
        mat3 rotation = getRotationFromNormal(normalVec);
        transformations[metadata[0]] = createTransformationMatrix(rotation, worldSpaceCoord);
        atomicAdd(metadata[0], 1);
    }
}