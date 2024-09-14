out vec4 fragColor;

#include "./CAMERA_VIEW_INFO.glsl"
#include "./CAMERA_PROJECTION_INFO.glsl"


void main() {
    fragColor = vec4(bufferResolution/bufferResolution.x, 1., 1.);
//    fragColor =  viewMatrix * fragColor;
//    fragColor.a = 1.;
}