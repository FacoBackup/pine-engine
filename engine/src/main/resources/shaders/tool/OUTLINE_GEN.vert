layout (location = 0) in vec3 position;

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"

uniform mat4 model;

void main() {
    gl_Position = viewProjection * model * vec4(position, 1);
}
