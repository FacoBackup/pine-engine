in vec2 texCoords;

layout (binding = 0) uniform sampler2D outlineSampler;

uniform float width;
uniform vec3 color;

#include "./buffer_objects/GLOBAL_DATA_UBO.glsl"
out vec4 finalColor;

void main(){
    vec2 size = width / bufferResolution;
    float center = texture(outlineSampler, texCoords).r;
    float left = texture(outlineSampler, texCoords + vec2(-1., 0.) * size).r;
    float right = texture(outlineSampler, texCoords + vec2(1., 0.) * size).r;
    float top = texture(outlineSampler, texCoords + vec2(0., -1.) * size).r;
    float bottom = texture(outlineSampler, texCoords + vec2(0., 1.) * size).r;
    if (left != center || right != center || top != center || bottom != center) {
        finalColor = vec4(color, 1.);
    }
    else discard;
}