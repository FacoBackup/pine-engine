layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uvV;
layout (location = 2) in vec3 normalV;

#include "./buffer_objects/MODEL_SSBO.glsl"

#include "./buffer_objects/CAMERA_VIEW_INFO.glsl"

uniform int transformationIndex;

out flat int renderingIndex;
out vec3 normal;
out vec2 uv;
out float depthFunc;

void main() {
    uv = uvV;
    normal = normalV;
    depthFunc = logDepthFC;

    renderingIndex = (transformationIndex + gl_InstanceID);
    gl_Position = viewProjection * modelMatrices[renderingIndex] * vec4(position, 1.0);
}
