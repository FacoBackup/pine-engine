layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;


#include "../buffer_objects/MODEL_SSBO.glsl"

#include "../util/MATH.glsl"

uniform int lightCount;
uniform int entityCount;

#define TO_RAD 0.01745329251
#define INFO_PER_LIGHT 30
#define INFO_PER_ENTITY 11
#define INFO_PER_LIGHT_METADATA 2

void main() {
//    for (int i = 0; i < entityCount; i++){
//        int actualIndex = i * INFO_PER_ENTITY;
//        bool isRenderable = transformation[actualIndex + 9] > 0.;
//        if (isRenderable){
//            modelMatrices[i] = getMatrixFromOffset(actualIndex);
//        }
//    }
}

