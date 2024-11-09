layout (location = 0) in vec3 position;

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

uniform int renderIndex;
uniform mat4 modelMatrix;

flat out int rIndex;

void main() {
    rIndex = renderIndex + 1;
    gl_Position = viewProjection * modelMatrix * vec4(position, 1);
}
