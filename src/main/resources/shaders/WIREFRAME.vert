layout (location = 0) in vec3 position;
#include "./CAMERA_VIEW_INFO.glsl"
uniform mat4 transformMatrix;
void main() { 
    gl_Position = viewProjection * transformMatrix * vec4(position, 1.0);
}