layout (location = 0) in vec3 position;

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

uniform int renderIndex;
uniform mat4 modelMatrix;

out mat4 invModelMatrix;
flat out int isDecalPass;
flat out int renderingIndex;
smooth out vec2 initialUV;
smooth out vec3 normalVec;
smooth out vec3 worldSpacePosition;

void main() {
    isDecalPass = 1;
    invModelMatrix = inverse(modelMatrix);

    renderingIndex = renderIndex;
    vec4 wPosition = modelMatrix * vec4(position, 1.0);
    worldSpacePosition = wPosition.xyz;
    normalVec = vec3(0);
    initialUV = vec2(0);

    gl_Position = viewProjection * wPosition;
}
