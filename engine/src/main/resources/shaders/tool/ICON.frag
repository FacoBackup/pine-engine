#define IMAGE_QUANTITY 7.

in vec2 texCoords;
layout (binding = 0) uniform sampler2D iconSampler;
layout (binding = 1) uniform sampler2D sceneDepth;
uniform vec3 iconColor;
uniform float imageIndex;
uniform int renderIndex;
uniform bool isSelected;

layout (location = 0) out vec4 gBufferAlbedoSampler;
layout (location = 1) out vec4 gBufferNormalSampler;
layout (location = 2) out vec4 gBufferRMAOSampler;
layout (location = 3) out vec4 gBufferMaterialSampler;
layout (location = 4) out vec4 gBufferDepthSampler;
layout (location = 5) out vec4 gBufferIndirect;

#include "../util/SCENE_DEPTH_UTILS.glsl"

#include "../uber/G_BUFFER_UTIL.glsl"

void main() {
    vec2 imageSize = vec2(textureSize(iconSampler, 0));
    float color = texture(iconSampler, vec2(texCoords.x / IMAGE_QUANTITY + imageIndex * imageSize.y / imageSize.x, 1. - texCoords.y)).a;
    gBufferDepthSampler = vec4(encode(logDepthFC), renderIndex + 1, 1, 1);
    if (color <= .1) discard;

    gBufferNormalSampler = vec4(0);
    gBufferRMAOSampler = vec4(0);
    gBufferIndirect = vec4(0);
    gBufferMaterialSampler = vec4(0);

    if (isSelected){
        gBufferAlbedoSampler = vec4(1., .5, 0., 1.);
    } else {
        gBufferAlbedoSampler = vec4(iconColor, 1.);
        vec2 quadUV = gl_FragCoord.xy / bufferResolution;
        float currentDepth = getLogDepth(quadUV);
        if (currentDepth > 0. && currentDepth < gl_FragCoord.z){
            gBufferAlbedoSampler.rgb *= .5;
        }
    }
}