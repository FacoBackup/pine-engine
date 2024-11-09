#include "./buffer_objects/GLOBAL_DATA_UBO.glsl"

in vec2 texCoords;
out vec4 finalColor;

layout(binding = 3) uniform sampler2D uDepthSampler;

#include "./ATMOSPHERE.glsl"

#include "./CLOUDS.glsl"

void main(){
    if (texture(uDepthSampler, texCoords).r != 0){
        discard;
    }
    vec4 clouds = computeClouds();
    vec4 sky = vec4(1);
    if(clouds.a < 1){
        sky = computeAtmpshere();
        clouds = vec4(clouds.rgb + (1.0f - clouds.a) * sky.rgb, 1);
    }
    finalColor = clouds;
}