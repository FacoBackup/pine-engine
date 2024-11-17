layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

layout(std430, binding = 3) buffer TransformationBuffer {
    vec3 transformations[];
};

layout(binding = 10) uniform sampler2D noise;
uniform vec2 terrainOffset;

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
    vec3 translation = transformations[renderingIndex];
    vec3 updatedPosition = position + translation;
    vec2 normPos = normalize(terrainOffset + translation.xz);

    vec2 noiseVal = texture(noise, normPos).rg * position.y;

    updatedPosition += vec3(noiseVal.x, 0, noiseVal.y);
    worldSpacePosition = updatedPosition.xyz;
    normalVec = normalize(normal);
    initialUV = uv;

    gl_Position = viewProjection * vec4(updatedPosition, 1);
}
