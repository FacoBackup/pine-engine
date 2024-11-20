layout (location = 0) in vec3 position;

#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

uniform int renderIndex;
uniform mat4 modelMatrix;

flat out int rIndex;
flat out vec3 translation;
flat out vec3 scale;

void main() {
    rIndex = renderIndex + 1;
    translation = modelMatrix[3].xyz;
    scale =  vec3(
        length(modelMatrix[0].xyz),
        length(modelMatrix[1].xyz),
        length(modelMatrix[2].xyz)
    );
    gl_Position = viewProjection * modelMatrix * vec4(position, 1);
}
