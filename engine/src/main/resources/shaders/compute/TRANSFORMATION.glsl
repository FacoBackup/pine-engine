#include "../buffer_objects/TRANSFORMATION_SSBO.glsl"

#include "../buffer_objects/CAMERA_VIEW_INFO.glsl"

void main() {
    transformation[0] += 2.;
    transformation[1] += 2.;
    transformation[2] += 2.;
}
