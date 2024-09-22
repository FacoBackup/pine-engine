layout(location = 0) in vec3 position;

#include "./buffer_objects/MODEL_SSBO.glsl"

uniform int transformationIndex;

out float instance;

void main(){
    int actualIndex = (transformationIndex + gl_InstanceID);
    instance = modelView[0];
    gl_Position = modelView[actualIndex] * vec4(position, 1.0);
}