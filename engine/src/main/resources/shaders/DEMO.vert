layout(location = 0) in vec3 position;

#include "./buffer_objects/MODEL_SSBO.glsl"

#include "./buffer_objects/CAMERA_VIEW_INFO.glsl"

uniform int transformationIndex;

out float renderId;

void main(){
    int index = (transformationIndex + gl_InstanceID);
    renderId = float(index);
    gl_Position = viewProjection * modelMatrices[index] * vec4(position, 1.0);
}