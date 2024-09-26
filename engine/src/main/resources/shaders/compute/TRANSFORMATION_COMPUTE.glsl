layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;


#include "../buffer_objects/TRANSFORMATION_SSBO.glsl"

#include "../buffer_objects/MODEL_SSBO.glsl"

#include "../util/MATH.glsl"

uniform int lightCount;
uniform int entityCount;

#define TO_RAD 0.01745329251
#define INFO_PER_LIGHT 30
#define INFO_PER_ENTITY 9
#define INFO_PER_LIGHT_METADATA 2

void main() {
    for (int i = 0; i < entityCount; i++){
        int actualIndex = i * INFO_PER_ENTITY;
        vec3 translation = vec3(transformation[actualIndex], transformation[actualIndex + 1], transformation[actualIndex + 2]);
        vec3 rotation = vec3(transformation[actualIndex + 3], transformation[actualIndex + 4], transformation[actualIndex + 5]);
        vec3 scale  = vec3(transformation[actualIndex + 6], transformation[actualIndex + 7], transformation[actualIndex + 8]);
        modelMatrices[i] = createModelMatrix(translation, rotation, scale);
    }
}
