in vec2 texCoords;

uniform sampler2D outlineSampler;

#include "./buffer_objects/CAMERA_VIEW_INFO.glsl"
out vec4 finalColor;

void main(){
    vec2 size = 2. / bufferResolution;
    float center = texture(outlineSampler, texCoords).r;
    float left = texture(outlineSampler, texCoords + vec2(-1., 0.) * size).r;
    float right = texture(outlineSampler, texCoords + vec2(1., 0.) * size).r;
    float top = texture(outlineSampler, texCoords + vec2(0., -1.) * size).r;
    float bottom = texture(outlineSampler, texCoords + vec2(0., 1.) * size).r;
    if (left != center || right != center || top != center || bottom != center) {
        finalColor = vec4(1., .35, 0., 1.);
    }
    else discard;
}