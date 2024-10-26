#define RADIUS 10.
uniform sampler2D metadataSampler;
uniform vec2 xy;
uniform vec2 viewportSize;

in vec2 texCoords;

#include "./buffer_objects/CAMERA_VIEW_INFO.glsl"

out vec4 finalColor;

void main() {
    vec2 mouseViewportPos = xy / viewportSize;
    if(texture(metadataSampler, mouseViewportPos).a != 0){
        finalColor = vec4(texture(metadataSampler, mouseViewportPos).a, 0, 0, 1);
    }else{
        finalColor = vec4(0);
    }
}