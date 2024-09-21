layout(location = 0) in vec3 position;
layout(location = 3) in mat4 localModelMatrix;

#include "./CAMERA_VIEW_INFO.glsl"

uniform mat4 baseModelMatrix;

void main(){
    gl_Position = viewProjection * baseModelMatrix * localModelMatrix * vec4(position, 1.0);
}
