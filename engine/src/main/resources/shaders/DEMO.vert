layout(location = 0) in vec3 position;

#include "./buffer_objects/MODEL_SSBO.glsl"

uniform int transformationIndex;

out float renderId;

void main(){
    int index = (transformationIndex + gl_InstanceID);
    renderId = float(index);
    gl_Position = modelView[index] * vec4(position, 1.0);
}