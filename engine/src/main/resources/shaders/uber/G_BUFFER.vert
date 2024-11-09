layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

uniform int transformationIndex;
uniform mat4 modelMatrix;

flat out vec3 cameraPlacement;
flat out int renderingIndex;
flat out float depthFunc;
smooth out vec2 initialUV;
smooth out vec3 normalVec;
smooth out vec3 worldSpacePosition;

void main() {
    cameraPlacement = cameraWorldPosition.xyz;
    renderingIndex = (transformationIndex + gl_InstanceID);
    vec4 wPosition = modelMatrix * vec4(position, 1.0);
    worldSpacePosition = wPosition.xyz;
    normalVec = normalize(mat3(modelMatrix) * normal);
    initialUV = uv;
    depthFunc = logDepthFC;

    gl_Position = viewProjection * wPosition;
}
