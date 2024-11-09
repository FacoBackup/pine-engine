layout (location = 0) in vec3 position;

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

#include "../buffer_objects/MODEL_SSBO.glsl"
uniform int renderIndex;

flat out int rIndex;

void main() {
    rIndex = renderIndex + 1;
    mat4 model = modelMatrices[renderIndex + gl_InstanceID];
    gl_Position = viewProjection * model * vec4(position, 1);
}
