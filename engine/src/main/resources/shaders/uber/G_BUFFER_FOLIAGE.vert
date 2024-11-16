layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

layout(std430, binding = 3) buffer TransformationBuffer {
    mat4 transformations[];
};

out mat4 invModelMatrix;
flat out int isDecalPass;
flat out int renderingIndex;
smooth out vec2 initialUV;
smooth out vec3 normalVec;
smooth out vec3 worldSpacePosition;

void main() {
    isDecalPass = 0;
    invModelMatrix = mat4(0);

    renderingIndex = gl_InstanceID + 1;
    mat4 modelMatrix = transformations[renderingIndex];
    vec4 wPosition = modelMatrix * vec4(position , 1.0);
    worldSpacePosition = wPosition.xyz;
    normalVec = normalize(mat3(modelMatrix) * normal);
    initialUV = uv;

    gl_Position = viewProjection * wPosition;
}
