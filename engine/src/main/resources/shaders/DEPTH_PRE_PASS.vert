layout (location = 0) in vec3 position;

#include "./buffer_objects/MODEL_SSBO.glsl"

#include "./buffer_objects/CAMERA_VIEW_INFO.glsl"

#include "./buffer_objects/CAMERA_PROJECTION_INFO.glsl"

uniform int transformationIndex;

out flat int renderingIndex;
out float depthFunc;

void main() {
    depthFunc = logDepthFC;

    renderingIndex = (transformationIndex + gl_InstanceID);
    gl_Position = viewProjection * modelMatrices[renderingIndex] * vec4(position, 1.0);
}
