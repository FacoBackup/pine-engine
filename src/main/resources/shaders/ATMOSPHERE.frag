#include "../buffer_objects/GLOBAL_DATA_UBO.glsl"

in vec2 texCoords;
out vec4 finalColor;

vec3 createRay() {
    vec2 pxNDS = texCoords * 2. - 1.;
    vec3 pointNDS = vec3(pxNDS, -1.);
    vec4 pointNDSH = vec4(pointNDS, 1.0);
    vec4 dirEye = invProjectionMatrix * pointNDSH;
    dirEye.w = 0.;
    vec3 dirWorld = (invViewMatrix * dirEye).xyz;
    return normalize(dirWorld);
}

#include "./ATMOSPHERE.glsl"

#include "./CLOUDS.glsl"

void main(){
    vec3 rayDir = createRay();
    vec4 clouds = computeClouds(rayDir);
    if(clouds.a < 1){
        vec3 sky = computeAtmpshere(rayDir);
        clouds = vec4(clouds.rgb + (1.0f - clouds.a) * sky.rgb, 1);
    }
    finalColor = clouds;
}