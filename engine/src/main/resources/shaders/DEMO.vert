layout(location = 0) in vec3 position;

#include "./CAMERA_VIEW_INFO.glsl"

uniform vec3 translation;
uniform vec3 rotation;
uniform vec3 scale;

void main(){

    gl_Position = viewProjection * vec4(position, 1.0);
}
