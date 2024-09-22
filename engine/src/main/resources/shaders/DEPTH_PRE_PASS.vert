layout (location = 0) in vec3 position;

#include "./buffer_objects/MODEL_SSBO.glsl"

#include "./buffer_objects/CAMERA_PROJECTION_INFO.glsl"

uniform int transformationIndex;

out int renderingIndex;
out float depthFunc;

void main() {
    depthFunc = logDepthFC;

    int renderingIndex = (transformationIndex + gl_InstanceID);
    gl_Position = modelView[renderingIndex] * vec4(position, 1.0);
}